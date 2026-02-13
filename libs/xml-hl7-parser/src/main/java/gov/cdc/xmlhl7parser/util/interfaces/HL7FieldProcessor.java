package gov.cdc.xmlhl7parser.util.interfaces;

import java.util.Map;

public interface HL7FieldProcessor {
    String  processFields(Map<String, String> inputFields);
}
