package gov.cdc.casenotificationservice.model.view_model;

import gov.cdc.casenotificationservice.repository.odse.model.CNTransportqOut;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class CnTransportOutVM {
    private Long cnTransportqOutUid;
    private String addReasonCd;
    private Timestamp addTime;
    private Long addUserId;
    private String lastChgReasonCd;
    private Timestamp lastChgTime;
    private Long lastChgUserId;
    private Long notificationUid;
    private String notificationLocalId;
    private String publicHealthCaseLocalId;
    private String reportStatusCd;
    private String recordStatusCd;
    private Timestamp recordStatusTime;
    private Short versionCtrlNbr;

    public CnTransportOutVM() {

    }

    public CnTransportOutVM(CNTransportqOut cnTransportqOut) {
        this.cnTransportqOutUid = cnTransportqOut.getCnTransportqOutUid();
        this.addReasonCd = cnTransportqOut.getAddReasonCd();
        this.addTime = cnTransportqOut.getAddTime();
        this.addUserId = cnTransportqOut.getAddUserId();
        this.lastChgReasonCd = cnTransportqOut.getLastChgReasonCd();
        this.lastChgTime = cnTransportqOut.getLastChgTime();
        this.lastChgUserId = cnTransportqOut.getLastChgUserId();
        this.notificationUid = cnTransportqOut.getNotificationUid();
        this.notificationLocalId = cnTransportqOut.getNotificationLocalId();
        this.publicHealthCaseLocalId = cnTransportqOut.getPublicHealthCaseLocalId();
        this.reportStatusCd = cnTransportqOut.getReportStatusCd();
        this.recordStatusCd = cnTransportqOut.getRecordStatusCd();
        this.recordStatusTime = cnTransportqOut.getRecordStatusTime();
        this.versionCtrlNbr = cnTransportqOut.getVersionCtrlNbr();
    }

}
