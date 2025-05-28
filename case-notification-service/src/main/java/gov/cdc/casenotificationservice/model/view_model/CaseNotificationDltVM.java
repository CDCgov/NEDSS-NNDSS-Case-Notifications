package gov.cdc.casenotificationservice.model.view_model;

import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationDlt;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Getter
@Setter
public class CaseNotificationDltVM {
    private UUID id;
    private Long cnTranportqOutUid;
    private String source;
    private String errorStackTrace;
    private String dltStatus;
    private Integer dltOccurrence;
    private Timestamp createdOn;
    private Timestamp updatedOn;

    public CaseNotificationDltVM() {}
    public CaseNotificationDltVM(CaseNotificationDlt caseNotificationDlt) {
        this.id = caseNotificationDlt.getId();
        this.cnTranportqOutUid = caseNotificationDlt.getCnTranportqOutUid();
        this.source = caseNotificationDlt.getSource();
        this.errorStackTrace = caseNotificationDlt.getErrorStackTrace();
        this.dltStatus = caseNotificationDlt.getDltStatus();
        this.dltOccurrence = caseNotificationDlt.getDltOccurrence();
        this.createdOn = caseNotificationDlt.getCreatedOn();
        this.updatedOn = caseNotificationDlt.getUpdatedOn();
    }

}
