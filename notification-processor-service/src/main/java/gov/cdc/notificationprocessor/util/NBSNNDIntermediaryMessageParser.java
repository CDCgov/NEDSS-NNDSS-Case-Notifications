package gov.cdc.notificationprocessor.util;

import gov.cdc.notificationprocessor.constants.Constants;
import gov.cdc.notificationprocessor.consumer.KafkaConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;

public class NBSNNDIntermediaryMessageParser extends DefaultHandler {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);

    private String currentElement = "";
    private String segmentField = "";
    private String segmentFieldValue = "";
    private final StringBuilder currentField = new StringBuilder();

    public Map<String,String> segmentFieldsWithValues = new HashMap<>();

    @Override
    public void startDocument() throws SAXException {
        logger.info("Starting the parsing process");
    }
    @Override
    public void endDocument() throws SAXException {
        logger.info("Completed the parsing process");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        currentElement = qName;
        currentField.setLength(0); // reset the current field
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!currentElement.isEmpty()){
            currentField.append(ch, start, length);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (Constants.HL_SEVEN_SEGMENT_FIELD.equals(qName)){
            segmentField = currentField.toString().trim();
        }else if (Constants.HL_SEVEN_SEGMENT_FIELD_VALUE.equals(qName)){
            segmentFieldValue = currentField.toString().trim();
        }else if (Constants.MESSAGE_ELEMENT.equals(qName)) {
            if (!segmentField.isEmpty() && !segmentFieldValue.isEmpty()){
                segmentFieldsWithValues.put(segmentField, segmentFieldValue);
            }
            //reset the variables
            segmentField = "";
            segmentFieldValue = "";
        }
        currentElement = "";
    }


}
