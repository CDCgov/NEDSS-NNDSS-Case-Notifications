-- ============================================================
-- Performance Test: Reset test data for re-runs
-- ============================================================

-- Reset CN_transportq_out rows back to UNPROCESSED
USE NBS_ODSE;
DELETE FROM CN_transportq_out
WHERE notification_local_id LIKE 'NOT-PERF-%';

PRINT CONCAT('Reset ', @@ROWCOUNT, ' rows in CN_transportq_out');
GO

-- Delete output rows from previous runs
USE NBS_MSGOUTE;
DELETE FROM TransportQ_out
WHERE messageId LIKE 'NOT-PERF-%';

PRINT CONCAT('Deleted ', @@ROWCOUNT, ' rows from TransportQ_out');
GO