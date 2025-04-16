package gov.cdc.casenotificationservice.service.nonstd.interfaces;

import gov.cdc.casenotificationservice.model.PHINMSProperties;

public interface INonStdBatchService {
    boolean isBatchConditionApplied(PHINMSProperties phinmsProperties);
    PHINMSProperties ReleaseQueuePopulateBatchFooterProperties();
    void holdQueue(PHINMSProperties phinmsProperties);
}