package gov.cdc.casenotificationservice.model;

import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationDlt;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApiDltResponseModel<T> {
    private CaseNotificationDlt caseNotificationDlt;
    private T payload;
}
