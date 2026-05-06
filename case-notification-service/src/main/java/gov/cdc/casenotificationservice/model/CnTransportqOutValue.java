package gov.cdc.casenotificationservice.model;

import gov.cdc.casenotificationservice.repository.odse.model.CNTransportqOut;
import java.sql.Timestamp;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CnTransportqOutValue {
  public Long cn_transportq_out_uid;
  public String add_reason_cd;
  public Timestamp add_time;
  public Long add_user_id;
  public String last_chg_reason_cd;
  public Timestamp last_chg_time;
  public Long last_chg_user_id;
  public String message_payload;
  public Long notification_uid;
  public String notification_local_id;
  public String public_health_case_local_id;
  public String report_status_cd;
  public String record_status_cd;
  public Timestamp record_status_time;
  public Short version_ctrl_nbr;

  /**
   * Create a {@link CnTransportqOutValue} instance from the values in a {@link CNTransportqOut}
   * instance.
   */
  public static CnTransportqOutValue fromCNTransportqOut(CNTransportqOut from) {
    CnTransportqOutValue to = new CnTransportqOutValue();

    to.setCn_transportq_out_uid(from.getCnTransportqOutUid());
    to.setAdd_reason_cd(from.getAddReasonCd());
    to.setAdd_time(from.getAddTime());
    to.setAdd_user_id(from.getAddUserId());
    to.setLast_chg_reason_cd(from.getLastChgReasonCd());
    to.setLast_chg_time(from.getLastChgTime());
    to.setLast_chg_user_id(from.getLastChgUserId());
    to.setMessage_payload(from.getMessagePayload());
    to.setNotification_uid(from.getNotificationUid());
    to.setNotification_local_id(from.getNotificationLocalId());
    to.setPublic_health_case_local_id(from.getPublicHealthCaseLocalId());
    to.setReport_status_cd(from.getReportStatusCd());
    to.setRecord_status_cd(from.getRecordStatusCd());
    to.setRecord_status_time(from.getRecordStatusTime());
    to.setVersion_ctrl_nbr(from.getVersionCtrlNbr());

    return to;
  }
}
