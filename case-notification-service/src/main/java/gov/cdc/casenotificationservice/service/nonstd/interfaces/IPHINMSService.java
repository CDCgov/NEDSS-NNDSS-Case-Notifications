package gov.cdc.casenotificationservice.service.nonstd.interfaces;


import gov.cdc.casenotificationservice.model.PHINMSProperties;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;

public interface IPHINMSService {
    PHINMSProperties gettingPHIMNSProperties(
            String payload,
            PHINMSProperties PHINMSProperties,
            CaseNotificationConfig caseNotificationConfig) throws Exception;
}