package gov.cdc.casenotificationservice.service.deadletter.interfaces;

import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationDlt;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;

public interface IDltService {
    void creatingDlt( String message,String topic,String stacktrace,String errorMessage,String exceptionRoot);
    Page<CaseNotificationDlt> getDltsBetweenWithPagination(Timestamp from, Timestamp to, int page, int size);
}
