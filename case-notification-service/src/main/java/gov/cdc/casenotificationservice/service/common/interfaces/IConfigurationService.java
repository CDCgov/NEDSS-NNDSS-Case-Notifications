package gov.cdc.casenotificationservice.service.common.interfaces;

import gov.cdc.casenotificationservice.model.dto.CaseNotificationConfigDto;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;

import java.util.List;

public interface IConfigurationService {
    boolean checkConfigurationAvailable();
    void updateConfiguration(Integer id, boolean configApplied);
    CaseNotificationConfig getAppliedCaseNotificationConfig();
    boolean checkHl7ValidationApplied();
    CaseNotificationConfig saveConfig(CaseNotificationConfigDto configDto);
    List<CaseNotificationConfig> getConfigs(String configName);
}
