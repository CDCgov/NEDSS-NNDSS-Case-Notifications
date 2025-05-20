package gov.cdc.xmlhl7parserservice.helper;

import gov.cdc.xmlhl7parserservice.model.Obx.ObxRepeatingElement;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Getter
@Setter
public class MessageState {
    private Boolean isSingleProfile = false;
    private String entityIdentifierGroup1 = "";
    private String entityIdentifierGroup2 = "";
    private String nndMessageVersion = "";
    private String nameSpaceIDGroup1 = "";
    private String universalIDGroup1 = "";
    private String universalIDTypeGroup1 = "";
    private String messageType = "other";
    private Boolean genericMMGv20 = false;
    private String nameSpaceIDGroup2 = "";
    private String universalIDGroup2 = "";
    private String universalIDTypeGroup2 = "";

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