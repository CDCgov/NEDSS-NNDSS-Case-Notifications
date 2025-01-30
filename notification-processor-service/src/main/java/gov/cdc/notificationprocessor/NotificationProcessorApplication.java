package gov.cdc.notificationprocessor;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.notificationprocessor.consumer.KafkaConsumerService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotificationProcessorApplication implements ApplicationRunner {

    private final KafkaConsumerService kafkaConsumerService;
    private static final Logger logger = LoggerFactory.getLogger(NotificationProcessorApplication.class);

    public NotificationProcessorApplication(KafkaConsumerService kafkaConsumerService) {
        this.kafkaConsumerService = kafkaConsumerService;
    }

    public static void main(String[] args) {
        //This is the main entry point where Spring Boot application is launched
        SpringApplication.run(NotificationProcessorApplication.class, args);
        logger.info("Notification Processor Application Started");


    }
    @Override
    public void run(ApplicationArguments args) throws HL7Exception {
        kafkaConsumerService.listen();
    }

}
