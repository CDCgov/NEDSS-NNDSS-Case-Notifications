package gov.cdc.casenotificationservice.service.deadletter;

import com.google.gson.Gson;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.repository.msg.CaseNotificationDltRepository;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationDlt;
import gov.cdc.casenotificationservice.repository.odse.CNTraportqOutRepository;
import gov.cdc.casenotificationservice.service.deadletter.interfaces.IDltService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

import static gov.cdc.casenotificationservice.util.TimeStampHelper.getCurrentTimeStamp;

@Service
public class DltService implements IDltService {
    @Value("${service.timezone}")
    private String tz = "UTC";

    private final CaseNotificationDltRepository caseNotificationDltRepository;
    private final CNTraportqOutRepository cnTraportqOutRepository;

    public DltService(CaseNotificationDltRepository caseNotificationDltRepository, CNTraportqOutRepository cnTraportqOutRepository) {
        this.caseNotificationDltRepository = caseNotificationDltRepository;
        this.cnTraportqOutRepository = cnTraportqOutRepository;
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


    public void reprocessingCaseNotification(String payload) {
        //TODO logic for repocessing
        //push message back to queue and let it process then update this specific record to injected
    }
}
