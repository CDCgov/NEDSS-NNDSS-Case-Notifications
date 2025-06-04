package gov.cdc.casenotificationservice.kafka.producer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class CaseNotificationProducer {
    private static final Logger logger = LoggerFactory.getLogger(CaseNotificationProducer.class); //NOSONAR

    private final KafkaTemplate<String, String> kafkaTemplate;

    public CaseNotificationProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    public void sendMessage(String payload, String topic)  {
        try {
            kafkaTemplate.send(topic, payload);
        } catch (Exception e) {
            logger.error("Failed to send message to Kafka: {}", e.getMessage(), e);
        }
    }
}
