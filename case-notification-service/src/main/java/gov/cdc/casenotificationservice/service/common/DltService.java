package gov.cdc.casenotificationservice.service.common;

import static gov.cdc.casenotificationservice.util.TimeStampHelper.getCurrentTimeStamp;

import com.google.gson.Gson;
import gov.cdc.casenotificationservice.exception.DltServiceException;
import gov.cdc.casenotificationservice.kafka.producer.CaseNotificationProducer;
import gov.cdc.casenotificationservice.model.*;
import gov.cdc.casenotificationservice.repository.msg.CaseNotificationDltRepository;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationDlt;
import gov.cdc.casenotificationservice.repository.odse.CNTransportQOutRepository;
import gov.cdc.casenotificationservice.repository.odse.model.CNTransportqOut;
import gov.cdc.casenotificationservice.service.common.interfaces.IDltService;
import java.sql.Timestamp;
import java.util.Optional;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class DltService implements IDltService {
  @Value("${service.timezone}")
  private String tz = "UTC";

  @Value("${spring.kafka.topic.cn-transportq-out-topic}")
  private String transportOutQTopic;

  private final CaseNotificationDltRepository caseNotificationDltRepository;
  private final CNTransportQOutRepository cnTransportqOutRepository;
  private final CaseNotificationProducer caseNotificationProducer;

  public DltService(
      CaseNotificationDltRepository caseNotificationDltRepository,
      CNTransportQOutRepository cnTransportqOutRepository,
      CaseNotificationProducer caseNotificationProducer) {
    this.caseNotificationDltRepository = caseNotificationDltRepository;
    this.cnTransportqOutRepository = cnTransportqOutRepository;
    this.caseNotificationProducer = caseNotificationProducer;
  }

  public CaseNotificationDlt getDlt(String uuid) {
    var res = caseNotificationDltRepository.findById(UUID.fromString(uuid));
    return res.orElse(null);
  }

  /**
   * Save given message to the `NBS_MSGOUTE.case_notification_dlt` table and update its status in
   * `NBS_ODSE.CN_transportq_out`.
   */
  public void creatingDlt(String message, String topic, String stacktrace, String origin) {
    var gson = new Gson();
    CnTransportqOutMessage data = gson.fromJson(message, CnTransportqOutMessage.class);

    // ensure proper data shape for message
    if (data == null || data.getPayload() == null || data.getPayload().getAfter() == null) {
      log.error("Invalid data found in message: {}", message);
      throw new RuntimeException("Invalid data found in message: " + message);
    }

    // get the existing entry in `odse.cn_transportq_out` table
    CNTransportqOut cnTransportqOut =
        cnTransportqOutRepository.findTopByRecordUid(
            data.getPayload().getAfter().getCn_transportq_out_uid());

    // create a CaseNotificationDlt entry
    CaseNotificationDlt cnDlt = new CaseNotificationDlt();
    cnDlt.setCnTranportqOutUid(data.getPayload().getAfter().getCn_transportq_out_uid());
    cnDlt.setOriginalPayload(
        cnTransportqOut == null ? message : cnTransportqOut.getMessagePayload());
    cnDlt.setSource(origin);
    cnDlt.setErrorStackTrace(stacktrace);
    cnDlt.setCreatedOn(getCurrentTimeStamp(tz));
    cnDlt.setUpdatedOn(getCurrentTimeStamp(tz));

    // save CaseNotificationDlt entry and update status on the odse.cn_transportq_out entry
    caseNotificationDltRepository.save(cnDlt);
    cnTransportqOutRepository.updateStatus(
        data.getPayload().getAfter().getCn_transportq_out_uid(), "PROCESSING_ERROR");
  }

  public Page<CaseNotificationDlt> getDltsBetweenWithPagination(
      Timestamp from, Timestamp to, int page, int size) {
    Pageable pageable = PageRequest.of(page, size); // page = 0-indexed
    return caseNotificationDltRepository.findByCreatedOnBetween(from, to, pageable);
  }

  public ApiDltResponseModel<CaseNotificationDlt> getDltByUid(String uuid)
      throws DltServiceException {
    var dltResult = caseNotificationDltRepository.findById(UUID.fromString(uuid));
    if (dltResult.isEmpty()) {
      throw new DltServiceException("No DLT Found for Id " + uuid);
    }
    CaseNotificationDlt dlt = dltResult.get();

    var apiResponse = new ApiDltResponseModel<CaseNotificationDlt>();
    apiResponse.setPayload(dlt);
    apiResponse.setCaseNotificationDlt(dltResult.get());
    return apiResponse;
  }

  /**
   * Given the UUID of an entry in the DLT table, retrieve the information and send it to Kafka for
   * re-processing. Optionally, replace the data's payload with a passed in payload String.
   */
  public void reprocessingCaseNotification(String payload, String uuid) throws DltServiceException {
    Optional<CaseNotificationDlt> dltResult =
        caseNotificationDltRepository.findById(UUID.fromString(uuid));
    if (dltResult.isEmpty()) {
      throw new DltServiceException("No DLT Found for Id " + uuid);
    }

    CaseNotificationDlt caseNotificationDlt = dltResult.get();
    CNTransportqOut cnTransportqOut =
        cnTransportqOutRepository.findTopByRecordUid(caseNotificationDlt.getCnTranportqOutUid());

    // update the CNTransportqOut payload if there is a new payload
    if (!Strings.isBlank(payload)) {
      cnTransportqOut.setMessagePayload(payload);
      cnTransportqOutRepository.save(cnTransportqOut);
    }

    // convert found data into the proper messaging format and send
    CnTransportqOutMessage cnTransportqOutMessage = new CnTransportqOutMessage();
    EnvelopePayload envelopePayload = new EnvelopePayload();
    CnTransportqOutValue cnTransportqOutValue =
        CnTransportqOutValue.fromCNTransportqOut(cnTransportqOut);
    cnTransportqOutValue.setRecord_status_cd("UNPROCESSED");
    envelopePayload.setAfter(cnTransportqOutValue);
    cnTransportqOutMessage.setPayload(envelopePayload);

    caseNotificationProducer.sendMessage(
        new Gson().toJson(cnTransportqOutMessage), transportOutQTopic);

    // mark as reprocessed in the DLT table
    caseNotificationDlt.setDltStatus("REPROCESSED");
    caseNotificationDlt.setUpdatedOn(getCurrentTimeStamp(tz));
    caseNotificationDltRepository.save(caseNotificationDlt);
  }
}
