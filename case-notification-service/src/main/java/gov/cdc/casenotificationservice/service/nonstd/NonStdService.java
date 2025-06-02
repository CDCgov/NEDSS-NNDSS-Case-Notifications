package gov.cdc.casenotificationservice.service.nonstd;

import gov.cdc.casenotificationservice.exception.*;
import gov.cdc.casenotificationservice.kafka.consumer.StdEventConsumer;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.model.PHINMSProperties;
import gov.cdc.casenotificationservice.repository.msg.CaseNotificationConfigRepository;
import gov.cdc.casenotificationservice.repository.msg.TransportQOutRepository;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import gov.cdc.casenotificationservice.repository.msg.model.TransportQOut;
import gov.cdc.casenotificationservice.repository.odse.CNTraportqOutRepository;
import gov.cdc.casenotificationservice.service.common.interfaces.IApiService;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.INonStdBatchService;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.INonStdService;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.IPHINMSService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
    private final IApiService apiService;

    public NonStdService(IPHINMSService phinmsService,
                         INonStdBatchService batchService,
                         TransportQOutRepository transportQOutRepository,
                         CNTraportqOutRepository cnTraportqOutRepository,
                         CaseNotificationConfigRepository caseNotificationConfigRepository,
                         IApiService apiService) {
        this.phinmsService = phinmsService;
        this.batchService = batchService;
        this.transportQOutRepository = transportQOutRepository;
        this.cnTraportqOutRepository = cnTraportqOutRepository;
        this.caseNotificationConfigRepository = caseNotificationConfigRepository;
        this.apiService = apiService;
    }

    public void nonStdProcessor(MessageAfterStdChecker messageAfterStdChecker, boolean hl7ValidationEnabled) throws IgnorableException, NonStdProcessorServiceException, NonStdBatchProcessorServiceException, APIException {
            PHINMSProperties phinmsProperties = new PHINMSProperties();
            CaseNotificationConfig stdConfig = caseNotificationConfigRepository.findNonStdConfig();
            var cnTranport = cnTraportqOutRepository.findTopByRecordUid(messageAfterStdChecker.getCnTransportqOutUid());

            var token = apiService.callToken();
            if (token == null || token.isEmpty()) {
                throw new IgnorableException("Token is Invalid");
            }
            var tranformedData = apiService.callHl7Endpoint(token, String.valueOf(cnTranport.getCnTransportqOutUid()), hl7ValidationEnabled);
            String payload = tranformedData;
            if (payload.isEmpty()) {
                throw new IgnorableException("Payload is empty");
            }

            phinmsProperties.setCnTransportUid(cnTranport.getCnTransportqOutUid());
            phinmsProperties.setPMessageUid(cnTranport.getNotificationLocalId());
            phinmsProperties.setPNotificationId(String.valueOf(cnTranport.getNotificationUid()));
            phinmsProperties.setPPublicHealthCaseLocalId(cnTranport.getPublicHealthCaseLocalId());
            phinmsProperties.setPReportStatusCd(cnTranport.getReportStatusCd());
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