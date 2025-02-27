package gov.cdc.notificationprocessor.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import gov.cdc.notificationprocessor.constants.Constants;
import gov.cdc.notificationprocessor.model.ServiceActionModel;
import gov.cdc.notificationprocessor.util.JsonReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

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
        Map<String, String> result = new HashMap<>();
        result.put("mappedService","");
        result.put("mappedAction","");
        result.put("mappedConditionCode","");
        String profile = inputFields.get("message_profile_id");
        Gson gson = new Gson();

        List<ServiceActionModel> serviceActionObjects = loadServiceActionPairFromJson();
        Optional<ServiceActionModel> matchedDataType = findMatch(serviceActionObjects, profile);

        if (matchedDataType.isPresent()) {
            String service = matchedDataType.get().service().trim();
            String action = matchedDataType.get().action().trim();
            String conditionCode = matchedDataType.get().conditionCode().trim();
            if (!service.isEmpty()) {
                result.put("mappedService",matchedDataType.get().service());
            }
            if (!action.isEmpty()) {
                result.put("mappedAction",matchedDataType.get().action());
            }
            if (!conditionCode.isEmpty()) {
                result.put("mappedConditionCode",matchedDataType.get().conditionCode());
            }
        }
        return gson.toJson(result);
    }

    private List<ServiceActionModel> loadServiceActionPairFromJson() {
        String jsonData = JsonReader.readJsonFromResources(Constants.SERVICE_ACTION_JSON);
        try {
            return mapper.readValue(jsonData, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            logger.error("Error occurred while parsing DataTypes.json", e);
            throw new RuntimeException(e);
        }
    }
    private Optional<ServiceActionModel> findMatch(List<ServiceActionModel> model, String messageProfile) {
        return model.stream()
                .filter(dataModel -> dataModel.messageProfileID().equals(messageProfile) && dataModel.statusCode().equals("ACTIVE"))
                .findFirst();
    }

}
