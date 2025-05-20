package gov.cdc.casenotificationservice.service.common;

import gov.cdc.casenotificationservice.repository.msg.CaseNotificationConfigRepository;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import gov.cdc.casenotificationservice.service.common.interfaces.IConfigurationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfigurationService implements IConfigurationService {
    private final CaseNotificationConfigRepository caseNotificationConfigRepository;

    public ConfigurationService(CaseNotificationConfigRepository caseNotificationConfigRepository) {
        this.caseNotificationConfigRepository = caseNotificationConfigRepository;
    }

    public boolean checkConfigurationAvailable() {
        return caseNotificationConfigRepository.isConfigApplied();
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



}
