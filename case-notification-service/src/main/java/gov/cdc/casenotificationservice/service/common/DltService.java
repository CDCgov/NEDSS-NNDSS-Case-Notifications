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

    public void creatingDlt( String message,String topic,String stacktrace,String errorMessage,String exceptionRoot) {
        var gson = new Gson();
        MessageAfterStdChecker data = gson.fromJson(message, MessageAfterStdChecker.class);
        var cnTransportqOut = cnTraportqOutRepository.findTopByRecordUid(data.getCnTransportqOutUid());

        CaseNotificationDlt caseNotificationDlt = new CaseNotificationDlt();
        caseNotificationDlt.setCnTranportqOutUid(data.getCnTransportqOutUid());
        caseNotificationDlt.setOriginalPayload(cnTransportqOut.getMessagePayload());
        caseNotificationDlt.setSource(topic);
        caseNotificationDlt.setErrorStackTrace(stacktrace);
        caseNotificationDlt.setCreatedOn(getCurrentTimeStamp(tz));
        caseNotificationDlt.setUpdatedOn(getCurrentTimeStamp(tz));

        caseNotificationDltRepository.save(caseNotificationDlt);
    }

    public Page<CaseNotificationDlt> getDltsBetweenWithPagination(Timestamp from, Timestamp to, int page, int size) {
        Pageable pageable = PageRequest.of(page, size); // page = 0-indexed
        return caseNotificationDltRepository.findByCreatedOnBetween(from, to, pageable);
    }

    public ApiDltResponseModel<MessageAfterStdChecker> getDltByUid(String uuid) throws DltServiceException {
        var dltResult = caseNotificationDltRepository.findById(UUID.fromString(uuid));
        if (dltResult.isEmpty()) {
            throw new DltServiceException("No DLT Found for Id " + uuid);
        }
        Gson gson = new Gson();
        var messageAfterStdChecker = gson.fromJson(dltResult.get().getOriginalPayload(), MessageAfterStdChecker.class);
        var apiResponse = new ApiDltResponseModel<MessageAfterStdChecker>();
        apiResponse.setPayload(messageAfterStdChecker);
        // dont need to return ori payload here
        dltResult.get().setOriginalPayload("");
        apiResponse.setCaseNotificationDlt(dltResult.get());
        return apiResponse;
    }

    public void reprocessingCaseNotification(String payload, String uuid) throws DltServiceException {
        //push message back to queue and let it process then update this specific record to injected
        var dltResult = caseNotificationDltRepository.findById(UUID.fromString(uuid));
        if (dltResult.isEmpty()) {
            throw new DltServiceException("No DLT Found for Id " + uuid);
        }
        Gson gson = new Gson();
        var caseNotificationDlt = dltResult.get();
        caseNotificationDlt.setDltStatus("REINJECTED");
        caseNotificationDlt.setUpdatedOn(getCurrentTimeStamp(tz));
        caseNotificationDltRepository.save(caseNotificationDlt);

        MessageAfterStdChecker data = gson.fromJson(payload, MessageAfterStdChecker.class);
        data.setDeadLetterUid(uuid);
        String topic;
        if (caseNotificationDlt.getSource().equalsIgnoreCase(nonStdTopic)) {
            topic = nonStdTopic;
        }
        else
        {
            topic = stdTopic;
        }
        caseNotificationProducer.sendMessage(payload, topic);

    }
}
