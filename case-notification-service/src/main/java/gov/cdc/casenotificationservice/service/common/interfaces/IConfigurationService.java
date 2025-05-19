package gov.cdc.casenotificationservice.service.common.interfaces;

import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;

public interface IConfigurationService {
    boolean checkConfigurationAvailable();
    void updateConfiguration(Integer id);
    CaseNotificationConfig getAppliedCaseNotificationConfig();
}
