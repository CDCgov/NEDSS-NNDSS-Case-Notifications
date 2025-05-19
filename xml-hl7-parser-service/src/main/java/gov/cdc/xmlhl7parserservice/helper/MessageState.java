package gov.cdc.xmlhl7parserservice.helper;

import gov.cdc.xmlhl7parserservice.model.Obx.ObxRepeatingElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MessageState {
    private Boolean isSingleProfile = false;
    private String entityIdentifierGroup1 = ""; // val11
    private String entityIdentifierGroup2 = ""; // val21
    private String nndMessageVersion = "";
    private String nameSpaceIDGroup1 = "";
    private String universalIDGroup1 = "";
    private String universalIDTypeGroup1 = "";
    private String messageType = "other";
    private Boolean genericMMGv20 = false;
    private String nameSpaceIDGroup2 = ""; // val12, val22
    private String universalIDGroup2 = ""; //val13, val23
    private String universalIDTypeGroup2 = ""; //val14, val24

    // PID-related state
    private Integer raceIndex = 0;
    private Integer cityIndex = 0;
    private Integer stateIndex = 0;
    private Integer zipcodeIndex = 0;
    private Integer countryIndex = 0;
    private Integer addressTypeIndex = 0;
    private Integer citizenshipTypeIndex = 0;
    private Integer identityReliabilityCodeIndex = 0;

    // NK1-related state
    private Integer nk1RaceIndex = 0;

    // OBR-related state
    private String entityIdentifier2 = "";
    private String obr7 = "";
    private String obr7DataType = "";
    private String obr7QuestionDataTypeNND = "";
    private String reasonForStudyIdentifier2 = "";
    private String reasonForStudyText2 = "";
    private String reasonForStudyNameOfCodingSystem2 = "";
    private String reasonForStudyAlternateIdentifier2 = "";
    private String reasonForStudyAlternateText2 = "";
    private String reasonForStudyNameOfAlternateCodingSystem2 = "";
    private String fillerOrderNumberUniversalID2 = "";
    private String fillerOrderNumberUniversalIDType2 = "";
    private String obrEntityIdentifierGroup1 = "";
    private String obrEntityIdentifierGroup2 = "";
    private String fillerOrderNumberNameSpaceIDGroup1 = "";
    private String fillerOrderNumberNameSpaceIDGroup2 = "";
    private String universalServiceIdentifierGroup1 = "";
    private String universalServiceIdentifierGroup2 = "";
    private String universalServiceIDTextGroup1 = "";
    private String universalServiceIDTextGroup2 = "";
    private String universalServiceIDNameOfCodingSystemGroup1 = "";
    private String universalServiceIDNameOfCodingSystemGroup2 = "";
    private String observationDateTime = "";
    private String resultStatusChgTime = "";

    // OBX-related state variables
    private int obxOrderGroupID = 0;
    private int obxInc = 0;
    private int obx5ValueInc = 0;
    private String obx5ObservationSubID = null;
    private boolean obxFound = false;
    private String messageTypePattern = "CongenitalSyphilis_MMG_V1.0";
    private List<ObxRepeatingElement> obxRepeatingElementArrayList = new ArrayList<>();
    private int drugCounter = 0;
    private int dupRepeatCongenitalCounter = 0;
    private int inv290Inv291Counter = 0;
    private boolean inv177Found = false;
    private String newDate = "";
    private String inv177Date = "";
    private boolean isDefaultNull = true;
    private String hcw = "";
    private int hcwObxInc = -1;
    private int hcwObxOrderGroupId = -1;
    private int hcwObx5ValueInc = -1;
    private boolean hcwTextBeforeCodedInd = false;
    private int obx1Inc = 0;
    private int obx2Inc = 0;
    private boolean INV162RepeatIndicator = false;

    public Boolean getIsSingleProfile() {
        return isSingleProfile;
    }

    public void setIsSingleProfile(Boolean singleProfile) {
        isSingleProfile = singleProfile;
    }

    public String getEntityIdentifierGroup1() {
        return entityIdentifierGroup1;
    }

    public void setEntityIdentifierGroup1(String entityIdentifierGroup1) {
        this.entityIdentifierGroup1 = entityIdentifierGroup1;
    }

    public String getEntityIdentifierGroup2() {
        return entityIdentifierGroup2;
    }

    public void setEntityIdentifierGroup2(String entityIdentifierGroup2) {
        this.entityIdentifierGroup2 = entityIdentifierGroup2;
    }

    public String getNndMessageVersion() {
        return nndMessageVersion;
    }

    public void setNndMessageVersion(String nndMessageVersion) {
        this.nndMessageVersion = nndMessageVersion;
    }

    public String getNameSpaceIDGroup1() {
        return nameSpaceIDGroup1;
    }

    public void setNameSpaceIDGroup1(String nameSpaceIDGroup1) {
        this.nameSpaceIDGroup1 = nameSpaceIDGroup1;
    }

    public String getUniversalIDGroup1() {
        return universalIDGroup1;
    }

    public void setUniversalIDGroup1(String universalIDGroup1) {
        this.universalIDGroup1 = universalIDGroup1;
    }

    public String getUniversalIDTypeGroup1() {
        return universalIDTypeGroup1;
    }

    public void setUniversalIDTypeGroup1(String universalIDTypeGroup1) {
        this.universalIDTypeGroup1 = universalIDTypeGroup1;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Boolean getGenericMMGv20() {
        return genericMMGv20;
    }

    public void setGenericMMGv20(Boolean genericMMGv20) {
        this.genericMMGv20 = genericMMGv20;
    }

    public String getNameSpaceIDGroup2() {
        return nameSpaceIDGroup2;
    }

    public void setNameSpaceIDGroup2(String nameSpaceIDGroup2) {
        this.nameSpaceIDGroup2 = nameSpaceIDGroup2;
    }

    public String getUniversalIDGroup2() {
        return universalIDGroup2;
    }

    public void setUniversalIDGroup2(String universalIDGroup2) {
        this.universalIDGroup2 = universalIDGroup2;
    }

    public String getUniversalIDTypeGroup2() {
        return universalIDTypeGroup2;
    }

    public void setUniversalIDTypeGroup2(String universalIDTypeGroup2) {
        this.universalIDTypeGroup2 = universalIDTypeGroup2;
    }

    // Getters and setters for PID state
    public Integer getRaceIndex() {
        return raceIndex;
    }

    public void setRaceIndex(Integer raceIndex) {
        this.raceIndex = raceIndex;
    }

    public Integer getCityIndex() {
        return cityIndex;
    }

    public void setCityIndex(Integer cityIndex) {
        this.cityIndex = cityIndex;
    }

    public Integer getStateIndex() {
        return stateIndex;
    }

    public void setStateIndex(Integer stateIndex) {
        this.stateIndex = stateIndex;
    }

    public Integer getZipcodeIndex() {
        return zipcodeIndex;
    }

    public void setZipcodeIndex(Integer zipcodeIndex) {
        this.zipcodeIndex = zipcodeIndex;
    }

    public Integer getCountryIndex() {
        return countryIndex;
    }

    public void setCountryIndex(Integer countryIndex) {
        this.countryIndex = countryIndex;
    }

    public Integer getAddressTypeIndex() {
        return addressTypeIndex;
    }

    public void setAddressTypeIndex(Integer addressTypeIndex) {
        this.addressTypeIndex = addressTypeIndex;
    }

    public Integer getCitizenshipTypeIndex() {
        return citizenshipTypeIndex;
    }

    public void setCitizenshipTypeIndex(Integer citizenshipTypeIndex) {
        this.citizenshipTypeIndex = citizenshipTypeIndex;
    }

    public Integer getIdentityReliabilityCodeIndex() {
        return identityReliabilityCodeIndex;
    }

    public void setIdentityReliabilityCodeIndex(Integer identityReliabilityCodeIndex) {
        this.identityReliabilityCodeIndex = identityReliabilityCodeIndex;
    }

    // Getters and setters for NK1 state
    public Integer getNk1RaceIndex() {
        return nk1RaceIndex;
    }

    public void setNk1RaceIndex(Integer nk1RaceIndex) {
        this.nk1RaceIndex = nk1RaceIndex;
    }

    // Getters and setters for OBR state
    public String getEntityIdentifier2() {
        return entityIdentifier2;
    }

    public void setEntityIdentifier2(String entityIdentifier2) {
        this.entityIdentifier2 = entityIdentifier2;
    }

    public String getObr7() {
        return obr7;
    }

    public void setObr7(String obr7) {
        this.obr7 = obr7;
    }

    public String getObr7DataType() {
        return obr7DataType;
    }

    public void setObr7DataType(String obr7DataType) {
        this.obr7DataType = obr7DataType;
    }

    public String getObr7QuestionDataTypeNND() {
        return obr7QuestionDataTypeNND;
    }

    public void setObr7QuestionDataTypeNND(String obr7QuestionDataTypeNND) {
        this.obr7QuestionDataTypeNND = obr7QuestionDataTypeNND;
    }

    public String getReasonForStudyIdentifier2() {
        return reasonForStudyIdentifier2;
    }

    public void setReasonForStudyIdentifier2(String reasonForStudyIdentifier2) {
        this.reasonForStudyIdentifier2 = reasonForStudyIdentifier2;
    }

    public String getReasonForStudyText2() {
        return reasonForStudyText2;
    }

    public void setReasonForStudyText2(String reasonForStudyText2) {
        this.reasonForStudyText2 = reasonForStudyText2;
    }

    public String getReasonForStudyNameOfCodingSystem2() {
        return reasonForStudyNameOfCodingSystem2;
    }

    public void setReasonForStudyNameOfCodingSystem2(String reasonForStudyNameOfCodingSystem2) {
        this.reasonForStudyNameOfCodingSystem2 = reasonForStudyNameOfCodingSystem2;
    }

    public String getReasonForStudyAlternateIdentifier2() {
        return reasonForStudyAlternateIdentifier2;
    }

    public void setReasonForStudyAlternateIdentifier2(String reasonForStudyAlternateIdentifier2) {
        this.reasonForStudyAlternateIdentifier2 = reasonForStudyAlternateIdentifier2;
    }

    public String getReasonForStudyAlternateText2() {
        return reasonForStudyAlternateText2;
    }

    public void setReasonForStudyAlternateText2(String reasonForStudyAlternateText2) {
        this.reasonForStudyAlternateText2 = reasonForStudyAlternateText2;
    }

    public String getReasonForStudyNameOfAlternateCodingSystem2() {
        return reasonForStudyNameOfAlternateCodingSystem2;
    }

    public void setReasonForStudyNameOfAlternateCodingSystem2(String reasonForStudyNameOfAlternateCodingSystem2) {
        this.reasonForStudyNameOfAlternateCodingSystem2 = reasonForStudyNameOfAlternateCodingSystem2;
    }

    public String getFillerOrderNumberUniversalID2() {
        return fillerOrderNumberUniversalID2;
    }

    public void setFillerOrderNumberUniversalID2(String fillerOrderNumberUniversalID2) {
        this.fillerOrderNumberUniversalID2 = fillerOrderNumberUniversalID2;
    }

    public String getFillerOrderNumberUniversalIDType2() {
        return fillerOrderNumberUniversalIDType2;
    }

    public void setFillerOrderNumberUniversalIDType2(String fillerOrderNumberUniversalIDType2) {
        this.fillerOrderNumberUniversalIDType2 = fillerOrderNumberUniversalIDType2;
    }

    public String getObrEntityIdentifierGroup1() {
        return obrEntityIdentifierGroup1;
    }

    public void setObrEntityIdentifierGroup1(String obrEntityIdentifierGroup1) {
        this.obrEntityIdentifierGroup1 = obrEntityIdentifierGroup1;
    }

    public String getObrEntityIdentifierGroup2() {
        return obrEntityIdentifierGroup2;
    }

    public void setObrEntityIdentifierGroup2(String obrEntityIdentifierGroup2) {
        this.obrEntityIdentifierGroup2 = obrEntityIdentifierGroup2;
    }

    public String getFillerOrderNumberNameSpaceIDGroup1() {
        return fillerOrderNumberNameSpaceIDGroup1;
    }

    public void setFillerOrderNumberNameSpaceIDGroup1(String fillerOrderNumberNameSpaceIDGroup1) {
        this.fillerOrderNumberNameSpaceIDGroup1 = fillerOrderNumberNameSpaceIDGroup1;
    }

    public String getFillerOrderNumberNameSpaceIDGroup2() {
        return fillerOrderNumberNameSpaceIDGroup2;
    }

    public void setFillerOrderNumberNameSpaceIDGroup2(String fillerOrderNumberNameSpaceIDGroup2) {
        this.fillerOrderNumberNameSpaceIDGroup2 = fillerOrderNumberNameSpaceIDGroup2;
    }

    public String getUniversalServiceIdentifierGroup1() {
        return universalServiceIdentifierGroup1;
    }

    public void setUniversalServiceIdentifierGroup1(String universalServiceIdentifierGroup1) {
        this.universalServiceIdentifierGroup1 = universalServiceIdentifierGroup1;
    }

    public String getUniversalServiceIdentifierGroup2() {
        return universalServiceIdentifierGroup2;
    }

    public void setUniversalServiceIdentifierGroup2(String universalServiceIdentifierGroup2) {
        this.universalServiceIdentifierGroup2 = universalServiceIdentifierGroup2;
    }

    public String getUniversalServiceIDTextGroup1() {
        return universalServiceIDTextGroup1;
    }

    public void setUniversalServiceIDTextGroup1(String universalServiceIDTextGroup1) {
        this.universalServiceIDTextGroup1 = universalServiceIDTextGroup1;
    }

    public String getUniversalServiceIDTextGroup2() {
        return universalServiceIDTextGroup2;
    }

    public void setUniversalServiceIDTextGroup2(String universalServiceIDTextGroup2) {
        this.universalServiceIDTextGroup2 = universalServiceIDTextGroup2;
    }

    public String getUniversalServiceIDNameOfCodingSystemGroup1() {
        return universalServiceIDNameOfCodingSystemGroup1;
    }

    public void setUniversalServiceIDNameOfCodingSystemGroup1(String universalServiceIDNameOfCodingSystemGroup1) {
        this.universalServiceIDNameOfCodingSystemGroup1 = universalServiceIDNameOfCodingSystemGroup1;
    }

    public String getUniversalServiceIDNameOfCodingSystemGroup2() {
        return universalServiceIDNameOfCodingSystemGroup2;
    }

    public void setUniversalServiceIDNameOfCodingSystemGroup2(String universalServiceIDNameOfCodingSystemGroup2) {
        this.universalServiceIDNameOfCodingSystemGroup2 = universalServiceIDNameOfCodingSystemGroup2;
    }

    public String getObservationDateTime() {
        return observationDateTime;
    }

    public void setObservationDateTime(String observationDateTime) {
        this.observationDateTime = observationDateTime;
    }

    public String getResultStatusChgTime() {
        return resultStatusChgTime;
    }

    public void setResultStatusChgTime(String resultStatusChgTime) {
        this.resultStatusChgTime = resultStatusChgTime;
    }

    // Getters and setters for OBX variables
    public int getObxOrderGroupID() {
        return obxOrderGroupID;
    }

    public void setObxOrderGroupID(int obxOrderGroupID) {
        this.obxOrderGroupID = obxOrderGroupID;
    }

    public int getObxInc() {
        return obxInc;
    }

    public void setObxInc(int obxInc) {
        this.obxInc = obxInc;
    }

    public int getObx5ValueInc() {
        return obx5ValueInc;
    }

    public void setObx5ValueInc(int obx5ValueInc) {
        this.obx5ValueInc = obx5ValueInc;
    }

    public String getObx5ObservationSubID() {
        return obx5ObservationSubID;
    }

    public void setObx5ObservationSubID(String obx5ObservationSubID) {
        this.obx5ObservationSubID = obx5ObservationSubID;
    }

    public boolean isObxFound() {
        return obxFound;
    }

    public void setObxFound(boolean obxFound) {
        this.obxFound = obxFound;
    }

    public String getMessageTypePattern() {
        return messageTypePattern;
    }

    public void setMessageTypePattern(String messageTypePattern) {
        this.messageTypePattern = messageTypePattern;
    }

    public List<ObxRepeatingElement> getObxRepeatingElementArrayList() {
        return obxRepeatingElementArrayList;
    }

    public void setObxRepeatingElementArrayList(List<ObxRepeatingElement> obxRepeatingElementArrayList) {
        this.obxRepeatingElementArrayList = obxRepeatingElementArrayList;
    }

    public int getDrugCounter() {
        return drugCounter;
    }

    public void setDrugCounter(int drugCounter) {
        this.drugCounter = drugCounter;
    }

    public int getDupRepeatCongenitalCounter() {
        return dupRepeatCongenitalCounter;
    }

    public void setDupRepeatCongenitalCounter(int dupRepeatCongenitalCounter) {
        this.dupRepeatCongenitalCounter = dupRepeatCongenitalCounter;
    }

    public int getInv290Inv291Counter() {
        return inv290Inv291Counter;
    }

    public void setInv290Inv291Counter(int inv290Inv291Counter) {
        this.inv290Inv291Counter = inv290Inv291Counter;
    }

    public boolean isInv177Found() {
        return inv177Found;
    }

    public void setInv177Found(boolean inv177Found) {
        this.inv177Found = inv177Found;
    }

    public String getNewDate() {
        return newDate;
    }

    public void setNewDate(String newDate) {
        this.newDate = newDate;
    }

    public String getInv177Date() {
        return inv177Date;
    }

    public void setInv177Date(String inv177Date) {
        this.inv177Date = inv177Date;
    }

    public boolean isDefaultNull() {
        return isDefaultNull;
    }

    public void setDefaultNull(boolean defaultNull) {
        isDefaultNull = defaultNull;
    }

    public String getHcw() {
        return hcw;
    }

    public void setHcw(String hcw) {
        this.hcw = hcw;
    }

    public int getHcwObxInc() {
        return hcwObxInc;
    }

    public void setHcwObxInc(int hcwObxInc) {
        this.hcwObxInc = hcwObxInc;
    }

    public int getHcwObxOrderGroupId() {
        return hcwObxOrderGroupId;
    }

    public void setHcwObxOrderGroupId(int hcwObxOrderGroupId) {
        this.hcwObxOrderGroupId = hcwObxOrderGroupId;
    }

    public int getHcwObx5ValueInc() {
        return hcwObx5ValueInc;
    }

    public void setHcwObx5ValueInc(int hcwObx5ValueInc) {
        this.hcwObx5ValueInc = hcwObx5ValueInc;
    }

    public boolean isHcwTextBeforeCodedInd() {
        return hcwTextBeforeCodedInd;
    }

    public void setHcwTextBeforeCodedInd(boolean hcwTextBeforeCodedInd) {
        this.hcwTextBeforeCodedInd = hcwTextBeforeCodedInd;
    }

    public int getObx1Inc() {
        return obx1Inc;
    }

    public void setObx1Inc(int obx1Inc) {
        this.obx1Inc = obx1Inc;
    }

    public int getObx2Inc() {
        return obx2Inc;
    }

    public void setObx2Inc(int obx2Inc) {
        this.obx2Inc = obx2Inc;
    }

    public boolean isINV162RepeatIndicator() {
        return INV162RepeatIndicator;
    }

    public void setINV162RepeatIndicator(boolean INV162RepeatIndicator) {
        this.INV162RepeatIndicator = INV162RepeatIndicator;
    }

    public void reset() {
        isSingleProfile = false;
        entityIdentifierGroup1 = "";
        entityIdentifierGroup2 = "";
        nndMessageVersion = "";
        nameSpaceIDGroup1 = "";
        universalIDGroup1 = "";
        universalIDTypeGroup1 = "";
        messageType = "other";
        genericMMGv20 = false;
        nameSpaceIDGroup2 = "";
        universalIDGroup2 = "";
        universalIDTypeGroup2 = "";

        raceIndex = 0;
        cityIndex = 0;
        stateIndex = 0;
        zipcodeIndex = 0;
        countryIndex = 0;
        addressTypeIndex = 0;
        citizenshipTypeIndex = 0;
        identityReliabilityCodeIndex = 0;
        nk1RaceIndex = 0;

        // Reset OBR state
        entityIdentifier2 = "";
        obr7 = "";
        obr7DataType = "";
        obr7QuestionDataTypeNND = "";
        reasonForStudyIdentifier2 = "";
        reasonForStudyText2 = "";
        reasonForStudyNameOfCodingSystem2 = "";
        reasonForStudyAlternateIdentifier2 = "";
        reasonForStudyAlternateText2 = "";
        reasonForStudyNameOfAlternateCodingSystem2 = "";
        fillerOrderNumberUniversalID2 = "";
        fillerOrderNumberUniversalIDType2 = "";
        obrEntityIdentifierGroup1 = "";
        obrEntityIdentifierGroup2 = "";
        fillerOrderNumberNameSpaceIDGroup1 = "";
        fillerOrderNumberNameSpaceIDGroup2 = "";
        universalServiceIdentifierGroup1 = "";
        universalServiceIdentifierGroup2 = "";
        universalServiceIDTextGroup1 = "";
        universalServiceIDTextGroup2 = "";
        universalServiceIDNameOfCodingSystemGroup1 = "";
        universalServiceIDNameOfCodingSystemGroup2 = "";
        observationDateTime = "";
        resultStatusChgTime = "";

        // Reset OBX variables
        obxOrderGroupID = 0;
        obxInc = 1;
        obx5ValueInc = 0;
        obx5ObservationSubID = null;
        obxFound = false;
        messageTypePattern = "CongenitalSyphilis_MMG_V1.0";
        obxRepeatingElementArrayList = new ArrayList<>();
        drugCounter = 0;
        dupRepeatCongenitalCounter = 0;
        inv290Inv291Counter = 0;
        inv177Found = false;
        newDate = "";
        inv177Date = "";
        isDefaultNull = true;
        hcw = "";
        hcwObxInc = -1;
        hcwObxOrderGroupId = -1;
        hcwObx5ValueInc = -1;
        hcwTextBeforeCodedInd = false;
        obx1Inc = 0;
        obx2Inc = 0;
        INV162RepeatIndicator = false;
    }
} 