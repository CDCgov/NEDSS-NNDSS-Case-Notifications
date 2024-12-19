package gov.cdc.stdprocessorservice.kafka.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

public class StdEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(StdEventConsumer.class); //NOSONAR


    @KafkaListener(
            topics = "${kafka.topic.std-topic}"
    )
    public void handleMessage(String message){
        try {

        } catch (Exception e) {
            logger.error("KafkaEdxLogConsumer.handleMessage: {}", e.getMessage());
        }

    }
}
