package gov.cdc.casenotificationservice.service.common;

import gov.cdc.casenotificationservice.repository.msg.CaseNotificationConfigRepository;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import gov.cdc.casenotificationservice.service.common.interfaces.IConfigurationService;
import org.springframework.stereotype.Service;

@Service
public class ConfigurationService implements IConfigurationService {
    private final CaseNotificationConfigRepository caseNotificationConfigRepository;

    public ConfigurationService(CaseNotificationConfigRepository caseNotificationConfigRepository) {
        this.caseNotificationConfigRepository = caseNotificationConfigRepository;
    }

    public boolean checkConfigurationAvailable() {
        return caseNotificationConfigRepository.isConfigApplied();
    }

    public void updateConfiguration(Integer id) {
        if (id == null) {
            caseNotificationConfigRepository.updateTopNonStdConfigToApplied();
        }
        else {
            caseNotificationConfigRepository.updateConfigAppliedById(id);
        }
    }

    public CaseNotificationConfig getAppliedCaseNotificationConfig() {
        return caseNotificationConfigRepository.findNonStdConfig();
    }



}
