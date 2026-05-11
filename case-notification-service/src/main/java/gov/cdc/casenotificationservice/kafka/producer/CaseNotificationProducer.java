package gov.cdc.casenotificationservice.kafka.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CaseNotificationProducer {

  private final KafkaTemplate<String, String> kafkaTemplate;

  public CaseNotificationProducer(KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendMessage(String payload, String topic) {
    kafkaTemplate.send(topic, payload);
  }
}
