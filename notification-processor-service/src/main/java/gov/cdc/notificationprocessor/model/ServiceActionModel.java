package gov.cdc.notificationprocessor.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public record ServiceActionModel(String service, String action,
                                 String totalServiceActionPairs, String  serialNumber, String messageProfileID,
                                 String conditionCode, String statusCode, String notes) {
    @JsonCreator
    public ServiceActionModel(@JsonProperty("service") String service,
                              @JsonProperty("action") String action,
                              @JsonProperty("total_service_action_pairs") String totalServiceActionPairs,
                              @JsonProperty("serial_number") String serialNumber,
                              @JsonProperty("message_profile_id") String messageProfileID,
                              @JsonProperty("condition_code") String conditionCode,
                              @JsonProperty("status_code") String statusCode,
                              @JsonProperty("notes") String notes) {
        this.service = service;
        this.action = action;
        this.totalServiceActionPairs = totalServiceActionPairs;
        this.serialNumber = serialNumber;
        this.messageProfileID = messageProfileID;
        this.conditionCode = conditionCode;
        this.statusCode = statusCode;
        this.notes = notes;
    }
    @Override
    public String toString() {
        return "DataTypeModel{" +
                "  service=" + service +
                ", action=" + action +
                ", totalServiceActionPairs=" + totalServiceActionPairs +
                ", serialNumber=" + serialNumber +
                ", messageProfileID= " + messageProfileID +
                ", conditionCode = " + conditionCode +
                ", statusCode = " + statusCode +
                ", notes = " + notes;
    }
}
