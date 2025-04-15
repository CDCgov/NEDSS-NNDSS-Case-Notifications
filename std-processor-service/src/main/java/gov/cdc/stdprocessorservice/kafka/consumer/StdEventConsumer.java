package gov.cdc.stdprocessorservice.kafka.consumer;

import gov.cdc.stdprocessorservice.service.interfaces.IXmlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class StdEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(StdEventConsumer.class); //NOSONAR
    private final IXmlService xmlService;

    public StdEventConsumer(IXmlService xmlService) {
        this.xmlService = xmlService;
    }

    @KafkaListener(
            topics = "${kafka.topic.std-topic}"
    )
    public void handleMessage(String message){
        try {
            xmlService.mappingXmlStringToObject(message);
        } catch (Exception e) {
            logger.error("KafkaEdxLogConsumer.handleMessage: {}", e.getMessage());
        }

    }
}
