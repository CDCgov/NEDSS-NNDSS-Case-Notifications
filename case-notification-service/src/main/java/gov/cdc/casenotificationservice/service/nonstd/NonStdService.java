package gov.cdc.casenotificationservice.service.nonstd;

import gov.cdc.casenotificationservice.model.PHINMSProperties;
import gov.cdc.casenotificationservice.repository.msg.TransportQOutRepository;
import gov.cdc.casenotificationservice.repository.msg.model.TransportQOut;
import gov.cdc.casenotificationservice.repository.odse.CNTraportqOutRepository;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.INonStdBatchService;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.INonStdService;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.IPHINMSService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NonStdService implements INonStdService {
    @Value("${service.timezone}")
    private String tz = "UTC";
    private final IPHINMSService phinmsService;
    private final INonStdBatchService batchService;
    private final TransportQOutRepository transportQOutRepository;
    private final CNTraportqOutRepository cnTraportqOutRepository;

    public NonStdService(IPHINMSService phinmsService, INonStdBatchService batchService,
                         TransportQOutRepository transportQOutRepository,
                         CNTraportqOutRepository cnTraportqOutRepository) {
        this.phinmsService = phinmsService;
        this.batchService = batchService;
        this.transportQOutRepository = transportQOutRepository;
        this.cnTraportqOutRepository = cnTraportqOutRepository;
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
        TransportQOut transportQOut = new TransportQOut(PHINMSProperties, tz);
        transportQOutRepository.save(transportQOut);
        cnTraportqOutRepository.updateStatusToQueued(69696969L); // "WHERE IS THIS ID COME FROM"
    }

    protected void batchNonStdProcessor(PHINMSProperties PHINMSProperties) throws Exception {
        batchService.holdQueue(PHINMSProperties);
    }
}