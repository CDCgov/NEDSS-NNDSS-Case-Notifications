package gov.cdc.casenotificationservice.model.view_model;

import gov.cdc.casenotificationservice.repository.msg.model.NetssTransportQOut;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter
@Setter
public class NetssTransportQOutVM {
    private Long netssTransportQOutUid;

    private String recordTypeCd;

    private Short mmwrYear;

    private Short mmwrWeek;

    private String netssCaseId;

    private String phcLocalId;

    private String notificationLocalId;

    private Timestamp addTime;

    private String payload;

    private String recordStatusCd;

    public NetssTransportQOutVM() {

    }
    public NetssTransportQOutVM(NetssTransportQOut netssTransportQOut) {
        this.netssTransportQOutUid = netssTransportQOut.getNetssTransportQOutUid();
        this.recordTypeCd = netssTransportQOut.getRecordTypeCd();
        this.mmwrYear = netssTransportQOut.getMmwrYear();
        this.mmwrWeek = netssTransportQOut.getMmwrWeek();
        this.netssCaseId = netssTransportQOut.getNetssCaseId();
        this.phcLocalId = netssTransportQOut.getPhcLocalId();
        this.notificationLocalId = netssTransportQOut.getNotificationLocalId();
        this.addTime = netssTransportQOut.getAddTime();
        this.payload = netssTransportQOut.getPayload();
        this.recordStatusCd = netssTransportQOut.getRecordStatusCd();
    }

}
