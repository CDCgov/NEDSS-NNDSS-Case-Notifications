package gov.cdc.casenotificationservice.service.nonstd;

import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.model.PHINMSProperties;
import gov.cdc.casenotificationservice.repository.msg.CaseNotificationConfigRepository;
import gov.cdc.casenotificationservice.repository.msg.TransportQOutRepository;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import gov.cdc.casenotificationservice.repository.msg.model.TransportQOut;
import gov.cdc.casenotificationservice.repository.odse.CNTraportqOutRepository;
import gov.cdc.casenotificationservice.repository.odse.model.CNTransportqOut;
import gov.cdc.casenotificationservice.service.common.interfaces.IApiService;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.INonStdBatchService;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.IPHINMSService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class NonStdServiceTest {

    @InjectMocks
    private NonStdService nonStdService;

    @Mock
    private IPHINMSService phinmsService;

    @Mock
    private INonStdBatchService batchService;

    @Mock
    private TransportQOutRepository transportQOutRepository;

    @Mock
    private CNTraportqOutRepository cnTraportqOutRepository;

    @Mock
    private CaseNotificationConfigRepository caseNotificationConfigRepository;

    @Mock
    private IApiService apiService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testNonStdProcessor_BatchConditionTrue() throws Exception {
        MessageAfterStdChecker checker = new MessageAfterStdChecker();
        checker.setCnTransportqOutUid(123L);

        CaseNotificationConfig config = new CaseNotificationConfig();
        config.setBatchMesageProfileId("TEST_PROFILE");

        PHINMSProperties props = new PHINMSProperties();
        props.setPMessageUid(123L);
        props.setPNotificationId("456");
        props.setPPublicHealthCaseLocalId("PHC123");
        props.setPReportStatusCd("COMPLETE");

        CNTransportqOut mockTransport = new CNTransportqOut();
        mockTransport.setCnTransportqOutUid(123L);
        when(apiService.callHl7Endpoint("", "123")).thenReturn("TEST");

        when(caseNotificationConfigRepository.findNonStdConfig()).thenReturn(config);
        when(cnTraportqOutRepository.findTopByRecordUid(123L)).thenReturn(mockTransport);
        when(phinmsService.gettingPHIMNSProperties(any(), any(), any())).thenReturn(props);
        when(batchService.isBatchConditionApplied(props, config)).thenReturn(true);

        nonStdService.nonStdProcessor(checker);

        verify(batchService).holdQueue(props);
    }

    @Test
    void testNonStdProcessor_BatchConditionFalse() throws Exception {
        MessageAfterStdChecker checker = new MessageAfterStdChecker();
        checker.setCnTransportqOutUid(124L);

        CaseNotificationConfig config = new CaseNotificationConfig();
        config.setBatchMesageProfileId("OTHER_PROFILE");

        PHINMSProperties props = new PHINMSProperties();
        props.setPMessageUid(124L);
        props.setPNotificationId("789");
        props.setPPublicHealthCaseLocalId("PHC999");
        props.setPReportStatusCd("NEW");

        CNTransportqOut mockTransport = new CNTransportqOut();
        mockTransport.setCnTransportqOutUid(123L);

        when(apiService.callHl7Endpoint("", "123")).thenReturn("TEST");
        when(caseNotificationConfigRepository.findNonStdConfig()).thenReturn(config);
        when(cnTraportqOutRepository.findTopByRecordUid(124L)).thenReturn(mockTransport);
        when(phinmsService.gettingPHIMNSProperties(any(), any(), any())).thenReturn(props);
        when(batchService.isBatchConditionApplied(props, config)).thenReturn(false);

        nonStdService.nonStdProcessor(checker);

        verify(transportQOutRepository).save(any(TransportQOut.class));
        verify(cnTraportqOutRepository).updateStatusToQueued(124L);
    }

    @Test
    void testReleaseHoldQueueAndProcessBatchNonStd() throws Exception {
        PHINMSProperties mockProps = new PHINMSProperties();
        mockProps.setPMessageUid(333L);

        when(batchService.ReleaseQueuePopulateBatchFooterProperties()).thenReturn(mockProps);

        nonStdService.releaseHoldQueueAndProcessBatchNonStd();

        verify(transportQOutRepository).save(any(TransportQOut.class));
        verify(cnTraportqOutRepository).updateStatusToQueued(333L);
    }
}
