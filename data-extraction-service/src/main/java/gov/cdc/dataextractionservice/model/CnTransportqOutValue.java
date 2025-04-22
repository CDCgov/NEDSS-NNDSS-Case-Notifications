package gov.cdc.dataextractionservice.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CnTransportqOutValue {
    public long cn_transportq_out_uid;
    public String add_reason_cd;
    public Long add_time;
    public Long add_user_id;
    public String last_chg_reason_cd;
    public Long last_chg_time;
    public Long last_chg_user_id;
    public String message_payload;
    public long notification_uid;
    public String notification_local_id;
    public String public_health_case_local_id;
    public String report_status_cd;
    public String record_status_cd;
    public Long record_status_time;
    public Short version_ctrl_nbr;
}
