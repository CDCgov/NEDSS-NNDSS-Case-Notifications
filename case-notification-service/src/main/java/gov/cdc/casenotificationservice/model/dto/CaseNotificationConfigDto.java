package gov.cdc.casenotificationservice.model.dto;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CaseNotificationConfigDto {
    private Integer id;
    private String configName;
    private Boolean configApplied;
    private String batchMesageProfileId;
    private String nbsCertificateUrl;
    private String phinEncryption;
    private String phinRoute;
    private String phinSignature;
    private String phinPublicKeyAddress;
    private String phinPublicKeyBaseDn;
    private String phinPublicKeyDn;
    private String phinRecipient;
    private String phinPriority;
    private Boolean hl7ValidationEnabled;

    public static CaseNotificationConfigDto fromEntity(CaseNotificationConfig entity) {
        return CaseNotificationConfigDto.builder()
                .id(entity.getId())
                .configName(entity.getConfigName())
                .configApplied(entity.getConfigApplied())
                .batchMesageProfileId(entity.getBatchMesageProfileId())
                .nbsCertificateUrl(entity.getNbsCertificateUrl())
                .phinEncryption(entity.getPhinEncryption())
                .phinRoute(entity.getPhinRoute())
                .phinSignature(entity.getPhinSignature())
                .phinPublicKeyAddress(entity.getPhinPublicKeyAddress())
                .phinPublicKeyBaseDn(entity.getPhinPublicKeyBaseDn())
                .phinPublicKeyDn(entity.getPhinPublicKeyDn())
                .phinRecipient(entity.getPhinRecipient())
                .phinPriority(entity.getPhinPriority())
                .hl7ValidationEnabled(entity.getHl7ValidationEnabled())
                .build();
    }
}
