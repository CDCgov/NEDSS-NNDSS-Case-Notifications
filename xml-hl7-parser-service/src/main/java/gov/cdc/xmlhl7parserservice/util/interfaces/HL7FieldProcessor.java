package gov.cdc.xmlhl7parserservice.util.interfaces;

import java.util.Map;

public interface HL7FieldProcessor {
    String  processFields(Map<String, String> inputFields);
}
