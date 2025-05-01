package gov.cdc.casenotificationservice.service.nonstd;

import gov.cdc.casenotificationservice.exception.NonStdProcessorServiceException;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.model.PHINMSProperties;
import gov.cdc.casenotificationservice.repository.msg.CaseNotificationConfigRepository;
import gov.cdc.casenotificationservice.repository.msg.TransportQOutRepository;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
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
    private final CaseNotificationConfigRepository caseNotificationConfigRepository;

    public NonStdService(IPHINMSService phinmsService,
                         INonStdBatchService batchService,
                         TransportQOutRepository transportQOutRepository,
                         CNTraportqOutRepository cnTraportqOutRepository,
                         CaseNotificationConfigRepository caseNotificationConfigRepository) {
        this.phinmsService = phinmsService;
        this.batchService = batchService;
        this.transportQOutRepository = transportQOutRepository;
        this.cnTraportqOutRepository = cnTraportqOutRepository;
        this.caseNotificationConfigRepository = caseNotificationConfigRepository;
    }

    public void nonStdProcessor(MessageAfterStdChecker messageAfterStdChecker) {
        try {
            PHINMSProperties phinmsProperties = new PHINMSProperties();
            CaseNotificationConfig stdConfig = caseNotificationConfigRepository.findNonStdConfig();
            var cnTranport = cnTraportqOutRepository.findTopByRecordUid(messageAfterStdChecker.getCnTransportqOutUid());

            // TODO: Logic to tranform xml to HL7
            String payload = "";
            if (payload.isEmpty()) {
                throw new NonStdProcessorServiceException("Payload is empty");
            }

            phinmsProperties.setPMessageUid(messageAfterStdChecker.getCnTransportqOutUid());
            phinmsProperties.setPNotificationId(String.valueOf(cnTranport.getNotificationUid()));
            phinmsProperties.setPPublicHealthCaseLocalId(cnTranport.getPublicHealthCaseLocalId());
            phinmsProperties.setPReportStatusCd(cnTranport.getReportStatusCd());
            phinmsProperties.setNETSS_MESSAGE_ONLY("queued");
            phinmsProperties.setBATCH_MESSAGE_PROFILE_ID(stdConfig.getBatchMesageProfileId());

            var updatedPhinmsProperties = phinmsService.gettingPHIMNSProperties(payload, phinmsProperties, stdConfig);
            if (batchService.isBatchConditionApplied(updatedPhinmsProperties, stdConfig)) {
                batchNonStdProcessor(updatedPhinmsProperties);
            }
            else
            {
                nonStdProcessor(updatedPhinmsProperties);
            }
        } catch (Exception e) {
            cnTraportqOutRepository.updateStatus(messageAfterStdChecker.getCnTransportqOutUid(), "NON_STD_ERROR");
        }

    }

    public void releaseHoldQueueAndProcessBatchNonStd() {
        var updatedPhinmsPropertiesForBatch = batchService.ReleaseQueuePopulateBatchFooterProperties();
        nonStdProcessor(updatedPhinmsPropertiesForBatch);
    }

    protected void nonStdProcessor(PHINMSProperties PHINMSProperties) {
        TransportQOut transportQOut = new TransportQOut(PHINMSProperties, tz);
        transportQOutRepository.save(transportQOut);
        cnTraportqOutRepository.updateStatusToQueued(PHINMSProperties.getPMessageUid()); // "WHERE IS THIS ID COME FROM"
    }

    protected void batchNonStdProcessor(PHINMSProperties PHINMSProperties) {
        batchService.holdQueue(PHINMSProperties);
    }
}