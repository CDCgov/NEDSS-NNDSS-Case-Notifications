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

INSERT INTO NBS_Case_Notification_Config (
    config_name,
    netss_message_only,
    batch_mesage_profile_id,
    nbs_certificate_url,
    config_applied,
    phin_encryption,
    phin_route,
    phin_signature,
    phin_public_key_address,
    phin_public_key_base_dn,
    phin_public_key_dn,
    phin_recipient,
    phin_priority
)
VALUES (
           'NON_STD_CASE_NOTIFICATION',
           'queued',
           'NONE',
           'TEST_URL',
           1,
           'YES',
           'CDC',
           'no',
           'directory.pki.digicert.com:389',
           'o=Centers for Disease Control and Prevention',
           'cn=cdc phinms',
           'CDC',
           '1'
       );

