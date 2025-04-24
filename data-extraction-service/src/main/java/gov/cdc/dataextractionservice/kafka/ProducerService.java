package gov.cdc.dataextractionservice.kafka;

import com.google.gson.Gson;
import gov.cdc.dataextractionservice.model.MessageAfterStdChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Gson gson;

    @Value("${kafka.topic.cn-transport-transformed-topic}")
    private String targetTopic;

    public ProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.gson = new Gson();
    }

    public void sendMessage(MessageAfterStdChecker transformedMessage) {
        try {
            String payload = gson.toJson(transformedMessage);
            kafkaTemplate.send(targetTopic, payload);
            log.info("Sent transformed message to topic {}: {}", targetTopic, payload);
        } catch (Exception e) {
            log.error("Failed to send message to Kafka: {}", e.getMessage(), e);
        }
    }
}
