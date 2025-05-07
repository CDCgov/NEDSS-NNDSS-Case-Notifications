package gov.cdc.xmlhl7parserservice.helper.interfaces;

import java.util.Map;

public interface HL7FieldProcessor {
    String  processFields(Map<String, String> inputFields);
}
