package gov.cdc.casenotificationservice.service.nonstd;

import gov.cdc.casenotificationservice.model.PHINMSProperties;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.INonStdBatchService;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.INonStdService;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.IPHINMSService;
import org.springframework.stereotype.Service;

@Service
public class NonStdService implements INonStdService {
    private final IPHINMSService phinmsService;
    private final INonStdBatchService batchService;

    public NonStdService(IPHINMSService phinmsService, INonStdBatchService batchService) {
        this.phinmsService = phinmsService;
        this.batchService = batchService;
    }

    public void nonStdProcessor(String payload) throws Exception {
        PHINMSProperties phinmsProperties = new PHINMSProperties();
        var updatedPhinmsProperties = phinmsService.gettingPHIMNSProperties(payload, phinmsProperties);
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