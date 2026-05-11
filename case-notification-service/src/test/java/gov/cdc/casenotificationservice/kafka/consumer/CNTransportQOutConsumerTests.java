package gov.cdc.casenotificationservice.kafka.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.google.gson.Gson;
import gov.cdc.casenotificationservice.exception.*;
import gov.cdc.casenotificationservice.model.CnTransportqOutMessage;
import gov.cdc.casenotificationservice.model.CnTransportqOutValue;
import gov.cdc.casenotificationservice.model.EnvelopePayload;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.repository.msg.CaseNotificationConfigRepository;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import gov.cdc.casenotificationservice.service.cntransportqout.StdCheckerTransformerService;
import gov.cdc.casenotificationservice.service.cntransportqout.UpdateService;
import gov.cdc.casenotificationservice.service.common.ConfigurationService;
import gov.cdc.casenotificationservice.service.nonstd.NonStdService;
import gov.cdc.casenotificationservice.service.std.XmlService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CNTransportQOutConsumerTests {

  @Mock private StdCheckerTransformerService transformerServiceMock;
  @Mock private UpdateService updateServiceMock;
  @Mock private CaseNotificationConfigRepository caseNotificationConfigRepositoryMock;
  @Mock private ConfigurationService configurationServiceMock;
  @Mock private XmlService xmlServiceMock;
  @Mock private NonStdService nonStdServiceMock;
  @InjectMocks private CNTransportQOutConsumer consumer;

  private AutoCloseable mocksAutoClosable;

  @BeforeEach
  void beforeEach() {
    mocksAutoClosable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void afterEach() throws Exception {
    mocksAutoClosable.close();
  }

  @Test
  public void handleMessage_nullConfig()
      throws IgnorableException,
          NonRetryableException,
          NonStdProcessorServiceException,
          StdProcessorServiceException,
          NonStdBatchProcessorServiceException {
    when(caseNotificationConfigRepositoryMock.findNonStdConfig()).thenReturn(null);

    consumer.handleMessage("");

    verify(updateServiceMock, times(0)).updateRecordStatus(anyLong(), anyString());
    verify(xmlServiceMock, times(0)).mappingXmlStringToObject(any(MessageAfterStdChecker.class));
    verify(nonStdServiceMock, times(0))
        .nonStdProcessor(any(MessageAfterStdChecker.class), anyBoolean());
  }

  @Test
  public void handleMessage_configNotApplied()
      throws IgnorableException,
          NonRetryableException,
          NonStdProcessorServiceException,
          StdProcessorServiceException,
          NonStdBatchProcessorServiceException {
    CaseNotificationConfig caseNotificationConfig = new CaseNotificationConfig();
    caseNotificationConfig.setConfigApplied(false);
    when(caseNotificationConfigRepositoryMock.findNonStdConfig())
        .thenReturn(caseNotificationConfig);

    consumer.handleMessage("");

    verify(updateServiceMock, times(0)).updateRecordStatus(anyLong(), anyString());
    verify(xmlServiceMock, times(0)).mappingXmlStringToObject(any(MessageAfterStdChecker.class));
    verify(nonStdServiceMock, times(0))
        .nonStdProcessor(any(MessageAfterStdChecker.class), anyBoolean());
  }

  @Test
  public void handleMessage_noAfterPayload()
      throws IgnorableException,
          NonRetryableException,
          NonStdProcessorServiceException,
          StdProcessorServiceException,
          NonStdBatchProcessorServiceException {
    CaseNotificationConfig caseNotificationConfig = new CaseNotificationConfig();
    caseNotificationConfig.setConfigApplied(true);
    when(caseNotificationConfigRepositoryMock.findNonStdConfig())
        .thenReturn(caseNotificationConfig);

    consumer.handleMessage("{payload: {}}");

    verify(updateServiceMock, times(0)).updateRecordStatus(anyLong(), anyString());
    verify(xmlServiceMock, times(0)).mappingXmlStringToObject(any(MessageAfterStdChecker.class));
    verify(nonStdServiceMock, times(0))
        .nonStdProcessor(any(MessageAfterStdChecker.class), anyBoolean());
  }

  @Test
  public void handleMessage_nullTransformed()
      throws IgnorableException,
          NonRetryableException,
          NonStdProcessorServiceException,
          StdProcessorServiceException,
          NonStdBatchProcessorServiceException {
    CaseNotificationConfig caseNotificationConfig = new CaseNotificationConfig();
    caseNotificationConfig.setConfigApplied(true);
    when(caseNotificationConfigRepositoryMock.findNonStdConfig())
        .thenReturn(caseNotificationConfig);

    when(transformerServiceMock.transform(any(CnTransportqOutValue.class))).thenReturn(null);

    consumer.handleMessage("{payload: {after: {}}}");

    verify(updateServiceMock, times(0)).updateRecordStatus(anyLong(), anyString());
    verify(xmlServiceMock, times(0)).mappingXmlStringToObject(any(MessageAfterStdChecker.class));
    verify(nonStdServiceMock, times(0))
        .nonStdProcessor(any(MessageAfterStdChecker.class), anyBoolean());
  }

  @Test
  public void handleMessage_stdMessage()
      throws IgnorableException,
          NonRetryableException,
          NonStdProcessorServiceException,
          StdProcessorServiceException,
          NonStdBatchProcessorServiceException {
    CaseNotificationConfig config = new CaseNotificationConfig();
    config.setConfigApplied(true);
    when(caseNotificationConfigRepositoryMock.findNonStdConfig()).thenReturn(config);

    MessageAfterStdChecker messageAfterStdChecker = new MessageAfterStdChecker();
    messageAfterStdChecker.setStdMessageDetected(true);
    messageAfterStdChecker.setNetssMessageOnly("BOTH");
    messageAfterStdChecker.setCnTransportqOutUid(19L);
    when(transformerServiceMock.transform(any())).thenReturn(messageAfterStdChecker);

    CnTransportqOutMessage msg = new CnTransportqOutMessage();
    EnvelopePayload payload = new EnvelopePayload();
    CnTransportqOutValue after = new CnTransportqOutValue();
    payload.setAfter(after);
    msg.setPayload(payload);

    consumer.handleMessage(new Gson().toJson(msg));

    verify(updateServiceMock).updateRecordStatus(eq(19L), eq("STD_PROCESSING"));
  }

  @Test
  public void handleMessage_nonStdMessage()
      throws IgnorableException,
          NonRetryableException,
          NonStdProcessorServiceException,
          StdProcessorServiceException,
          NonStdBatchProcessorServiceException {
    CaseNotificationConfig config = new CaseNotificationConfig();
    config.setConfigApplied(true);
    when(caseNotificationConfigRepositoryMock.findNonStdConfig()).thenReturn(config);

    MessageAfterStdChecker messageAfterStdChecker = new MessageAfterStdChecker();
    messageAfterStdChecker.setStdMessageDetected(false);
    messageAfterStdChecker.setCnTransportqOutUid(19L);
    when(transformerServiceMock.transform(any(CnTransportqOutValue.class)))
        .thenReturn(messageAfterStdChecker);

    CnTransportqOutMessage msg = new CnTransportqOutMessage();
    EnvelopePayload payload = new EnvelopePayload();
    CnTransportqOutValue after = new CnTransportqOutValue();
    payload.setAfter(after);
    msg.setPayload(payload);

    consumer.handleMessage(new Gson().toJson(msg));

    verify(updateServiceMock).updateRecordStatus(eq(19L), eq("NON_STD_PROCESSING"));
  }

  @Test
  public void processEvent_std()
      throws IgnorableException,
          NonRetryableException,
          NonStdProcessorServiceException,
          StdProcessorServiceException,
          NonStdBatchProcessorServiceException {
    MessageAfterStdChecker message = new MessageAfterStdChecker();
    message.setStdMessageDetected(true);

    when(configurationServiceMock.checkConfigurationAvailable()).thenReturn(true);

    consumer.processEvent(message);

    verify(xmlServiceMock).mappingXmlStringToObject(eq(message));
    verify(nonStdServiceMock, times(0))
        .nonStdProcessor(any(MessageAfterStdChecker.class), any(Boolean.class));
  }

  @Test
  public void processEvent_nonStd()
      throws IgnorableException,
          NonRetryableException,
          NonStdProcessorServiceException,
          StdProcessorServiceException,
          NonStdBatchProcessorServiceException {
    MessageAfterStdChecker message = new MessageAfterStdChecker();
    message.setStdMessageDetected(false);

    when(configurationServiceMock.checkConfigurationAvailable()).thenReturn(true);
    when(configurationServiceMock.checkHl7ValidationApplied()).thenReturn(true);

    consumer.processEvent(message);

    verify(nonStdServiceMock).nonStdProcessor(eq(message), eq(true));
    verify(xmlServiceMock, times(0)).mappingXmlStringToObject(any(MessageAfterStdChecker.class));
  }
}
