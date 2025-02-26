package gov.cdc.notificationprocessor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of HL7FieldProcessor that reads serviceActionPair.json from Resources
 * creates list of Java Objects.
 */
public class OBR31SegmentProcessing implements Hl7FieldProcessor{
    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(DateTypeProcessing.class);

    @Override
    public String process(Map<String, String> inputFields) {
        //initialize result object
        Map<String, Boolean> results = new HashMap<>();
        logger.info("Processing OBR31SegmentProcessing {}", inputFields);


        return "";
    }
}
