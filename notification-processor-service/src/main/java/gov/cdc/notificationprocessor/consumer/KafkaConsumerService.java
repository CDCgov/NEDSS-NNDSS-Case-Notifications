package gov.cdc.notificationprocessor.consumer;

import gov.cdc.notificationprocessor.util.NBSNNDMessageParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
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
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            File xmlFile = new File("src/main/resources/sample_010724.xml");
            NBSNNDMessageParser handler  = new NBSNNDMessageParser();
            saxParser.parse(xmlFile, handler);
        }catch (Exception e) {
            logger.error("Exception occurred while parsing/processing NBSNNDMessage xml file", e);
        }








        //check if its std or non std and call handler accordingly.


    }

}
