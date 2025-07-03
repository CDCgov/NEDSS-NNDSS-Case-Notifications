package gov.cdc.casenotificationservice.service.common;

import com.google.gson.Gson;
import gov.cdc.casenotificationservice.exception.DltServiceException;
import gov.cdc.casenotificationservice.kafka.producer.CaseNotificationProducer;
import gov.cdc.casenotificationservice.model.ApiDltResponseModel;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.repository.msg.CaseNotificationDltRepository;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationDlt;
import gov.cdc.casenotificationservice.repository.odse.CNTraportqOutRepository;
import gov.cdc.casenotificationservice.service.common.interfaces.IDltService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.UUID;

import static gov.cdc.casenotificationservice.util.TimeStampHelper.getCurrentTimeStamp;

@Service
public class DltService implements IDltService {
    @Value("${service.timezone}")
    private String tz = "UTC";

    @Value("${spring.kafka.topic.non-std-topic}")
    private String nonStdTopic;

    @Value("${spring.kafka.topic.std-topic}")
    private String stdTopic;

    private final CaseNotificationDltRepository caseNotificationDltRepository;
    private final CNTraportqOutRepository cnTraportqOutRepository;
    private final CaseNotificationProducer caseNotificationProducer;

    public DltService(CaseNotificationDltRepository caseNotificationDltRepository,
                      CNTraportqOutRepository cnTraportqOutRepository,
                      CaseNotificationProducer caseNotificationProducer) {
        this.caseNotificationDltRepository = caseNotificationDltRepository;
        this.cnTraportqOutRepository = cnTraportqOutRepository;
        this.caseNotificationProducer = caseNotificationProducer;
    }

    public CaseNotificationDlt getDlt(String uuid) {
        var res = caseNotificationDltRepository.findById(UUID.fromString(uuid));
        return res.orElse(null);
    }

    public void creatingDlt( String message,String topic,String stacktrace, String origin) {
        var gson = new Gson();
        String status;
        MessageAfterStdChecker data = null;
        try {
            if (message == null || message.trim().isEmpty()) {
                status = "INVALID_ERR";
            } else {
                data = gson.fromJson(message, MessageAfterStdChecker.class);
                if (data == null || data.getCnTransportqOutUid() == null) {
                    status = "INVALID_ERR";
                } else if (Boolean.TRUE.equals(data.isStdMessageDetected())) {
                    status = "STD_ERR";
                } else if (Boolean.FALSE.equals(data.isStdMessageDetected())) {
                    status = "NONSTD_ERR";
                } else {
                    status = "UNKNOWN_ERR";
                }
            }
        } catch (Exception e) {
            status = "INVALID_ERR";
        }

        var cnTransportqOut = (data != null && data.getCnTransportqOutUid() != null)
                ? cnTraportqOutRepository.findTopByRecordUid(data.getCnTransportqOutUid())
                : null;

        CaseNotificationDlt caseNotificationDlt = new CaseNotificationDlt();
        if (data != null) {
            caseNotificationDlt.setCnTranportqOutUid(data.getCnTransportqOutUid());
        }
        if (cnTransportqOut != null) {
            caseNotificationDlt.setOriginalPayload(cnTransportqOut.getMessagePayload());
        } else {
            caseNotificationDlt.setOriginalPayload(message);
        }
        caseNotificationDlt.setSource(origin);
        caseNotificationDlt.setErrorStackTrace(stacktrace);
        caseNotificationDlt.setCreatedOn(getCurrentTimeStamp(tz));
        caseNotificationDlt.setUpdatedOn(getCurrentTimeStamp(tz));

        caseNotificationDltRepository.save(caseNotificationDlt);

        if (data != null && data.getCnTransportqOutUid() != null) {
            cnTraportqOutRepository.updateStatus(data.getCnTransportqOutUid(), status);
        }
    }

    public Page<CaseNotificationDlt> getDltsBetweenWithPagination(Timestamp from, Timestamp to, int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // page = 0-indexed
        return caseNotificationDltRepository.findByCreatedOnBetween(from, to, pageable);
    }

    public ApiDltResponseModel<CaseNotificationDlt> getDltByUid(String uuid) throws DltServiceException {
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

    public void reprocessingCaseNotification(String payload, String uuid) throws DltServiceException {
        //push message back to queue and let it process then update this specific record to injected
        var dltResult = caseNotificationDltRepository.findById(UUID.fromString(uuid));
        if (dltResult.isEmpty()) {
            throw new DltServiceException("No DLT Found for Id " + uuid);
        }
        var caseNotificationDlt = dltResult.get();
        caseNotificationDlt.setDltStatus("REPROCESSED");
        caseNotificationDlt.setUpdatedOn(getCurrentTimeStamp(tz));
        caseNotificationDltRepository.save(caseNotificationDlt);

        var cnTransportqOut = cnTraportqOutRepository.findTopByRecordUid(dltResult.get().getCnTranportqOutUid());
        cnTransportqOut.setMessagePayload(payload); // UPDATE NEW PAYLOAD TO CN TRANSPORT
        cnTraportqOutRepository.save(cnTransportqOut);

        String topic;
        if (caseNotificationDlt.getSource().equalsIgnoreCase(nonStdTopic)) {
            topic = nonStdTopic;
        }
        else
        {
            topic = stdTopic;
        }
        caseNotificationProducer.sendMessage(uuid, topic);

    }
}
