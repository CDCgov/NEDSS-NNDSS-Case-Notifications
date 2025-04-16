package gov.cdc.casenotificationservice.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class NonStdEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(StdEventConsumer.class); //NOSONAR
    @KafkaListener(
            topics = "${spring.kafka.topic.non-std-topic}",
            containerFactory = "kafkaListenerContainerFactoryConsumerForNonStd"
    )
    public void handleMessage(String message){
        try {

        } catch (Exception e) {
            logger.error("KafkaEdxLogConsumer.handleMessage: {}", e.getMessage());
        }

    }
}
