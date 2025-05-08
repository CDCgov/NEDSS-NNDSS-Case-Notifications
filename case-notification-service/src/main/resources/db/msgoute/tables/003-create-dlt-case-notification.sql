IF
NOT EXISTS(
        SELECT 'X'
        FROM INFORMATION_SCHEMA.TABLES
        WHERE TABLE_NAME = 'case_notification_dlt')
BEGIN
CREATE TABLE case_notification_dlt
(
    id                UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
    cn_tranportq_out_uid    bigint NOT NULL,
    original_payload NVARCHAR(MAX),
    source    NVARCHAR(255) ,
    error_stack_trace       NVARCHAR(MAX),
    error_stack_trace_short NVARCHAR(MAX),
    dlt_status              NVARCHAR(10),
    dlt_occurrence          INT,
    created_on              DATETIME DEFAULT getdate(),
    updated_on              DATETIME DEFAULT getdate(),
);
END
