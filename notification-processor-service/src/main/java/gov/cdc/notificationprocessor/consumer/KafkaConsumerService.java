package gov.cdc.notificationprocessor.consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * KafkaConsumerService listens to a kafka topic and processes notifications it receives.
 * TODO - More detail to follow.
 */
@Service
public class KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    public void listen() {
        //TODO- reading from kafka topic, below code assumes xml is ready for processing
        logger.info("Processing notification routes for STD/NonSTD for now manual");

        //check if its std or non std and call handler accordingly.


    }

}
