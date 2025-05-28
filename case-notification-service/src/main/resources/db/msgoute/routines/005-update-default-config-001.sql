Delete from NBS_Case_Notification_Config where config_name = 'NON_STD_CASE_NOTIFICATION';

IF
NOT EXISTS (SELECT 1 FROM [dbo].[NBS_Case_Notification_Config] WHERE config_name = 'NON_STD_CASE_NOTIFICATION')
BEGIN
INSERT INTO NBS_Case_Notification_Config (
    config_name,
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
    phin_priority,
    hl7_validation_enabled
)
VALUES (
           'NON_STD_CASE_NOTIFICATION',
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
           '1',
           '0'
       );
END;