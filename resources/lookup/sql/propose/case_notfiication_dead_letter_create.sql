CREATE TABLE case_notification_dead_letter (
                                               id UNIQUEIDENTIFIER PRIMARY KEY DEFAULT NEWID(),
                                               pipeline VARCHAR(50) NOT NULL,
                                               origin VARCHAR(255) NOT NULL,           -- source of the error
                                               payload NVARCHAR(MAX) NOT NULL,         -- JSON payload
                                               stacktrace NVARCHAR(MAX),               -- full stack trace
                                               status VARCHAR(50) NOT NULL             -- status: error, success, etc.
);
