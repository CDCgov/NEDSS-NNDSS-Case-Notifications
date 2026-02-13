-- ============================================================
-- Performance Test: Metrics Report
-- Run after push-kafka-messages.sh completes processing
-- ============================================================

-- ----- SUMMARY -----
PRINT '=== PROCESSING SUMMARY ===';
PRINT '';

-- Status breakdown
SELECT
    record_status_cd AS status,
    COUNT(*) AS count
FROM NBS_ODSE.dbo.CN_transportq_out
WHERE notification_local_id LIKE 'NOT-PERF-%'
GROUP BY record_status_cd;
GO

-- Success vs error counts
DECLARE @total INT, @succeeded INT, @failed INT, @pending INT;

SELECT @total = COUNT(*) FROM NBS_ODSE.dbo.CN_transportq_out
WHERE notification_local_id LIKE 'NOT-PERF-%';

SELECT @succeeded = COUNT(*) FROM NBS_MSGOUTE.dbo.TransportQ_out
WHERE messageId LIKE 'NOT-PERF-%';

SELECT @failed = COUNT(*) FROM NBS_MSGOUTE.dbo.case_notification_dlt dlt
                                   INNER JOIN NBS_ODSE.dbo.CN_transportq_out cn
                                              ON dlt.cn_tranportq_out_uid = cn.cn_transportq_out_uid
WHERE cn.notification_local_id LIKE 'NOT-PERF-%';

SET @pending = @total - @succeeded - @failed;

PRINT '';
PRINT '=== ERROR RATE ===';
PRINT CONCAT('Total messages:  ', @total);
PRINT CONCAT('Succeeded:       ', @succeeded);
PRINT CONCAT('Failed (DLT):    ', @failed);
PRINT CONCAT('Pending:         ', @pending);
PRINT CONCAT('Error rate:      ',
    CASE WHEN @total > 0
        THEN CAST(CAST(@failed AS FLOAT) / @total * 100 AS DECIMAL(5,2))
        ELSE 0
    END, '%');
GO

-- ----- THROUGHPUT -----
PRINT '';
PRINT '=== THROUGHPUT ===';

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

-- ----- END-TO-END LATENCY -----
PRINT '';
PRINT '=== END-TO-END LATENCY (per message) ===';

SELECT
    COUNT(*) AS total_messages,
    CAST(AVG(CAST(latency_ms AS FLOAT)) AS DECIMAL(10,1)) AS avg_latency_ms,
    MIN(latency_ms) AS min_latency_ms,
    MAX(latency_ms) AS max_latency_ms,
    CAST(STDEV(CAST(latency_ms AS FLOAT)) AS DECIMAL(10,1)) AS stddev_latency_ms
FROM (
         SELECT
             DATEDIFF(MILLISECOND, cn.add_time, CAST(tq.messageCreationTime AS DATETIME2)) AS latency_ms
         FROM NBS_MSGOUTE.dbo.TransportQ_out tq
                  INNER JOIN NBS_ODSE.dbo.CN_transportq_out cn
                             ON tq.messageId = cn.notification_local_id
         WHERE cn.notification_local_id LIKE 'NOT-PERF-%'
     ) latencies;
GO

-- Latency distribution (percentiles)
PRINT '';
PRINT '=== LATENCY PERCENTILES ===';

SELECT DISTINCT
    PERCENTILE_CONT(0.50) WITHIN GROUP (ORDER BY latency_ms) OVER () AS p50_ms,
    PERCENTILE_CONT(0.90) WITHIN GROUP (ORDER BY latency_ms) OVER () AS p90_ms,
    PERCENTILE_CONT(0.95) WITHIN GROUP (ORDER BY latency_ms) OVER () AS p95_ms,
    PERCENTILE_CONT(0.99) WITHIN GROUP (ORDER BY latency_ms) OVER () AS p99_ms
FROM (
    SELECT
    DATEDIFF(MILLISECOND, cn.add_time, CAST(tq.messageCreationTime AS DATETIME2)) AS latency_ms
    FROM NBS_MSGOUTE.dbo.TransportQ_out tq
    INNER JOIN NBS_ODSE.dbo.CN_transportq_out cn
    ON tq.messageId = cn.notification_local_id
    WHERE cn.notification_local_id LIKE 'NOT-PERF-%'
    ) latencies;
GO

-- ----- DLT ERROR DETAILS -----
PRINT '';
PRINT '=== DLT ERROR BREAKDOWN (if any) ===';

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