package gov.cdc.casenotificationservice.kafka.consumer;

import com.google.gson.Gson;
import gov.cdc.casenotificationservice.exception.NonRetryableException;
import gov.cdc.casenotificationservice.kafka.producer.CNTransportProducer;
import gov.cdc.casenotificationservice.model.CnTransportqOutMessage;
import gov.cdc.casenotificationservice.model.CnTransportqOutValue;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.repository.msg.CaseNotificationConfigRepository;
import gov.cdc.casenotificationservice.service.cntransportqout.StdCheckerTransformerService;
import gov.cdc.casenotificationservice.service.cntransportqout.UpdateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class CNTransportQOutConsumer {
  private static final Logger logger = LoggerFactory.getLogger(CNTransportQOutConsumer.class);

  @Value("${spring.kafka.topic.cn-transportq-out-topic}")
  private String transportOutQTopic;

  @Autowired private StdCheckerTransformerService transformerService;

  @Autowired private CNTransportProducer producerService;

  @Autowired private UpdateService updateService;

  private final CaseNotificationConfigRepository caseNotificationConfigRepository;

  public CNTransportQOutConsumer(
      CaseNotificationConfigRepository caseNotificationConfigRepository) {
    this.caseNotificationConfigRepository = caseNotificationConfigRepository;
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
  public void handleMessage(String messages) {
    try {
      var config = caseNotificationConfigRepository.findNonStdConfig();
      if (config != null && config.getConfigApplied()) {
        logger.info("Raw message: {}", messages);
        Gson gson = new Gson();
        CnTransportqOutMessage message = gson.fromJson(messages, CnTransportqOutMessage.class);

        CnTransportqOutValue after = message.getPayload().getAfter();
        if (after != null) {
          MessageAfterStdChecker transformed = transformerService.transform(after);

          if (transformed != null) {
            logger.info("Transformed message ready: {}", transformed);

            // Send to downstream Kafka
            producerService.sendMessage(transformed);

            // Update database record_status_cd
            if (transformed.isStdMessageDetected()
                && ("NETSS_MESSAGE_ONLY".equals(transformed.getNetssMessageOnly())
                    || "BOTH".equals(transformed.getNetssMessageOnly()))) {
              updateService.updateRecordStatus(
                  transformed.getCnTransportqOutUid(), "STD_PROCESSING");
            } else {
              updateService.updateRecordStatus(
                  transformed.getCnTransportqOutUid(), "NON_STD_PROCESSING");
            }
          } else {
            logger.info("Message skipped - did not meet the criteria");
          }
        } else {
          logger.info("Change Data Capture event ignored (no 'after' state)");
        }
      }

    } catch (Exception e) {
      logger.error("ConsumerService.handleMessage: {}", e.getMessage(), e);
    }
  }
}
