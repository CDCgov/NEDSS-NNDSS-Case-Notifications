package gov.cdc.xmlhl7parserlib.util.interfaces;

import java.util.Map;

public interface HL7FieldProcessor {
    String  processFields(Map<String, String> inputFields);
}
