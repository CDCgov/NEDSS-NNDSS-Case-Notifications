package gov.cdc.xmlhl7parserservice.helper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.xmlhl7parserservice.constants.Constants;

import gov.cdc.xmlhl7parserservice.helper.interfaces.Hl7FieldProcessor;
import gov.cdc.xmlhl7parserservice.model.DataTypeModel;
import gov.cdc.xmlhl7parserservice.repository.IDataTypeLookupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of HL7FieldProcessor that reads DataTypes.json from Resources
 * creates list of Java Objects.
 */public class DataTypeProcessor implements Hl7FieldProcessor {

    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(DataTypeProcessor.class);
    IDataTypeLookupRepository iDataTypeLookupRepository;

    @Override
    public String processFields(Map<String, String> inputFields) {
        logger.info ("inputFields: {}", inputFields);
        //String segmentField = inputFields.get("hl7Segment");
        String hl7SegmentDateField = inputFields.get("hl7SegmentField");
        String inputDataType = inputFields.get("inputDataType");
        String mmgVersion = inputFields.get("mmgVersion");

        String outputDataType = mapInputToOutputDataType(inputDataType);

        Optional<DataTypeModel> matchedDataType = iDataTypeLookupRepository.findByMmgVersionAndCoreDataType(mmgVersion, inputDataType);

        if (matchedDataType.isPresent()) {
            outputDataType = matchedDataType.get().getDataType();
        }

        String dateFormatted = formatDateField(hl7SegmentDateField, outputDataType);

        logger.info("Final Formatted date string: {}", dateFormatted);
        return dateFormatted;
    }

    /**
     *
     * @param hl7SegmentDateField date format from hl7 segment field in ISO 8601
     * @param outputDataType date format based on the
     * @return date format based on the match found in DateTypes.json in epoch format.
     */
    private String formatDateField(String hl7SegmentDateField,String outputDataType) {
        boolean matchFound = false;
        //extract year
        String year = extractDateComponent(hl7SegmentDateField,0, 4);
        String month = "";
        String day = "";
        String hours = "";
        String minutes = "";
        String seconds = "";
        String milliseconds = "";
        //month
        if (outputDataType.equalsIgnoreCase("DT4") || outputDataType.equals("TS4")) {
            matchFound = true;
        }else {
            if (hl7SegmentDateField.length() <7) {
                month = "00";
            }else{
                month = extractDateComponent(hl7SegmentDateField,5, 7);
            }
        }
        //day
        if (outputDataType.equalsIgnoreCase("DT6") || outputDataType.equals("TS6")) {
            matchFound = true;
        }else if (!matchFound) {
            if (hl7SegmentDateField.length() <10) {
                day = "00";
            }else{
                day = extractDateComponent(hl7SegmentDateField,8, 10);
            }
        }
        //hour
        if (outputDataType.equalsIgnoreCase("DT8") || outputDataType.equals("TS8")) {
            matchFound = true;
        }else if (!matchFound) {
            if (hl7SegmentDateField.length() <13) {
                hours = "00";
            }else{
                hours = extractDateComponent(hl7SegmentDateField,11, 13);
            }
        }

        //minute
        if (outputDataType.equals("TS10")) {
            matchFound = true;
        }else if (!matchFound) {
            if (hl7SegmentDateField.length() <16) {
                minutes = "00";
            }else{
                minutes = extractDateComponent(hl7SegmentDateField,14, 16);
            }
        }

        //seconds
        if (outputDataType.equals("TS12")) {
            matchFound = true;
        }else if (!matchFound) {
            if (hl7SegmentDateField.length() <16) {
                seconds = "00";
            }else{
                seconds = extractDateComponent(hl7SegmentDateField,17, 19);
            }
        }
        //seconds
        if (outputDataType.equals("TS18")) {
            matchFound = true;
        }else if (!matchFound) {
            if (hl7SegmentDateField.length() <23) {
                milliseconds = "000";
            }else{
                milliseconds = extractDateComponent(hl7SegmentDateField,20, 23);
            }
        }

        return year + month + day + hours + minutes + seconds + milliseconds;
    }

    /**
     * Checks if inputDataType is of type DT or TS, if not  returns an empty string
     * @param inputDataType input datatype
     * @return dataType
     */
    private String mapInputToOutputDataType(String inputDataType) {
        if ("DT".equals(inputDataType)) {
            return "DT8";
        } else if ("TS".equals(inputDataType)) {
            return "TS18";
        }
        return inputDataType;
    }


    /**
     * Extracts date component from hl7DateField
     * @param hl7DateField date string
     * @param startIndex start index
     * @param endIndex end index
     * @return extracted substring
     */
    private String extractDateComponent(String hl7DateField, int startIndex, int endIndex) {
        return hl7DateField.substring(startIndex, endIndex);
    }

}