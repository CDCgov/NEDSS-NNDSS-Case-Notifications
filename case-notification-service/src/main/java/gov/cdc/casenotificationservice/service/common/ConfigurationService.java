package gov.cdc.casenotificationservice.service.common;

import gov.cdc.casenotificationservice.exception.APIException;
import gov.cdc.casenotificationservice.model.dto.CaseNotificationConfigDto;
import gov.cdc.casenotificationservice.repository.msg.CaseNotificationConfigRepository;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import gov.cdc.casenotificationservice.service.common.interfaces.IConfigurationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static gov.cdc.casenotificationservice.util.TimeStampHelper.getCurrentTimeStamp;

@Service
public class ConfigurationService implements IConfigurationService {
    private final CaseNotificationConfigRepository caseNotificationConfigRepository;

    public ConfigurationService(CaseNotificationConfigRepository caseNotificationConfigRepository) {
        this.caseNotificationConfigRepository = caseNotificationConfigRepository;
    }

    public boolean checkConfigurationAvailable() {
        return caseNotificationConfigRepository.isConfigApplied();
    }

    public boolean checkHl7ValidationApplied() {
        return caseNotificationConfigRepository.isHl7ValidationApplied();
    }


    @Transactional(transactionManager = "msgTransactionManager")
    public void updateConfiguration(Integer id, boolean configApplied) {
        try {
            var status = 0;
            if (configApplied) {
                status = 1;
            }

            if (id == null) {
                caseNotificationConfigRepository.updateTopNonStdConfigToApplied(status);
            }
            else {
                caseNotificationConfigRepository.updateConfigAppliedById(id, status);
            }
        }catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

    }

    public CaseNotificationConfig getAppliedCaseNotificationConfig() {
        return caseNotificationConfigRepository.findNonStdConfig();
    }


    public CaseNotificationConfig saveConfig(CaseNotificationConfigDto configDto)  {
        CaseNotificationConfig config = caseNotificationConfigRepository.findConfigByName(configDto.getConfigName());

        if (config != null) {
            config.setConfigApplied(configDto.getConfigApplied());
            config.setBatchMesageProfileId(configDto.getBatchMesageProfileId());
            config.setNbsCertificateUrl(configDto.getNbsCertificateUrl());
            config.setPhinEncryption(configDto.getPhinEncryption());
            config.setPhinRoute(configDto.getPhinRoute());
            config.setPhinSignature(configDto.getPhinSignature());
            config.setPhinPublicKeyAddress(configDto.getPhinPublicKeyAddress());
            config.setPhinPublicKeyBaseDn(configDto.getPhinPublicKeyBaseDn());
            config.setPhinPublicKeyDn(configDto.getPhinPublicKeyDn());
            config.setPhinRecipient(configDto.getPhinRecipient());
            config.setPhinPriority(configDto.getPhinPriority());
            config.setHl7ValidationEnabled(configDto.getHl7ValidationEnabled());

        }
        else {
            config = new CaseNotificationConfig(configDto);
        }

        var configReturn = caseNotificationConfigRepository.save(config);
        return configReturn;
    }


    public List<CaseNotificationConfig> getConfigs(String configName) {
        List<CaseNotificationConfig> dataViewConfigList = new ArrayList<>();
        if (configName != null && !configName.isEmpty()) {
            var dataViewConfig = caseNotificationConfigRepository.findConfigByName(configName);
            dataViewConfigList.add(dataViewConfig);
        }
        else {
            dataViewConfigList = caseNotificationConfigRepository.findAll();
        }

        return dataViewConfigList;
    }


}
