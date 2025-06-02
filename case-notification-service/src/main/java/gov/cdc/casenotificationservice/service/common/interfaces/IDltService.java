package gov.cdc.casenotificationservice.service.common.interfaces;

import gov.cdc.casenotificationservice.exception.DltServiceException;
import gov.cdc.casenotificationservice.model.ApiDltResponseModel;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationDlt;
import org.springframework.data.domain.Page;

import java.sql.Timestamp;

public interface IDltService {
    void creatingDlt( String message,String topic,String stacktrace, String origin);
    Page<CaseNotificationDlt> getDltsBetweenWithPagination(Timestamp from, Timestamp to, int page, int size);
    void reprocessingCaseNotification(String payload, String uuid) throws DltServiceException;
    ApiDltResponseModel<CaseNotificationDlt> getDltByUid(String uuid) throws DltServiceException;
    CaseNotificationDlt getDlt(String uuid);
}
