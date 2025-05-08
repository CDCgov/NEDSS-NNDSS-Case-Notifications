IF NOT EXISTS(
    SELECT 'X'
    FROM INFORMATION_SCHEMA.TABLES
    WHERE TABLE_NAME = 'NBS_Case_Notification_Config')
BEGIN
CREATE TABLE NBS_Case_Notification_Config (
                                              id INT IDENTITY(1,1) PRIMARY KEY,
                                              config_name VARCHAR(255) NOT NULL UNIQUE,
                                              config_applied BIT DEFAULT(0),
                                              netss_message_only VARCHAR(50) NULL,
                                              batch_mesage_profile_id VARCHAR(255) NULL,
                                              nbs_certificate_url VARCHAR(255) NULL,

                                              phin_encryption VARCHAR(255) NULL,
                                              phin_route VARCHAR(255) NULL,
                                              phin_signature VARCHAR(255) NULL,
                                              phin_public_key_address VARCHAR(255) NULL,
                                              phin_public_key_base_dn VARCHAR(255) NULL,
                                              phin_public_key_dn VARCHAR(255) NULL,
                                              phin_recipient VARCHAR(255) NULL,
                                              phin_priority VARCHAR(255) NULL



);
END
