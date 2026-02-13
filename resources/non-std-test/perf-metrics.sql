-- ============================================================
-- Performance Test: Metrics Report
-- Run after push-kafka-messages.sh completes processing
-- ============================================================

-- Status breakdown
SELECT
    record_status_cd AS status,
    COUNT(*) AS count
FROM NBS_ODSE.dbo.CN_transportq_out
WHERE notification_local_id LIKE 'NOT-PERF-%'
GROUP BY record_status_cd;
GO

-- ----- THROUGHPUT -----
SELECT
    COUNT(*) AS total_processed,
    MIN(messageCreationTime) AS first_output,
    MAX(messageCreationTime) AS last_output,
    DATEDIFF(SECOND,
            CAST(MIN(messageCreationTime) AS DATETIME2),
            CAST(MAX(messageCreationTime) AS DATETIME2)
    ) AS processing_window_seconds,
    CASE
        WHEN DATEDIFF(SECOND,
                     CAST(MIN(messageCreationTime) AS DATETIME2),
                     CAST(MAX(messageCreationTime) AS DATETIME2)
             ) > 0
            THEN CAST(COUNT(*) AS FLOAT) / DATEDIFF(SECOND,
                CAST(MIN(messageCreationTime) AS DATETIME2),
                CAST(MAX(messageCreationTime) AS DATETIME2)
                                           )
        ELSE COUNT(*)
        END AS messages_per_second
FROM NBS_MSGOUTE.dbo.TransportQ_out
WHERE messageId LIKE 'NOT-PERF-%';
GO

-- ----- DLT ERROR DETAILS -----
SELECT
    dlt.dlt_status,
    dlt.source AS kafka_topic,
    COUNT(*) AS count,
    MIN(dlt.created_on) AS first_error,
    MAX(dlt.created_on) AS last_error
FROM NBS_MSGOUTE.dbo.case_notification_dlt dlt
    INNER JOIN NBS_ODSE.dbo.CN_transportq_out cn
ON dlt.cn_tranportq_out_uid = cn.cn_transportq_out_uid
WHERE cn.notification_local_id LIKE 'NOT-PERF-%'
GROUP BY dlt.dlt_status, dlt.source;
GO