CREATE TABLE case_notification_config (
                                          id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
                                          profile_name VARCHAR(255) UNIQUE NOT NULL,
                                          config NVARCHAR(MAX) -- assuming MESSAGE_PROFILE_ID and CERTIFICATE_URL as JSON
);