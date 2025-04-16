package gov.cdc.casenotificationservice.repository.odse.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CN_transportq_out", schema = "dbo")
public class CNTransportqOut {

    @Id
    @Column(name = "cn_transportq_out_uid")
    private Long cnTransportqOutUid;

    @Column(name = "add_reason_cd", length = 20)
    private String addReasonCd;

    @Column(name = "add_time")
    private Timestamp addTime;

    @Column(name = "add_user_id")
    private Long addUserId;

    @Column(name = "last_chg_reason_cd", length = 20)
    private String lastChgReasonCd;

    @Column(name = "last_chg_time")
    private Timestamp lastChgTime;

    @Column(name = "last_chg_user_id")
    private Long lastChgUserId;

    @Column(name = "message_payload")
    private String messagePayload;

    @Column(name = "notification_uid", nullable = false)
    private Long notificationUid;

    @Column(name = "notification_local_id", length = 50)
    private String notificationLocalId;

    @Column(name = "public_health_case_local_id", length = 50)
    private String publicHealthCaseLocalId;

    @Column(name = "report_status_cd", length = 20)
    private String reportStatusCd;

    @Column(name = "record_status_cd", length = 20)
    private String recordStatusCd;

    @Column(name = "record_status_time")
    private Timestamp recordStatusTime;

    @Column(name = "version_ctrl_nbr")
    private Short versionCtrlNbr;

}