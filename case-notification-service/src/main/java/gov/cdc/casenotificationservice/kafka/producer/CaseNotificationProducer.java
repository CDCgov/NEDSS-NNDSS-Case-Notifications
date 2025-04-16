package gov.cdc.casenotificationservice.kafka.producer;

import gov.cdc.casenotificationservice.exception.KafkaProducerException;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class CaseNotificationProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;

    public CaseNotificationProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }


    private void sendMessage(ProducerRecord<String, String> prodRecord) throws KafkaProducerException {
        try {
            kafkaTemplate.send(prodRecord).get(3, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new KafkaProducerException("Thread was interrupted while sending Kafka message to topic: "
                    + prodRecord.topic() + " with UUID: " + prodRecord.value());
        }
        catch (TimeoutException | ExecutionException e) {
            throw new KafkaProducerException("Failed publishing message to kafka topic: " + prodRecord.topic() + " with UUID: " + prodRecord.value());
        }
    }
}
