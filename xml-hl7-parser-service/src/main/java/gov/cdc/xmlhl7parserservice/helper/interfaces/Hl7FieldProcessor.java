package gov.cdc.xmlhl7parserservice.helper.interfaces;

import java.util.Map;

public interface Hl7FieldProcessor {
    String  processFields(Map<String, String> inputFields);
}
