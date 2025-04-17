CREATE TABLE SERVICE_ACTION_PAIR (
                                     SERVICE varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
    [ACTION] varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
    TOTAL_SERVICE_ACTION_PAIRS int NULL,
    SERIAL_NUMBER int NULL,
    MESSAGE_PROFILE_ID varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
    CONDITION_CODE int NULL,
    STATUS_CODE varchar(50) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
    NOTES varchar(256) COLLATE SQL_Latin1_General_CP1_CI_AS NULL,
    CONCEPT_CODE int NULL
    );