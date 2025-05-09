package gov.cdc.xmlhl7parserservice.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Entity;

@Entity
@Table(name = "NNDSS_SERVICE_ACTION_PAIR_LOOKUP")
public class ServiceActionPairModel {
    @Column(name = "service")
    private String service;
    @Column(name = "action")
    private String action;
    @Column(name = "total_service_action_pairs")
    private int totalServiceActionPairs;
    @Id
    @Column(name = "serial_number")
    private int serialNumber;
    @Column(name = "message_profile_id")
    private String messageProfileId;
    @Column(name = "condition_code")
    private String conditionCode;
    @Column(name = "status_code")
    private String statusCode;
    @Column(name = "notes")
    private String notes;
    @Column(name = "concept_code")
    private String conceptCode;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public int getTotalServiceActionPairs() {
        return totalServiceActionPairs;
    }

    public void setTotalServiceActionPairs(int totalServiceActionPairs) {
        this.totalServiceActionPairs = totalServiceActionPairs;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(int serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getMessageProfileId() {
        return messageProfileId;
    }

    public void setMessageProfileId(String messageProfileId) {
        this.messageProfileId = messageProfileId;
    }

    public String getConditionCode() {
        return conditionCode;
    }

    public void setConditionCode(String conditionCode) {
        this.conditionCode = conditionCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getConceptCode() {
        return conceptCode;
    }

    public void setConceptCode(String conceptCode) {
        this.conceptCode = conceptCode;
    }
}