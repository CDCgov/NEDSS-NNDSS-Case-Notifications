package gov.cdc.casenotificationservice.service.nonstd;

import gov.cdc.casenotificationservice.exception.*;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.model.PHINMSProperties;
import gov.cdc.casenotificationservice.repository.msg.CaseNotificationConfigRepository;
import gov.cdc.casenotificationservice.repository.msg.TransportQOutRepository;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import gov.cdc.casenotificationservice.repository.msg.model.TransportQOut;
import gov.cdc.casenotificationservice.repository.odse.CNTraportqOutRepository;
import gov.cdc.casenotificationservice.service.common.interfaces.IXmlHl7Service;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.INonStdBatchService;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.INonStdService;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.IPHINMSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NonStdService implements INonStdService {
    private static final Logger logger = LoggerFactory.getLogger(NonStdService.class); //NOSONAR

    @Value("${service.timezone}")
    private String tz = "UTC";
    private final IPHINMSService phinmsService;
    private final INonStdBatchService batchService;
    private final TransportQOutRepository transportQOutRepository;
    private final CNTraportqOutRepository cnTraportqOutRepository;
    private final CaseNotificationConfigRepository caseNotificationConfigRepository;
    private final IXmlHl7Service xmlHl7Service;

    public NonStdService(IPHINMSService phinmsService,
                         INonStdBatchService batchService,
                         TransportQOutRepository transportQOutRepository,
                         CNTraportqOutRepository cnTraportqOutRepository,
                         CaseNotificationConfigRepository caseNotificationConfigRepository,
                         IXmlHl7Service xmlHl7Service) {
        this.phinmsService = phinmsService;
        this.batchService = batchService;
        this.transportQOutRepository = transportQOutRepository;
        this.cnTraportqOutRepository = cnTraportqOutRepository;
        this.caseNotificationConfigRepository = caseNotificationConfigRepository;
        this.xmlHl7Service = xmlHl7Service;
    }

    public void nonStdProcessor(MessageAfterStdChecker messageAfterStdChecker, boolean hl7ValidationEnabled) throws IgnorableException, NonStdProcessorServiceException, NonStdBatchProcessorServiceException, APIException {
            PHINMSProperties phinmsProperties = new PHINMSProperties();
            CaseNotificationConfig stdConfig = caseNotificationConfigRepository.findNonStdConfig();
            var cnTransport = cnTraportqOutRepository.findTopByRecordUid(messageAfterStdChecker.getCnTransportqOutUid());

            var payload = xmlHl7Service.buildHl7Message(cnTransport, hl7ValidationEnabled);

            if (payload.isEmpty()) {
                throw new IgnorableException("Payload is empty");
            }

            phinmsProperties.setCnTransportUid(cnTransport.getCnTransportqOutUid());
            phinmsProperties.setPMessageUid(cnTransport.getNotificationLocalId());
            phinmsProperties.setPNotificationId(String.valueOf(cnTransport.getNotificationUid()));
            phinmsProperties.setPPublicHealthCaseLocalId(cnTransport.getPublicHealthCaseLocalId());
            phinmsProperties.setPReportStatusCd(cnTransport.getReportStatusCd());
            phinmsProperties.setNETSS_MESSAGE_ONLY("queued");
            phinmsProperties.setBATCH_MESSAGE_PROFILE_ID(stdConfig.getBatchMesageProfileId());

            PHINMSProperties updatedPhinmsProperties;

            try {
                updatedPhinmsProperties = phinmsService.gettingPHIMNSProperties(payload, phinmsProperties, stdConfig);
            } catch (Exception e) {
                throw new NonStdProcessorServiceException("Failure at PHINMS processor", e);
            }

            if (batchService.isBatchConditionApplied(updatedPhinmsProperties, stdConfig)) {
                try {
                    batchNonStdProcessor(updatedPhinmsProperties);
                } catch (Exception e) {
                    throw new NonStdBatchProcessorServiceException("Failure at batch processor", e);
                }
            }
            else
            {
                try {
                    nonStdProcessor(updatedPhinmsProperties);
                } catch (Exception e) {
                    throw new NonStdProcessorServiceException("Failure at Non Std DB Logic", e);

                }

            }

    }

    public void releaseHoldQueueAndProcessBatchNonStd() throws NonRetryableException {
        var updatedPhinmsPropertiesForBatch = batchService.ReleaseQueuePopulateBatchFooterProperties();
        nonStdProcessor(updatedPhinmsPropertiesForBatch);
    }

    protected void nonStdProcessor(PHINMSProperties PHINMSProperties) throws NonRetryableException {
        TransportQOut transportQOut = new TransportQOut(PHINMSProperties, tz);
        transportQOutRepository.save(transportQOut);

        try {
            cnTraportqOutRepository.updateStatusToQueued(PHINMSProperties.getCnTransportUid()); // "WHERE IS THIS ID COME FROM"
        } catch (Exception e) {
            throw new NonRetryableException(e.getMessage(), e);
        }
    }

    protected void batchNonStdProcessor(PHINMSProperties PHINMSProperties) {
        batchService.holdQueue(PHINMSProperties);
    }
}