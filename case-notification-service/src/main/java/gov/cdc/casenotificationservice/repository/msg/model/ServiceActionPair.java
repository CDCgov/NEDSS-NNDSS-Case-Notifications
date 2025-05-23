package gov.cdc.casenotificationservice.repository.msg.model;


import lombok.Data;

@Data
//@NoArgsConstructor
//@AllArgsConstructor
//@Entity
//@Table(name = "SERVICE_ACTION_PAIR")
public class ServiceActionPair {


//    @Column(name = "SERVICE", length = 50)
    private String service;

//    @Column(name = "ACTION", length = 50)
    private String action;

//    @Column(name = "TOTAL_SERVICE_ACTION_PAIRS")
    private Integer totalServiceActionPairs;

//    @Column(name = "SERIAL_NUMBER")
    private Integer serialNumber;

//    @Column(name = "MESSAGE_PROFILE_ID", length = 50)
    private String messageProfileId;

//    @Column(name = "CONDITION_CODE")
    private Integer conditionCode;

//    @Column(name = "STATUS_CODE", length = 50)
    private String statusCode;

//    @Column(name = "NOTES", length = 256)
    private String notes;

//    @Column(name = "CONCEPT_CODE")
    private Integer conceptCode;
}