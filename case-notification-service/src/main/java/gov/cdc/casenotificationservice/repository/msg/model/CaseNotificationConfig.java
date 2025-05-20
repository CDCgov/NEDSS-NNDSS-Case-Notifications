package gov.cdc.casenotificationservice.repository.msg.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "NBS_Case_Notification_Config")
public class CaseNotificationConfig {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "config_name", nullable = false, unique = true, length = 255)
    private String configName;

    @Column(name = "config_applied")
    private Boolean configApplied;

    @Column(name = "batch_mesage_profile_id", length = 255)
    private String batchMesageProfileId;

    @Column(name = "nbs_certificate_url", length = 255)
    private String nbsCertificateUrl;

    @Column(name = "phin_encryption", length = 255)
    private String phinEncryption;

    @Column(name = "phin_route", length = 255)
    private String phinRoute;

    @Column(name = "phin_signature", length = 255)
    private String phinSignature;

    @Column(name = "phin_public_key_address", length = 255)
    private String phinPublicKeyAddress;

    @Column(name = "phin_public_key_base_dn", length = 255)
    private String phinPublicKeyBaseDn;

    @Column(name = "phin_public_key_dn", length = 255)
    private String phinPublicKeyDn;

    @Column(name = "phin_recipient", length = 255)
    private String phinRecipient;

    @Column(name = "phin_priority", length = 255)
    private String phinPriority;

    @Column(name= "hl7_validation_applied")
    private Boolean hl7ValidationApplied;

}