package gov.cdc.nonstdprocessorservice.non_std.service;

import gov.cdc.nonstdprocessorservice.non_std.model.PHINMSProperties;
import gov.cdc.nonstdprocessorservice.non_std.service.interfaces.IBatchService;
import gov.cdc.nonstdprocessorservice.non_std.service.interfaces.INonStdService;
import gov.cdc.nonstdprocessorservice.non_std.service.interfaces.IPHINMSService;
import org.springframework.stereotype.Service;

@Service
public class NonStdService implements INonStdService {
    private final IPHINMSService phinmsService;
    private final IBatchService batchService;

    public NonStdService(IPHINMSService phinmsService, IBatchService batchService) {
        this.phinmsService = phinmsService;
        this.batchService = batchService;
    }

    public void nonStdProcessor(String payload, PHINMSProperties PHINMSProperties) throws Exception {
        var updatedPhinmsProperties = phinmsService.gettingPHIMNSProperties(payload, PHINMSProperties);
        if (batchService.isBatchConditionApplied(updatedPhinmsProperties)) {
            batchNonStdProcessor(updatedPhinmsProperties);
        }
        else
        {
            nonStdProcessor(updatedPhinmsProperties);
        }
    }

    public void releaseHoldQueueAndProcessBatchNonStd() throws Exception {
        var updatedPhinmsPropertiesForBatch = batchService.ReleaseQueuePopulateBatchFooterProperties();
        nonStdProcessor(updatedPhinmsPropertiesForBatch);
    }

    protected void nonStdProcessor(PHINMSProperties PHINMSProperties) throws Exception {
        // TODO: Logic to update TransportQOut table and Logic to update PHINMSQUEUED
    }

    protected void batchNonStdProcessor(PHINMSProperties PHINMSProperties) throws Exception {
        batchService.holdQueue(PHINMSProperties);
    }
}
