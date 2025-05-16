package gov.cdc.casenotificationservice.repository.msg.model;


import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.util.UUID;

@Data
@Entity
@Table(name = "case_notification_dlt")
public class CaseNotificationDlt {

    @Id
    @GeneratedValue
    @Column(name = "id", columnDefinition = "uniqueidentifier")
    private UUID id;

    @Column(name = "cn_tranportq_out_uid", nullable = false)
    private Long cnTranportqOutUid;

    @Column(name = "original_payload", columnDefinition = "NVARCHAR(MAX)")
    private String originalPayload;

    @Column(name = "source", length = 255)
    private String source;

    @Column(name = "error_stack_trace", columnDefinition = "NVARCHAR(MAX)")
    private String errorStackTrace;


    @Column(name = "dlt_status", length = 10)
    private String dltStatus;

    @Column(name = "dlt_occurrence")
    private Integer dltOccurrence;

    @CreationTimestamp
    @Column(name = "created_on", updatable = false)
    private Timestamp createdOn;

    @UpdateTimestamp
    @Column(name = "updated_on")
    private Timestamp updatedOn;
}