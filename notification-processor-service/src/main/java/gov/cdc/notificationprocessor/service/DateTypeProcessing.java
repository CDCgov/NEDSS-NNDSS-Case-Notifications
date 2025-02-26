package gov.cdc.notificationprocessor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gov.cdc.notificationprocessor.constants.Constants;

import gov.cdc.notificationprocessor.model.DataTypeModel;
import gov.cdc.notificationprocessor.util.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of HL7FieldProcessor that reads DataTypes.json from Resources
 * creates list of Java Objects.
 */public class DateTypeProcessing implements Hl7FieldProcessor {

    private final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(DateTypeProcessing.class);

    @Override
    public String process(Map<String, String> inputFields) {
        String segmentField = inputFields.get("hl7Segment");
        String hl7SegmentDateField = inputFields.get("hl7SegmentField");
        String inputDataType = inputFields.get("inputDataType");
        String mmgVersion = inputFields.get("mmgVersion");

        String outputDataType = mapInputToOutputDataType(inputDataType);

        List<DataTypeModel> dataTypeModels = loadDataTypesFromJson();
        Optional<DataTypeModel> matchedDataType = findMatchingDataType(dataTypeModels, mmgVersion, inputDataType);

        if (matchedDataType.isPresent()) {
            outputDataType = matchedDataType.get().getDataType();
        }

        String dateFormatted = formatDateField(hl7SegmentDateField, inputDataType, outputDataType);

        logger.info("Final Formatted date string: {}", dateFormatted);

        return dateFormatted;
    }

    private String formatDateField(String hl7SegmentDateField, String inputDataType, String outputDataType) {
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
        }else if (!matchFound) {
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

    private String mapInputToOutputDataType(String inputDataType) {
        if ("DT".equals(inputDataType)) {
            return "DT8";
        } else if ("TS".equals(inputDataType)) {
            return "TS18";
        }
        return "";
    }

    private List<DataTypeModel> loadDataTypesFromJson() {
        String jsonData = JsonReader.readJsonFromResources(Constants.DATA_TYPES_JSON);
        try {
            return mapper.readValue(jsonData, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            logger.error("Error occurred while parsing DataTypes.json", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Finds first match based on mmgVersion, inputDataType and returns first, from the list of Java Objects
     * @param dataTypeModels Array of JavaObjects from dateTypes.json
     * @param mmgVersion The MMG version to be used for matching
     * @param inputDataType The core data type to be used for matching
     * @return first found match
     */
    private Optional<DataTypeModel> findMatchingDataType(List<DataTypeModel> dataTypeModels, String mmgVersion, String inputDataType) {
        return dataTypeModels.stream()
                .filter(dataModel -> dataModel.getMmgVersion().equals(mmgVersion) && dataModel.getCoreDataType().equals(inputDataType))
                .findFirst();
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
