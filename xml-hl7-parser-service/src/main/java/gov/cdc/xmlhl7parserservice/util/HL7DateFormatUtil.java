package gov.cdc.xmlhl7parserservice.util;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class HL7DateFormatUtil {
    
    private final DataTypeProcessor dataTypeProcessor;
    private String messageType = "other"; // Default value
    public static final String HL_SEVEN_SEGMENT_FIELD = "hl7SegmentField";


    public HL7DateFormatUtil(DataTypeProcessor dataTypeProcessor) {
        this.dataTypeProcessor = dataTypeProcessor;
    }

    /**
     * Formats a date value according to HL7 standards based on the segment and field type
     * Uses DataTypeProcessor to handle the actual date formatting based on HL7 standards
     * 
     * @param dateValue The date value to format
     * @param questionDataTypeNND The NND data type
     * @param questionIdentifierNND The NND question identifier
     * @param segmentField The HL7 segment and field (e.g. "PID-7", "OBR-7.0")
     * @return Formatted date string according to HL7 standards
     */
    public String formatDate(String dateValue, String questionDataTypeNND, String questionIdentifierNND, String segmentField) {
        Map<String, String> fields = new HashMap<>();
        fields.put(HL_SEVEN_SEGMENT_FIELD, dateValue);
        fields.put("mmgVersion", messageType);
        fields.put("inputDataType", questionDataTypeNND);
        fields.put("questionIdentifier", questionIdentifierNND);
        fields.put("hl7Segment", segmentField);
        
        return dataTypeProcessor.processFields(fields);
    }
} 