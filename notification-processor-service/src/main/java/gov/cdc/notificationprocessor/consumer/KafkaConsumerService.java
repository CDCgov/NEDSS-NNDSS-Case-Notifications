package gov.cdc.notificationprocessor.consumer;

import gov.cdc.notificationprocessor.model.MessageElement;
import gov.cdc.notificationprocessor.model.NBSNNDIntermediaryMessage;
import gov.cdc.notificationprocessor.util.HL7MessageBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import java.io.File;


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

        try {
            File xmlFile = new File("src/main/resources/sample_010724.xml");
            JAXBContext context = JAXBContext.newInstance(NBSNNDIntermediaryMessage.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            NBSNNDIntermediaryMessage nbsnndIntermediaryMessage= (NBSNNDIntermediaryMessage) unmarshaller.unmarshal(xmlFile);
            //Instantiate class to start building HL7 Message
            HL7MessageBuilder messageBuilder = new HL7MessageBuilder(nbsnndIntermediaryMessage);
            messageBuilder.handler();

        }catch (Exception e) {
            logger.error("Exception occurred while parsing/processing NBSNNDMessage xml file", e);
        }
    }
}
