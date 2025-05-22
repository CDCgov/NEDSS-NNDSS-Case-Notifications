
INSERT INTO CN_transportq_out (
    add_reason_cd,
    add_time,
    add_user_id,
    last_chg_reason_cd,
    last_chg_time,
    last_chg_user_id,
    message_payload,
    notification_uid,
    notification_local_id,
    public_health_case_local_id,
    report_status_cd,
    record_status_cd,
    record_status_time,
    version_ctrl_nbr
)
SELECT
    add_reason_cd,
    add_time,
    add_user_id,
    last_chg_reason_cd,
    last_chg_time,
    last_chg_user_id,
    message_payload,
    notification_uid,
    notification_local_id,
    public_health_case_local_id,
    report_status_cd,
    'UNPROCESSED',
    record_status_time,
    version_ctrl_nbr
FROM CN_transportq_out
WHERE cn_transportq_out_uid = 23332;
