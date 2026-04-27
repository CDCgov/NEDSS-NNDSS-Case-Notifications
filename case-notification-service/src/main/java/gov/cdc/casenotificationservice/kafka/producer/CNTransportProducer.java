package gov.cdc.casenotificationservice.kafka.producer;

import com.google.gson.Gson;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CNTransportProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;
  private final Gson gson;

  @Value("${spring.kafka.topic.std-topic}")
  private String stdTopic;

  @Value("${spring.kafka.topic.non-std-topic}")
  private String nonStdTopic;

  public CNTransportProducer(KafkaTemplate<String, String> kafkaTemplate, Gson gson) {
    this.kafkaTemplate = kafkaTemplate;
    this.gson = gson;
  }

  public void sendMessage(MessageAfterStdChecker transformedMessage) {
    try {
      String payload = gson.toJson(transformedMessage);

      if (transformedMessage.isStdMessageDetected()) {
        kafkaTemplate.send(stdTopic, payload);
        log.info("Sent transformed message to std topic {}: {}", stdTopic, payload);
        kafkaTemplate.send(nonStdTopic, payload);
        log.info("Sent transformed message to non-std topic {}: {}", nonStdTopic, payload);
      } else {
        kafkaTemplate.send(nonStdTopic, payload);
        log.info("Sent transformed message to non-std topic {}: {}", nonStdTopic, payload);
      }
    } catch (Exception e) {
      log.error("Failed to send message to Kafka: {}", e.getMessage(), e);
    }
  }
}
