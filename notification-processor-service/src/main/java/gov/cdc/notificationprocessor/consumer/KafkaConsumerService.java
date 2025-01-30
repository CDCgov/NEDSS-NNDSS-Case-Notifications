package gov.cdc.notificationprocessor.consumer;

import ca.uhn.hl7v2.HL7Exception;
import gov.cdc.notificationprocessor.util.NBSNNDIntermediaryMessageParser;
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

    public void listen() throws HL7Exception {
        //TODO- reading from kafka topic, below code assumes xml is ready for processing
        logger.info("Processing notification routes for STD/NonSTD for now manual");


        try {
            // we need to parse the xml to find out which route to use
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            File xmlFile = new File("src/main/resources/sample_010724.xml");
            NBSNNDIntermediaryMessageParser handler  = new NBSNNDIntermediaryMessageParser();
            saxParser.parse(xmlFile, handler);

            logger.info("final {} ",handler.segmentFieldsWithValues);
           // ORU_R01 observationMessage = new ORU_R01();




        }catch (Exception e) {
            logger.error("The ERROR Is ", e);
        }








        //check if its std or non std and call handler accordingly.


    }

}
