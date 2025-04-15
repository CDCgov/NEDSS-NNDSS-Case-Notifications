package gov.cdc.nonstdprocessorservice.non_std.service.interfaces;

import gov.cdc.nonstdprocessorservice.non_std.model.PHINMSProperties;

public interface IBatchService {
    boolean isBatchConditionApplied(PHINMSProperties phinmsProperties);
    PHINMSProperties ReleaseQueuePopulateBatchFooterProperties();
    void holdQueue(PHINMSProperties phinmsProperties);
}
