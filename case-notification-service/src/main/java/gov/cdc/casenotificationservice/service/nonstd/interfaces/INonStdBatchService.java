package gov.cdc.casenotificationservice.service.nonstd.interfaces;

import gov.cdc.casenotificationservice.model.PHINMSProperties;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;

public interface INonStdBatchService {
    boolean isBatchConditionApplied(PHINMSProperties phinmsProperties,  CaseNotificationConfig stdConfig);
    PHINMSProperties ReleaseQueuePopulateBatchFooterProperties();
    void holdQueue(PHINMSProperties phinmsProperties);
}