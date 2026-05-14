package gov.cdc.casenotificationservice.kafka.consumer;

import com.google.gson.Gson;
import gov.cdc.casenotificationservice.exception.*;
import gov.cdc.casenotificationservice.model.CnTransportqOutMessage;
import gov.cdc.casenotificationservice.model.CnTransportqOutValue;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.repository.msg.CaseNotificationConfigRepository;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import gov.cdc.casenotificationservice.service.cntransportqout.StdCheckerTransformerService;
import gov.cdc.casenotificationservice.service.cntransportqout.UpdateService;
import gov.cdc.casenotificationservice.service.common.ConfigurationService;
import gov.cdc.casenotificationservice.service.common.DltService;
import gov.cdc.casenotificationservice.service.nonstd.NonStdService;
import gov.cdc.casenotificationservice.service.std.XmlService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CNTransportQOutConsumer {

  @Value("${spring.kafka.topic.cn-transportq-out-topic}")
  private String transportOutQTopic;

  @Autowired private StdCheckerTransformerService transformerService;

  @Autowired private UpdateService updateService;

  @Autowired private ConfigurationService configurationService;

  @Autowired private XmlService xmlService;

  @Autowired private NonStdService nonStdService;

  @Autowired private DltService dltService;

  private final CaseNotificationConfigRepository caseNotificationConfigRepository;

  public CNTransportQOutConsumer(
      StdCheckerTransformerService transformerService,
      UpdateService updateService,
      CaseNotificationConfigRepository caseNotificationConfigRepository,
      ConfigurationService configurationService,
      XmlService xmlService,
      NonStdService nonStdService,
      DltService dltService) {
    this.transformerService = transformerService;
    this.updateService = updateService;
    this.caseNotificationConfigRepository = caseNotificationConfigRepository;
    this.configurationService = configurationService;
    this.xmlService = xmlService;
    this.nonStdService = nonStdService;
    this.dltService = dltService;
  }

  @RetryableTopic(
      attempts = "${spring.kafka.retry.max-retry}",
      autoCreateTopics = "false",
      dltStrategy = DltStrategy.FAIL_ON_ERROR,
      retryTopicSuffix = "${spring.kafka.retry.suffix}",
      dltTopicSuffix = "${spring.kafka.dlt.suffix}",
      // retry topic name, such as topic-retry-1, topic-retry-2, etc
      topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
      // time to wait before attempting to retry
      backoff = @Backoff(delay = 1000, multiplier = 2.0),
      exclude = {NonRetryableException.class})
  @KafkaListener(
      topics = "${spring.kafka.topic.cn-transportq-out-topic}",
      containerFactory = "kafkaListenerContainerFactoryDebeziumConsumer")
  public void handleMessage(String message)
      throws IgnorableException,
          NonRetryableException,
          NonStdProcessorServiceException,
          StdProcessorServiceException,
          NonStdBatchProcessorServiceException {
    CaseNotificationConfig config = caseNotificationConfigRepository.findNonStdConfig();

    if (message == null || message.isEmpty()) {
      throw new NonRetryableException("passed in JSON is null or empty");
    }

    if (config == null || !config.getConfigApplied()) {
      throw new RuntimeException("config not found or is not applied");
    }

    CnTransportqOutMessage cnTransportqOutMessage =
        new Gson().fromJson(message, CnTransportqOutMessage.class);

    if (cnTransportqOutMessage.getPayload() == null) {
      throw new NonRetryableException("payload missing from passed in message");
    }

    CnTransportqOutValue after = cnTransportqOutMessage.getPayload().getAfter();

    if (after == null) {
      throw new NonRetryableException("payload does not contain an after state");
    }

    MessageAfterStdChecker transformed = transformerService.transform(after);

    if (transformed == null) {
      throw new NonRetryableException(
          "CnTransportqOutValue to MessageAfterStdChecker conversion yieled null");
    }

    log.info("Transformed message ready: {}", transformed);

    processEvent(transformed);

    String newStatus =
        transformed.isStdMessageDetected()
                && ("NETSS_MESSAGE_ONLY".equals(transformed.getNetssMessageOnly())
                    || "BOTH".equals(transformed.getNetssMessageOnly()))
            ? "STD_PROCESSING"
            : "NON_STD_PROCESSING";
    updateService.updateRecordStatus(transformed.getCnTransportqOutUid(), newStatus);
  }

  /** Process DLT messages through the {@link DltService}. */
  @DltHandler
  public void handleDlt(
      String message,
      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
      @Header(KafkaHeaders.EXCEPTION_STACKTRACE) String stacktrace) {
    log.info("Received DLT message: {}", message);
    dltService.creatingDlt(message, topic, stacktrace, topic);
  }

  /**
   * Call the requisite service for the given message. Service call is based upon whether message is
   * STD or NON-STD.
   */
  void processEvent(MessageAfterStdChecker messageAfterStdChecker)
      throws IgnorableException,
          NonStdProcessorServiceException,
          NonStdBatchProcessorServiceException,
          NonRetryableException,
          StdProcessorServiceException {
    String messageType = messageAfterStdChecker.isStdMessageDetected() ? "std" : "non-std";
    log.info("Received {} message", messageType);
    if (!configurationService.checkConfigurationAvailable()) {
      log.warn("configuration not available, unable to process {} message", messageType);
      return;
    }

    if (messageAfterStdChecker.isStdMessageDetected()) {
      xmlService.mappingXmlStringToObject(messageAfterStdChecker);
    } else {
      nonStdService.nonStdProcessor(
          messageAfterStdChecker, configurationService.checkHl7ValidationApplied());
    }

    log.info("Completed {} message", messageType);
  }
}
