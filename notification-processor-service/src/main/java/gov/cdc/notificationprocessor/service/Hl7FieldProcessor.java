package gov.cdc.notificationprocessor.service;



import java.util.Map;

/**
 * The Hl7FieldProcessor interface  for processing HL7 field data.
 */
public interface Hl7FieldProcessor{
    String  process(Map<String, String> inputFields);
}