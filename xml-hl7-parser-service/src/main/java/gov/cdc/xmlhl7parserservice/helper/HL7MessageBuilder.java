package gov.cdc.xmlhl7parserservice.helper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;

import ca.uhn.hl7v2.model.Primitive;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v25.datatype.*;
import ca.uhn.hl7v2.model.v25.datatype.DTM;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.*;

import gov.cdc.xmlhl7parserservice.constants.Constants;

import gov.cdc.xmlhl7parserservice.model.*;
import gov.cdc.xmlhl7parserservice.repository.IDataTypeLookupRepository;
import gov.cdc.xmlhl7parserservice.repository.IServiceActionPairRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Service
public class HL7MessageBuilder {

//    private NBSNNDIntermediaryMessage nbsnndIntermediaryMessage;

    private final IServiceActionPairRepository iServiceActionPairRepository;
    private final IDataTypeLookupRepository iDataTypeLookupRepository;
    private final DataTypeProcessor dataTypeProcessor;

    @Autowired
    public HL7MessageBuilder(
            IServiceActionPairRepository iServiceActionPairRepository,
            IDataTypeLookupRepository iDataTypeLookupRepository,
            DataTypeProcessor dataTypeProcessor
    ) {
        this.iServiceActionPairRepository = iServiceActionPairRepository;
        this.iDataTypeLookupRepository = iDataTypeLookupRepository;
        this.dataTypeProcessor = dataTypeProcessor;
    }

    //initialize variables
    Boolean isSingleProfile = false;
    String entityIdentifierGroup1 = "";
    String entityIdentifierGroup2 = "";
    String nndMessageVersion = "";
    String nameSpaceIDGroup1 = "";
    String universalIDGroup1 = "";
    String universalIDTypeGroup1 = "";
    String messageType = "";
    boolean isDefaultNull= true;
    Boolean genericMMGv20 = false;
    String nameSpaceIDGroup2 = "";
    String universalIDGroup2 = "";
    String universalIDTypeGroup2 = "";
    String newDate = "";
    String inv177Date = "";
    private String stateLocalID = "";
    private Integer raceIndex = 0;
    private Integer cityIndex = 0;
    private Integer stateIndex = 0;
    private Integer zipcodeIndex = 0;
    private Integer countryIndex = 0;
    private Integer addressTypeIndex = 0;
    private Integer citizenshipTypeIndex = 0;
    private Integer identityReliabilityCodeIndex = 0;
    private Integer nk1RaceInc = 0;

    private final HashMap<String, String> obxRepeatingElementArray = new HashMap<>();
    //Repeating block for lab
    int drugCounter = 0;
    int dupRepeatCongenitalCounter = 0;
    int inv290Inv291Counter = 0;
    int inv290Inv291Counter1 = 0;
    int inv290Inv291Counter2 = 0;
    boolean inv177Found = false;
    int std121ObxInc = -1;
    int std121obxOrderGroupId = 0;
    int std121ObsValue = -1;
    String NBS246observationSubID = "";
    String std300 = "";

    int maxObr = 0;
    int maxObx = 0;
    //HCW Specific fields
    boolean hcwTextBeforeCodedInd=false;
    String hcw="";
    int hcwTextcounter=-1;
    int hcwObxInc=-1;
    int obx2Inc = 0;
    int obx1Inc = 0;
    int hcwObxOrderGroupId=-1;
    int hcwObx5ValueInc=-1;
    int raceCounterNK1 = 0;
    String OTH_COMP_TEXT = "";
    String OTH_COMP_REPLACE ="";
    int complicationCounter = 0;
    String OTH_SANDS_TEXT = "";
    String OTH_SANDS_REPLACE ="";
    int signSymptomsCounter = 0;
    boolean INV162RepeatIndicator = false;
    private String fillerOrderNumberUniversalID2 = "";
    private String fillerOrderNumberUniversalIDType2 = "";
    private String obrEntityIdentifierGroup1 = "";
    private String getObrEntityIdentifierGroup2 ="";
    private String fillerOrderNumberNameSpaceIDGroup1 = "";
    private String fillerOrderNumberNameSpaceIDGroup2 = "";
    private String universalServiceIdentifierGroup1 = "";
    private String universalServiceIdentifierGroup2 = "";
    private String universalServiceIDTextGroup1 = "";
    private String universalServiceIDTextGroup2 = "";
    private String universalServiceIDNameOfCodingSystemGroup1 ="";
    private String universalServiceIDNameOfCodingSystemGroup2 ="";

    List<DynamicRepeatMulti>  dynamicRepeatMultiArray = new ArrayList<>();
    DiscreteMulti discreteMulti = new DiscreteMulti();
    List<DiscreteMulti> repeatMultiArray = new ArrayList<>();
    List<DiscreteRepeat> discreteRepeatArray = new ArrayList<>();
    List<DiscreteRepeat> discreteRepeatSNTypeArray = new ArrayList<>();


    private static final Logger logger = LoggerFactory.getLogger(HL7MessageBuilder.class);

    // TODO - Extract methods to helper classes
    public String parseXml(NBSNNDIntermediaryMessage nbsnndIntermediaryMessage) throws HL7Exception {
//        nbsnndIntermediaryMessage = nbsnndIntermediaryMessage;
        ORU_R01 oruMessage = new ORU_R01();
        MSH msh = oruMessage.getMSH();
        PID pid = oruMessage.getPATIENT_RESULT().getPATIENT().getPID();
        OBR obr = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR();
        NK1 nk1 = oruMessage.getPATIENT_RESULT().getPATIENT().getNK1();
        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0);
        ORU_R01_ORDER_OBSERVATION obx = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION();

        //set static fields
        try {
            msh.getFieldSeparator().setValue(Constants.FIELD_SEPARATOR);
            msh.getEncodingCharacters().setValue(Constants.ENCODING_CHARACTERS);
            //msh.getMessageType().getMessageCode().setValue(Constants.MESSAGE_CODE);
            //msh.getMessageType().getTriggerEvent().setValue(Constants.MESSAGE_TRIGGER_EVENT);
            pid.getSetIDPID().setValue("1");
            pid.getPatientName(0).getNameTypeCode().setValue("S");
            obr.getObr1_SetIDOBR().setValue("1");
        } catch (DataTypeException e) {
            throw new RuntimeException(e);
        }

        for (MessageElement messageElement: nbsnndIntermediaryMessage.getMessageElement()){
            String segmentField = messageElement.getHl7SegmentField().trim();
            if (segmentField.startsWith("MSH")){
                processMSHFields(messageElement, msh);
            }else if (segmentField.startsWith("PID")) {
                processPIDFields(messageElement, pid);
            }else if (segmentField.startsWith("NK1")){
                processNK1Fields(messageElement, nk1);
            }else if (segmentField.startsWith("OBR")){
                processOBRFields(messageElement, obr);
            }
//            else if (segmentField.startsWith("OBX")) {
//                processOBXFields(messageElement,obx);
//            }

            if (messageElement.getQuestionIdentifierNND().trim().equals("STD300")){
                std300 = messageElement.getDataElement().getStDataType().getStringData().trim();
                if (!std300.isEmpty()){
                    std300 = std300.replace("\\","\\E\\");
                    std300 = std300.replace("|","\\F\\");
                    std300 = std300.replace("~","\\R\\");
                    std300 = std300.replace("^","\\S\\");
                    std300 = std300.replace("&","\\T\\");
                }

            }else if (messageElement.getQuestionIdentifierNND().trim().equals("NBS246")){
                if (!messageElement.getDataElement().getCweDataType().getCweCodedValue().trim().equals("C")){
                    NBS246observationSubID = messageElement.getObservationSubID().trim();
                }
            }else if (messageElement.getQuestionIdentifierNND().trim().equals("223366009") && genericMMGv20){
                String cweCodedValue = messageElement.getDataElement().getCweDataType().getCweCodedValue().trim();
                switch (cweCodedValue) {
                    case "Y" -> hcw = "; HCWYes";
                    case "N" -> hcw = "; HCWNo";
                    case "UNK" -> hcw = "; HCWUnknown";
                }

                if (hcwTextBeforeCodedInd) {

                    TX textDataType = (TX)obx.getOBSERVATION(hcwObxOrderGroupId).getOBX().getObservationValue(hcwObx5ValueInc).getData();
                    textDataType.setValue(hcw);
                    obx.getOBSERVATION(hcwObxOrderGroupId).getOBX().getObx5_ObservationValue(hcwObx5ValueInc).setData(textDataType);
                }else{
                    int obxOrderGroupId;
                    if (messageElement.getOrderGroupId().trim().equals("1")){
                        obxOrderGroupId = 0;
                    }else{
                        obxOrderGroupId = 1;
                    }
                    obx.getOBSERVATION(1).getOBX().getSetIDOBX().setValue(String.valueOf(obx2Inc+1));
                    obx.getOBSERVATION(1).getOBX().getObservationResultStatus().setValue("F");
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getValueType().setValue("TX");
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationIdentifier().getIdentifier().setValue("77999-1");
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationIdentifier().getNameOfCodingSystem().setValue("2.16.840.1.113883.6.1");
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationIdentifier().getText().setValue("Comment");
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationIdentifier().getAlternateIdentifier().setValue("INV886");
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationIdentifier().getAlternateText().setValue("Notification Comments to CDC");
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");

                    TX textData = (TX) obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationValue(0).getData();
                    textData.setValue(hcw);
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationValue(0).setData(textData);

                    obx2Inc +=1;
                }
            }
            //STD specific code for combining STD121
            else if (messageElement.getQuestionIdentifierNND().trim().equals("STD121")){
                if (std121ObxInc==-1){
                    if (messageElement.getOrderGroupId().trim().equals("1")){
                        std121ObxInc = obx1Inc;
                        std121obxOrderGroupId = 0;
                        obx1Inc += 1;
                    }else{
                        std121ObxInc = obx2Inc;
                        std121obxOrderGroupId = 1;
                        obx2Inc += 1;
                    }
                }
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getSetIDOBX().setValue(String.valueOf(std121ObxInc+1));
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getValueType().setValue("CWE");
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationIdentifier().getIdentifier().setValue(messageElement.getQuestionIdentifierNND().trim());
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationIdentifier().getNameOfCodingSystem().setValue(messageElement.getQuestionOID().trim());
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationIdentifier().getText().setValue(messageElement.getQuestionLabelNND().trim());
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationIdentifier().getAlternateIdentifier().setValue(messageElement.getQuestionIdentifier().trim());
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationIdentifier().getAlternateText().setValue(messageElement.getQuestionLabelNND().trim());
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");
                if (!messageElement.getObservationSubID().trim().isEmpty()){
                    obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationSubID().setValue(messageElement.getObservationSubID().trim());
                }else if (!messageElement.getQuestionGroupSeqNbr().trim().isEmpty()){
                    obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationSubID().setValue(messageElement.getQuestionGroupSeqNbr().trim());
                }

                std121ObsValue += 1;
                String codedValue = "";
                String codedValueDescription = "";
                String codedValueCodingSystem = "";
                String localCodedValue = "";
                String localCodedValueDescription = "";
                String localCodedValueCodingSystem = "";
                String originalOtherText = "";
                if (!messageElement.getDataElement().getCweDataType().getCweCodedValue().trim().isEmpty()){
                    codedValue = messageElement.getDataElement().getCweDataType().getCweCodedValue().trim();
                }
                if (!messageElement.getDataElement().getCweDataType().getCweCodedValueDescription().trim().isEmpty()){
                    codedValueDescription = messageElement.getDataElement().getCweDataType().getCweCodedValue().trim();
                }
                if (!messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem().trim().isEmpty()){
                    codedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem().trim();
                }
                if (!messageElement.getDataElement().getCweDataType().getCweLocalCodedValue().trim().isEmpty()){
                    localCodedValue = messageElement.getDataElement().getCweDataType().getCweLocalCodedValue().trim();
                }
                if (!messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription().trim().isEmpty()){
                    localCodedValueDescription = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription().trim();
                }
                if (!messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem().trim().isEmpty()){
                    localCodedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem().trim();
                }
                if (!messageElement.getDataElement().getCweDataType().getCweOriginalText().trim().isEmpty()){
                    originalOtherText = messageElement.getDataElement().getCweDataType().getCweOriginalText().trim();
                }
                TX textData = (TX)obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationValue(std121ObsValue).getData();
                String value = codedValue + "^"+codedValueDescription+"^"+codedValueCodingSystem+"^"+localCodedValue+"^"+
                        localCodedValueDescription+"^" +localCodedValueCodingSystem+"^"+originalOtherText;
                textData.setValue(value);
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationValue(std121ObsValue).setData(textData);
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationResultStatus().setValue("F");
                
            }

            if(messageElement.getIndicatorCd() != null) {
                if(messageElement.getIndicatorCd().contains("ParentRepeatBlock")){
                    mapToDynamicParentRptToRpt(messageElement,  obx2Inc, messageType, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0));
                } else if(messageElement.getIndicatorCd().contains("DiscAsRepeat")){
                    mapToDynamicRootlDiscToRepeat(messageElement, obx2Inc, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0));
                } else if(messageElement.getIndicatorCd().contains("DiscCdToMultiOBS")){
                    mapToDisRepeat(messageElement, obx2Inc, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0));
                } else if(messageElement.getIndicatorCd().contains("RepeatToMultiNND")){
                    mapToRepeatToMultiNND(messageElement, obx2Inc, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0));
                }
            }
             if ((messageElement.getHl7SegmentField().equals("OBX-3.0")
                     || messageElement.getHl7SegmentField().equals("OBX-5.9"))
                     && messageElement.getQuestionMap() != null && messageElement.getQuestionMap().contains("|")) {
                mapToQuestionMap(messageElement, obx2Inc, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0));
            }

             if (messageElement.getHl7SegmentField().equals("OBX-3.0") ) {
                 processOBXFields(messageElement,obx);
             }


            if(messageType.contains("Arbo_Case_Map_v1.0") && isDefaultNull && !stateLocalID.isEmpty()) {
                OBX obxForArboCaseMapV1 = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBSERVATION(maxObr).getOBX();

                obxForArboCaseMapV1.getObx1_SetIDOBX().setValue(String.valueOf(maxObx+1));
                obxForArboCaseMapV1.getValueType().setValue("ST");
                obxForArboCaseMapV1.getObservationIdentifier().getIdentifier().setValue("INV173");
                obxForArboCaseMapV1.getObservationIdentifier().getText().setValue("State Case ID");
                obxForArboCaseMapV1.getObservationIdentifier().getNameOfCodingSystem().setValue("2.16.840.1.114222.4.5.232");

                obxForArboCaseMapV1.getObservationIdentifier().getAlternateIdentifier().setValue("INV173");
                obxForArboCaseMapV1.getObservationIdentifier().getAlternateText().setValue("State Case ID");
                obxForArboCaseMapV1.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");
                // obxForArboCaseMapV1.getObservationIdentifier().getIdentifier().setValue(stateLocalID);
                Varies value = obxForArboCaseMapV1.getObservationValue(0);
                ST st = new ST(oruMessage);
                st.setValue(stateLocalID);
                value.setData(st);

                obxForArboCaseMapV1.getObservationResultStatus().setValue("F");

//            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBSERVATION(maxObr).getOBX().getObx1_SetIDOBX().setValue(String.valueOf(maxObx+1));
//            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBSERVATION(maxObr).getOBX().getValueType().setValue("ST");
//            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBSERVATION(maxObr).getOBX().getObservationIdentifier().getIdentifier().setValue("INV173");
//            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBSERVATION(maxObr).getOBX().getObservationIdentifier().getText().setValue("State Case ID");
//            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBSERVATION(maxObr).getOBX().getObservationIdentifier().getNameOfCodingSystem().setValue("2.16.840.1.114222.4.5.232");
//
//            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBSERVATION(maxObr).getOBX().getObservationIdentifier().getAlternateIdentifier().setValue("INV173");
//            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBSERVATION(maxObr).getOBX().getObservationIdentifier().getAlternateText().setValue("State Case ID");
//            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBSERVATION(maxObr).getOBX().getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");
//            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBSERVATION(maxObr).getOBX().getObservationIdentifier().getIdentifier().setValue(stateLocalID);
//            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBSERVATION(maxObr).getOBX().getObservationResultStatus().setValue("F");

            }

            // TODO - Remaining implementation for situation outside TB investigation
        /* This code will cover the situation outside TB investigation (In TB question_identifer='INV121'
	   and question_identifier_nnd='INV177' and question is populated from frontend)
	   where INV177 is not in the intermediate message, this is applicable to only V2 Pages. Excludes Arbo, Gen V1, varicella*/
            if (!inv177Found && inv177Date != null && !inv177Date.isEmpty() && !messageType.contains("TB_Case_Map_v2.0")
                    && !messageType.contains("Arbo_Case_Map_v1.0") && !messageType.contains("Gen_Case_Map_v1.0") && !messageType.contains("Var_Case_Map_v2.0")) {

                OBX obxForGenV2 = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBSERVATION(1).getOBX();

                obxForGenV2.getObx1_SetIDOBX().setValue(String.valueOf(obx2Inc + 1));
                obxForGenV2.getValueType().setValue("DT");
                obxForGenV2.getObservationResultStatus().setValue("F");
                obxForGenV2.getObservationIdentifier().getIdentifier().setValue("77970-2");
                obxForGenV2.getObservationIdentifier().getText().setValue("Date First Reported PHD");
                obxForGenV2.getObservationIdentifier().getNameOfCodingSystem().setValue("2.16.840.1.113883.6.1");
                obxForGenV2.getObservationIdentifier().getAlternateIdentifier().setValue("INV177");
                obxForGenV2.getObservationIdentifier().getAlternateText().setValue("Date First Reported PHD");
                obxForGenV2.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");

                Varies value = obxForGenV2.getObservationValue(0);
                DT dt = new DT(oruMessage);
                dt.setValue(inv177Date);
                value.setData(dt);
            }

            /*This code should execute for Gen V1 guides, exculdes varicella and Arbo*/
            if (!inv177Found && inv177Date != null && !inv177Date.isEmpty() && messageType.startsWith("Gen_Case_Map_v1.0")) {

                OBX obxForGenV1 = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBSERVATION(1).getOBX();

                obxForGenV1.getObx1_SetIDOBX().setValue(String.valueOf(obx2Inc + 1));
                obxForGenV1.getValueType().setValue("TS");
                obxForGenV1.getObservationResultStatus().setValue("F");

                obxForGenV1.getObservationIdentifier().getIdentifier().setValue("INV177");
                obxForGenV1.getObservationIdentifier().getText().setValue("Date First Reported PHD");
                obxForGenV1.getObservationIdentifier().getNameOfCodingSystem().setValue("2.16.840.1.114222.4.5.232");

                obxForGenV1.getObservationIdentifier().getAlternateIdentifier().setValue("INV177");
                obxForGenV1.getObservationIdentifier().getAlternateText().setValue("Date First Reported PHD");
                obxForGenV1.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");

                Varies value = obxForGenV1.getObservationValue(0);
                TS ts = new TS(oruMessage);
                ts.getTime().setValue(inv177Date + "000000.000"); //for TS18 format
                value.setData(ts);
            }

            // TODO - Verify NND_ORU_v2.0 if loop is not needed as it uses OBR[1] and .getOBR() doesn't take any
            // arguments when it comes to HAPI library

        }

        int	labObrCounter = 1;
        EIElement eiType = new EIElement();

        ParentLink parentLink = new ParentLink();
        String cachedOBX3data = "";

        for (LabReportEvent labReportEvent: nbsnndIntermediaryMessage.getLabReportEvent()){
            int obrCounter = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATIONReps();
            labObrCounter = obrCounter + 1;
            int obxSubIdCounter = 1;
            
            mapLabReportEventToOBR(
                    nbsnndIntermediaryMessage,
                    labReportEvent,
                    obrCounter,
                    labObrCounter,
                    messageType,
                    0,
                    eiType,
                    parentLink,
                    obxSubIdCounter,
                    cachedOBX3data,
                    oruMessage
            );
        }

//        for(int i = 0; i < oruMessage.getPATIENT_RESULT().getORDER_OBSERVATIONReps(); i++) {
//            for(int j = 0; j < oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATIONReps(); j++) {
//                for(OBX obx : oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX()) {
//
//                }
//            }
//
//        }

        for(int i = 0; i < oruMessage.getPATIENT_RESULT().getORDER_OBSERVATIONReps(); i++) {
            for(int j = 0; j < oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATIONReps(); j++) {
                String alternateIdentifier = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getAlternateIdentifier().getValue();

                if(messageType.contains("CongenitalSyphilis_MMG_V1.0")) {
                    Set<String> validAlternateIds = Set.of("LAB588", "INV290", "INV291", "STD123", "LAB167", "STD123_1");

                    if (validAlternateIds.contains(alternateIdentifier)) {
                        int subIdCounter;
                        try {
                            subIdCounter = Integer.parseInt(oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationSubID().getValue());
                        } catch (NumberFormatException e) {
                            // TODO check this value
                            subIdCounter = -1;
                        }
                        if (subIdCounter < 0) {
                            int newSubidCounter = -subIdCounter;
                            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationSubID().setValue(Integer.toString(dupRepeatCongenitalCounter + newSubidCounter));
                        }
                    }
                    else {
                        if("STD122".equals(alternateIdentifier) || "STD123".equals(alternateIdentifier) || "STD126".equals(alternateIdentifier)) {
                            if (inv290Inv291Counter1 == 0) {
                                inv290Inv291Counter++;
                                inv290Inv291Counter1 = inv290Inv291Counter;
                            }
                            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationSubID().setValue(Integer.toString(inv290Inv291Counter1));
                        } else if ("STD124".equals(alternateIdentifier) || "STD125".equals(alternateIdentifier)) {
                            if (inv290Inv291Counter2 == 0) {
                                inv290Inv291Counter++;
                                inv290Inv291Counter2 = inv290Inv291Counter;
                            }
                            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationSubID().setValue(Integer.toString(inv290Inv291Counter2));
                        } else if ("STD121".equals(alternateIdentifier)) {
                            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationSubID().setValue("");
                        }
                    }
                    
                    String obxValue = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(0).toString();
                    String obxIdIdentifier = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getIdentifier().getValue();
                    ST stType = (ST) oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(0).getData();
                    ST stType1 = (ST) oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(1).getData();

                    int observationValue1Size  = ((ST) oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(1).getData()).getValue().length();

                    if(obxValue != null && obxValue.contains("Other Drugs Used^2.16.840.1.114222.4.5.274") && std300 != null && !std300.isEmpty()) {
                        stType.setValue(obxValue + "^^^^^^" + std300);
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(0).setData(stType);
                    } else if ("67187-5".equals(obxIdIdentifier) && obxValue != null && obxValue.contains(OTH_COMP_REPLACE)) {
                        if (OTH_COMP_TEXT != null && !OTH_COMP_TEXT.isEmpty()) {
                            stType.setValue(OTH_COMP_REPLACE + "^^^^^^" + OTH_COMP_TEXT);
                            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(0).setData(stType);
                        } else {
                            stType.setValue("OTH^other^2.16.840.1.113883.5.1008");
                            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(0).setData(stType);
                        }
                        stType1.setValue("");
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(1).setData(stType1);
                    } else if ("56831-1".equals(obxIdIdentifier) && obxValue != null && obxValue.contains(OTH_SANDS_REPLACE)) {
                        stType.setValue(OTH_SANDS_REPLACE + "^^^^^^" + OTH_SANDS_TEXT);
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(0).setData(stType);

                        stType1.setValue("");
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(1).setData(stType1);
                    } else if ("56831-1".equals(obxIdIdentifier) && observationValue1Size > 0) {
                        stType1.setValue("");
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(1).setData(stType1);
                    }
                }
            }
        }

        logger.info("Final message: {} ", oruMessage.getMessage());
        System.err.println("Final message...: " + oruMessage);
        // based on message type
        String base64EncodedString = encodeToBase64(oruMessage.getMessage().toString());
        //based on the message type and
//        System.err.println("Final message is..." + base64EncodedString);

        //TODO - connector.persistNotification(base64EncodedString,Constants.NETSS_TRANSPORT_Q_OUT_TABLE);
        return oruMessage.toString();
    }

    private void mapToRepeatToMultiNND(MessageElement messageElement, int obx2Inc, ORU_R01_ORDER_OBSERVATION orderObservation) throws DataTypeException {
        String indPartMain = "";
        String indPart1 = "";
        String questPart1 = "";
        String questPart2 = "";
        String questPart3 = "";
        String questPart4 = "";
        String questPart5 = "";
        String questPart6 = "";

        String otherText = "";
        int obx4Counter = 1;
        String unkcode = "";
        String mappedValue = "";
        String indicatorCode = messageElement.getIndicatorCd();
        int obsCounter = 0;
        int counter = 0;
        String output = "";
        String originalString = "";

        int startInd = indicatorCode.indexOf("|");
        int discCdIndex = indicatorCode.indexOf("DiscCdToMultiOBS");
        mappedValue = indicatorCode.substring(0, discCdIndex - 2);
        indicatorCode = indicatorCode.substring(startInd + 2);

        String questionMap = messageElement.getQuestionMap();
        int start = questionMap.indexOf("|");
        String subStringRight = questionMap.substring(start + 1);
        int end = questionMap.indexOf("|");
        String subStringLeft = questionMap.substring(0, start);

        if (subStringLeft.contains("^")) {
            int startPartInt1 = subStringLeft.indexOf("^");
            questPart1 = subStringLeft.substring(0, startPartInt1);
            String remainingPart1 = subStringLeft.substring(startPartInt1 + 1);

            int startPartInt2 = remainingPart1.indexOf("^");
            questPart2 = remainingPart1.substring(0, startPartInt2);
            String remainingPart2 = remainingPart1.substring(startPartInt2 + 1);

            int startPartInt3 = remainingPart2.indexOf("^");
            questPart3 = remainingPart2.substring(0, startPartInt3);
            String remainingPart3 = remainingPart2.substring(startPartInt3 + 1);

            int startPartInt4 = remainingPart3.indexOf("^");
            questPart4 = remainingPart3.substring(0, startPartInt4);
            String remainingPart4 = remainingPart3.substring(startPartInt4 + 1);

            int startPartInt5 = remainingPart4.indexOf("^");
            questPart5 = remainingPart4.substring(0, startPartInt5);
            questPart6 = remainingPart4.substring(startPartInt5 + 1);
        }

        int checkerNum = 0;

        for (DiscreteMulti repeatMulti : repeatMultiArray) {
            if (repeatMulti.getCode().equals(questPart1)) {
                counter = repeatMulti.getCounter();
                repeatMulti.setObsValueCounter(repeatMulti.getObsValueCounter() + 1);
                obsCounter = repeatMulti.getObsValueCounter();
                checkerNum = 1;
                break;
            }
        }

        if (checkerNum == 0) {
            DiscreteMulti repeatMulti = new DiscreteMulti();
            repeatMulti.setObsValueCounter(repeatMultiArray.size());
            counter = obx2Inc;
            repeatMulti.setCounter(obx2Inc);
            repeatMulti.setCode(questPart1);
            repeatMulti.setObsValueCounter(0);
            repeatMulti.setCweQuestionIdentifier(messageElement.getQuestionIdentifier());
            repeatMultiArray.add(repeatMulti);

            OBX obx = orderObservation.getOBSERVATION(1).getOBX();
            obx.getObservationResultStatus().setValue("F");
            obx.getSetIDOBX().setValue(String.valueOf(obx2Inc + 1));
            obx.getValueType().setValue(messageElement.getDataElement().getQuestionDataTypeNND());

            CE observationIdentifier = obx.getObservationIdentifier();
            observationIdentifier.getIdentifier().setValue(questPart1);
            observationIdentifier.getText().setValue(questPart2);
            observationIdentifier.getNameOfCodingSystem().setValue(questPart3);
            observationIdentifier.getAlternateIdentifier().setValue(questPart4);
            observationIdentifier.getAlternateText().setValue(questPart5);
            observationIdentifier.getNameOfAlternateCodingSystem().setValue(questPart6);

            obx.getObservationResultStatus().setValue("F");
        }

        var localComplex = messageElement.getDataElement();

        if (localComplex.getCeDataType() != null) {
            var ce = localComplex.getCeDataType();
            if (ce.getCeCodedValue() != null) {
                output = ce.getCeCodedValue();
            }
            if (ce.getCeCodedValueCodingSystem() != null) {
                output += "^" + ce.getCeCodedValueCodingSystem();
            }
            if (ce.getCeCodedValueDescription() != null) {
                output += "^" + ce.getCeCodedValueDescription();
            }
        } else if (localComplex.getCweDataType() != null) {
            var cwe = localComplex.getCweDataType();
            if (cwe.getCweCodedValue() != null) {
                output = cwe.getCweCodedValue();
            }
            if (cwe.getCweCodedValueCodingSystem() != null) {
                output += "^" + cwe.getCweCodedValueCodingSystem();
            }
            if (cwe.getCweCodedValueDescription() != null) {
                output += "^" + cwe.getCweCodedValueDescription();
            }
        } else if (localComplex.getDtDataType() != null) {
            var dt = localComplex.getDtDataType();
            if (dt.getYear() != null) {
                originalString = dt.getYear();
            }
            if (dt.getDate() != null) {
                originalString = dt.getDate().toString();
            }

            int stringSize = originalString.length();
            output = originalString.substring(0, Math.min(4, stringSize));
            if (stringSize > 7) {
                output += originalString.substring(4, 6);
            }
            if (stringSize > 10) {
                output += originalString.substring(6, 8);
            }
        } else if (localComplex.getStDataType() != null) {
            output = localComplex.getStDataType().getStringData();
        }

        if (localComplex.getNmDataType() != null) {
            output = localComplex.getNmDataType().getNum();
        }

        ST stType = (ST) orderObservation.getOBSERVATION(1).getOBX().getObservationValue(obsCounter).getData();
        stType.setValue(output);
        orderObservation.getOBSERVATION(1).getOBX().getObservationValue(obsCounter).setData(stType);


//        out.OBSERVATION[1].OBX[counter].ObservationValue[obsCounter] = output;


        if (checkerNum == 0) {
            obx2Inc++;
        }

    }

    private void mapToDisRepeat(MessageElement messageElement, int obx2Inc, ORU_R01_ORDER_OBSERVATION orderObservation) throws DataTypeException {
        String indicatorCode = messageElement.getIndicatorCd();
        int startInd = indicatorCode.indexOf("|");

        String mappedValue = indicatorCode.substring(0, indicatorCode.indexOf("DiscCdToMultiOBS") - 2);

        if (mappedValue.equals("Y") && (messageElement.getDataElement().getCweDataType().getCweCodedValue()).equals("Y")) {
            indicatorCode = indicatorCode.substring(startInd + 2);

            String questionMap = messageElement.getQuestionMap();
            int start = questionMap.indexOf("|");
            String subStringRight = questionMap.substring(start + 1);
            String subStringLeft = questionMap.substring(0, start);

            String questPart1 = "", questPart2 = "", questPart3 = "", questPart4 = "", questPart5 = "", questPart6 = "";
            if (subStringLeft.contains("^")) {
                int partStart1 = subStringLeft.indexOf("^");
                questPart1 = subStringLeft.substring(0, partStart1);
                String remaining1 = subStringLeft.substring(partStart1 + 1);

                int partStart2 = remaining1.indexOf("^");
                questPart2 = remaining1.substring(0, partStart2);
                String remaining2 = remaining1.substring(partStart2 + 1);

                int partStart3 = remaining2.indexOf("^");
                questPart3 = remaining2.substring(0, partStart3);
                String remaining3 = remaining2.substring(partStart3 + 1);

                int partStart4 = remaining3.indexOf("^");
                questPart4 = remaining3.substring(0, partStart4);
                String remaining4 = remaining3.substring(partStart4 + 1);

                int partStart5 = remaining4.indexOf("^");
                questPart5 = remaining4.substring(0, partStart5);
                questPart6 = remaining4.substring(partStart5 + 1);
            }

            // TODO - only for test?
            if ("TUB120".equals(indicatorCode)) {
                String test = "mappedValue";
            }

            if (discreteMulti.getCounter() == 0) {
                discreteMulti.setCounter(obx2Inc);
                discreteMulti.setObsValueCounter(0);
                discreteMulti.setCode(questPart1);
                discreteMulti.setIndicatorCode(messageElement.getIndicatorCd());
                discreteMulti.setCweQuestionIdentifier(messageElement.getQuestionIdentifier());

                OBX obx = orderObservation.getOBSERVATION(1).getOBX();
                obx.getObservationResultStatus().setValue("F");
                obx.getSetIDOBX().setValue(String.valueOf(obx2Inc + 1));
                obx.getValueType().setValue("CWE");

                CE obsId = obx.getObservationIdentifier();
                obsId.getIdentifier().setValue(questPart1);
                obsId.getText().setValue(questPart2);
                obsId.getNameOfCodingSystem().setValue(questPart3);
                obsId.getAlternateIdentifier().setValue(questPart4);
                obsId.getAlternateText().setValue(questPart5);
                obsId.getNameOfAlternateCodingSystem().setValue(questPart6);

                obx2Inc += 1;
            } else {
                discreteMulti.setObsValueCounter(discreteMulti.getObsValueCounter() + 1);
            }

            //TODO - Verify this implementation everywhere in the code
            ST stType = (ST) orderObservation.getOBSERVATION(1).getOBX().getObservationValue(discreteMulti.getObsValueCounter()).getData();
            stType.setValue(subStringRight);
            orderObservation.getOBSERVATION(1).getOBX().getObservationValue(discreteMulti.getObsValueCounter()).setData(stType);


//            OBX obx = out.getOBSERVATION(1).getOBX(DiscreteMulti.counter);
//            obx.getObservationValue(DiscreteMulti.ObsValueCounter).setValue(subStringRight);

            //TODO - Remove this test
            String test31 = orderObservation.getOBSERVATION(1).getOBX().getObservationValue(discreteMulti.getObsValueCounter()).toString();
            String test323 = orderObservation.getOBSERVATION(1).getOBX().getObservationValue(discreteMulti.getObsValueCounter()).toString();
            System.err.println("Printing test values in maptodistrepeat..." + test31 + "...." + test323);
        }
    }

    private void mapToDynamicRootlDiscToRepeat(MessageElement messageElement, int obx2Inc, ORU_R01_ORDER_OBSERVATION orderObservation) throws DataTypeException {
        String questionMap = messageElement.getQuestionMap();
        String divider = "++";
        String separatorVal = "|";
        String separatorParent = "|:";
        String separatorSub = "^";
        String questionIdentifier = "";
        int countNum = 0;

        int intIndicator1 = questionMap.indexOf(divider);

        if (intIndicator1 > 0) {
            String part1 = questionMap.substring(0, intIndicator1);
            mapToDynamicDiscToRepeat(messageElement, part1, 1, obx2Inc, questionIdentifier, countNum, orderObservation);

            String part2 = questionMap.substring(intIndicator1 + divider.length());
            mapToDynamicDiscToRepeat(messageElement, part2, 2, obx2Inc, questionIdentifier, countNum, orderObservation);
        } else {
            mapToDynamicDiscToRepeat(messageElement, questionMap, 1, obx2Inc, questionIdentifier, countNum, orderObservation);
        }

        String indicatorCd = messageElement.getIndicatorCd();
        int intIndicator2 = indicatorCd.indexOf(separatorParent);

        if (intIndicator2 > 1) {
            String[] parts = indicatorCd.split(Pattern.quote(separatorVal));
            if (parts.length >= 3) {
                String part3 = parts[0];
                String part5 = parts[1];

                String dataType = messageElement.getDataElement().getQuestionDataTypeNND();
                if (dataType.equals("CWE")) {
                    String codedValue = messageElement.getDataElement().getCweDataType().getCweCodedValue();

                    boolean match = indicatorCd.startsWith(codedValue + separatorSub)
                            || indicatorCd.contains(separatorSub + codedValue + separatorSub)
                            || indicatorCd.contains(separatorSub + codedValue + separatorVal);

                    if (match) {
                        mapToDynamicIndicatorToObx(messageElement, questionIdentifier, part5, countNum, obx2Inc, orderObservation);
                    }
                }
            }
        }
    }

    private void mapToDynamicIndicatorToObx(MessageElement messageElement, String questionIdentifier, String mappedString, int countNum, int obx2Inc, ORU_R01_ORDER_OBSERVATION orderObservation) throws DataTypeException {
        String separatorWOObx5 = "|:";
        int intIndicator = mappedString.indexOf(separatorWOObx5);

        String partIndicator1 = "";
        String partIndicator2 = "";
        String partIndicator3 = "";
        String partIndicator4 = "";
        String partIndicator5 = "";
        String partIndicator6 = "";

        String obx1 = "";
        String partobx11 = "";
        String obx2 = "";
        String partobx12 = "";
        String obx3 = "";
        String partobx13 = "";

        if(mappedString.contains("^")) {
            int intStart1 = mappedString.indexOf("^");
            partIndicator1 = mappedString.substring(0, intStart1);
            String partRemaining1 = mappedString.substring(intStart1 + 1);

            int intStart2 = partRemaining1.indexOf("^");
            partIndicator2 = partRemaining1.substring(0, intStart2);
            String partRemaining2 = partRemaining1.substring(intStart2 + 1);

            int intStart3 = partRemaining2.indexOf("^");
            partIndicator3 = partRemaining2.substring(0, intStart3);
            String partRemaining3 = partRemaining2.substring(intStart3 + 1);

            int intStart4 = partRemaining3.indexOf("^");
            partIndicator4 = partRemaining3.substring(0, intStart4);
            String partRemaining4 = partRemaining3.substring(intStart4 + 1);

            int intStart5 = partRemaining4.indexOf("^");
            partIndicator5 = partRemaining4.substring(0, intStart5);
            String partRemaining5 = partRemaining4.substring(intStart5 + 1);

            int intStart6 = partRemaining5.indexOf("|||");
            partIndicator6 = partRemaining5.substring(0, intStart6);
            String partRemaining6 = partRemaining5.substring(intStart6 + 3);

            OBX obx = orderObservation.getOBSERVATION(1).getOBX();
            obx.getSetIDOBX().setValue(String.valueOf(obx2Inc + 1));

            CE observationIdentifier = obx.getObservationIdentifier();
            observationIdentifier.getIdentifier().setValue(partIndicator1);
            observationIdentifier.getText().setValue(partIndicator2);
            observationIdentifier.getNameOfCodingSystem().setValue(partIndicator3);
            observationIdentifier.getAlternateIdentifier().setValue(partIndicator4);
            observationIdentifier.getAlternateText().setValue(partIndicator5);
            observationIdentifier.getNameOfAlternateCodingSystem().setValue(partIndicator6);

            obx.getObservationSubID().setValue(String.valueOf(countNum));
            obx.getObservationResultStatus().setValue("F");

            orderObservation.getOBSERVATION(1).getOBX().getValueType().setValue("CWE");
            String questionDataType = messageElement.getDataElement().getQuestionDataTypeNND();

            if(questionDataType.equals("CWE")) {
                if (intIndicator > 2) {
                    String codedValue = "";
                    if (messageElement.getDataElement().getCweDataType().getCweCodedValue() != null) {
                        codedValue = messageElement.getDataElement().getCweDataType().getCweCodedValue();
                    }

                    String codedValueDescription = "";
                    if (messageElement.getDataElement().getCweDataType().getCweCodedValueDescription() != null) {
                        codedValueDescription = messageElement.getDataElement().getCweDataType().getCweCodedValueDescription();
                    }

                    String codedValueCodingSystem = "";
                    if (messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem() != null) {
                        codedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem();
                    }

                    String localCodedValue = "";
                    if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValue() != null) {
                        localCodedValue = messageElement.getDataElement().getCweDataType().getCweLocalCodedValue();
                    }

                    String localCodedValueDescription = "";
                    if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription() != null) {
                        localCodedValueDescription = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription();
                    }

                    String localCodedValueCodingSystem = "";
                    if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem() != null) {
                        localCodedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem();
                    }

                    String originalOtherText = "";
                    if (messageElement.getDataElement().getCweDataType().getCweOriginalText() != null) {
                        originalOtherText = "^^^" + messageElement.getDataElement().getCweDataType().getCweOriginalText();
                    }

                    String observationValue = String.join("^",
                            codedValue,
                            codedValueDescription,
                            codedValueCodingSystem,
                            localCodedValue,
                            localCodedValueDescription,
                            localCodedValueCodingSystem
                    ) + originalOtherText;

                    ST stData = (ST) obx.getObservationValue(0).getData();
                    stData.setValue(observationValue);
                    obx.getObservationValue(0).setData(stData);

                } else {
                    String observationValue = String.join("^", obx1, obx2, obx3);
                    ST stData = (ST) obx.getObservationValue(0).getData();
                    stData.setValue(observationValue);
                    obx.getObservationValue(0).setData(stData);
                }
            }

            if (questionDataType.equals("TS")) {
                obx.getValueType().setValue("TS");
                String time = messageElement.getDataElement().getTsDataType().getTime().toString();
                String year = "";
                String month = "00";
                String day = "00";
                String hour = "00";
                String minute = "00";
                String second = "00";
                String milli = "000";
                String separator = ".";

                int stringSize = time.length();

                if (stringSize >= 4) year = time.substring(0, 4);
                if (stringSize >= 7) month = time.substring(4, 6);
                if (stringSize >= 10) day = time.substring(6, 8);
                if (stringSize >= 13) hour = time.substring(8, 10);
                if (stringSize >= 16) minute = time.substring(10, 12);
                if (stringSize >= 19) second = time.substring(12, 14);
                if (stringSize >= 23) milli = time.substring(14, 17);

                String formattedTime = year + month + day + hour + minute + second + separator + milli;
                ST stData = (ST) obx.getObservationValue(0).getData();
                stData.setValue(formattedTime);
                obx.getObservationValue(0).setData(stData);
            }

            if (questionDataType.equals("TX")) {
                obx.getValueType().setValue("ST");

                String textData = "";
                if (messageElement.getDataElement().getTxDataType() != null &&
                        messageElement.getDataElement().getTxDataType().getTextData() != null) {

                    textData = messageElement.getDataElement().getTxDataType().getTextData();
                    textData = textData.replace("\n", " ");
                    textData = mapToRemoveSpecialCharacters(textData);
                }

                ST stData = (ST) obx.getObservationValue(0).getData();
                stData.setValue(textData);
                obx.getObservationValue(0).setData(stData);
            }

            if (questionDataType.equals("ST")) {
                obx.getValueType().setValue("ST");

                String textData = "";
                if (messageElement.getDataElement().getStDataType() != null &&
                        messageElement.getDataElement().getStDataType().getStringData() != null) {

                    textData = messageElement.getDataElement().getStDataType().getStringData();
                    textData = mapToRemoveSpecialCharacters(textData);
                }

                ST stData = (ST) obx.getObservationValue(0).getData();
                stData.setValue(textData);
                obx.getObservationValue(0).setData(stData);
            }
            obx2Inc = obx2Inc + 1;
        }

    }

    //TODO - Add string value to all the method calls and set that to output
    private String mapToRemoveSpecialCharacters(String input) {
        String output = input;
        output = output.replace("\\", "\\E\\");
        output = output.replace("|", "\\F\\");
        output = output.replace("~", "\\R\\");
        output = output.replace("^", "\\S\\");
        output = output.replace("&", "\\T\\");
        return output;
    }

    private void mapToDynamicDiscToRepeat(MessageElement messageElement, String mappedString, int splitPart, int obx2Inc, String questionIdentifier, int repeatCountNum, ORU_R01_ORDER_OBSERVATION orderObservation) throws DataTypeException {
        String separatorWOObx5 = "||||";
        int intIndicator = mappedString.indexOf(separatorWOObx5);

        String partIndicator1 = "";
        String partIndicator2 = "";
        String partIndicator3 = "";
        String partIndicator4 = "";
        String partIndicator5 = "";
        String partIndicator6 = "";
        String partIndicator7 = "";
        String partIndicator8 = "";
        String partIndicator9 = "";
        String partRemaining9 = "";

        String obx1 = "";
        String partobx11 = "";
        String obx2 = "";
        String partobx12 = "";
        String obx3 = "";
        String partobx13 = "";

        int counter = 0;

        if (mappedString.contains("^")) {
            if (intIndicator > 0) {
                int intStart1 = mappedString.indexOf("^");
                partIndicator1 = mappedString.substring(0, intStart1);
                String partRemaining1 = mappedString.substring(intStart1 + 1);

                int intStart2 = partRemaining1.indexOf("^");
                partIndicator2 = partRemaining1.substring(0, intStart2);
                String partRemaining2 = partRemaining1.substring(intStart2 + 1);

                int intStart3 = partRemaining2.indexOf("^");
                partIndicator3 = partRemaining2.substring(0, intStart3);
                String partRemaining3 = partRemaining2.substring(intStart3 + 1);

                int intStart4 = partRemaining3.indexOf("^");
                partIndicator4 = partRemaining3.substring(0, intStart4);
                String partRemaining4 = partRemaining3.substring(intStart4 + 1);

                int intStart5 = partRemaining4.indexOf("^");
                partIndicator5 = partRemaining4.substring(0, intStart5);
                String partRemaining5 = partRemaining4.substring(intStart5 + 1);

                int intStart6 = partRemaining5.indexOf("|||");
                partIndicator6 = partRemaining5.substring(0, intStart6);
                String partRemaining6 = partRemaining5.substring(intStart6 + 3);

                int intStart7 = partRemaining6.indexOf("|");
                partIndicator7 = partRemaining6.substring(0, intStart7);
                String partRemaining7 = partRemaining6.substring(intStart7 + partIndicator7.length() + 3);

                int intStart8 = partRemaining7.indexOf("|");
                partIndicator8 = partRemaining7.substring(0, intStart8);
                String numVar = partIndicator8.substring(0, partRemaining7.indexOf("+"));
                int numCounter = Integer.parseInt(partIndicator8.substring(partRemaining7.indexOf("+") + 1));

                partRemaining9 = partRemaining7.substring(intStart8 + 1);
            } else {
                int intStart1 = mappedString.indexOf("^");
                partIndicator1 = mappedString.substring(0, intStart1);
                String partRemaining1 = mappedString.substring(intStart1 + 1);

                int intStart2 = partRemaining1.indexOf("^");
                partIndicator2 = partRemaining1.substring(0, intStart2);
                String partRemaining2 = partRemaining1.substring(intStart2 + 1);

                int intStart3 = partRemaining2.indexOf("^");
                partIndicator3 = partRemaining2.substring(0, intStart3);
                String partRemaining3 = partRemaining2.substring(intStart3 + 1);

                int intStart4 = partRemaining3.indexOf("^");
                partIndicator4 = partRemaining3.substring(0, intStart4);
                String partRemaining4 = partRemaining3.substring(intStart4 + 1);

                int intStart5 = partRemaining4.indexOf("^");
                partIndicator5 = partRemaining4.substring(0, intStart5);
                String partRemaining5 = partRemaining4.substring(intStart5 + 1);

                int intStart6 = partRemaining5.indexOf("|");
                partIndicator6 = partRemaining5.substring(0, intStart6);
                String partRemaining6 = partRemaining5.substring(intStart6 + 1);

                int intobx1 = partRemaining6.indexOf("^");
                obx1 = partRemaining6.substring(0, intobx1);
                String partRemainingintobx1 = partRemaining6.substring(intobx1 + 1);

                int intobx2 = partRemainingintobx1.indexOf("^");
                obx2 = partRemainingintobx1.substring(0, intobx2);
                String partRemainingintobx2 = partRemainingintobx1.substring(intobx2 + 1);

                int intobx3 = partRemainingintobx2.indexOf("|||");
                obx3 = partRemainingintobx2.substring(0, intobx3);
                String partRemainingintobx3 = partRemainingintobx2.substring(intobx3 + 3);

                int intStart8 = partRemainingintobx3.indexOf("|");
                partIndicator8 = partRemainingintobx3.substring(0, intStart8);
                String numVar = partIndicator8.substring(0, partRemainingintobx3.indexOf("+"));
                int numCounter = Integer.parseInt(partIndicator8.substring(partRemainingintobx3.indexOf("+") + 1));

                partRemaining9 = partRemainingintobx3.substring(intStart8 + 1);


            }

            questionIdentifier = partRemaining9;
            int maxObxCounter = 0;

            for (DynamicRepeatMulti item : dynamicRepeatMultiArray) {
                if (item.getParentCode().equals(partRemaining9) && item.getPartIndicator().equals(partIndicator8)) {
                    counter = item.getObx4counter();
                } else if (item.getParentCode().equals(partRemaining9) && counter == 0) {
                    if (maxObxCounter < item.getObx4counter()) {
                        maxObxCounter = item.getObx4counter();
                    }
                }
            }

            if (maxObxCounter == 0 && counter == 0) {
                DynamicRepeatMulti dynamicRepeat = new DynamicRepeatMulti();
                dynamicRepeat.setParentCode(partRemaining9);
                dynamicRepeat.setPartIndicator(partIndicator8);
                dynamicRepeat.setObx4counter(1);
                counter = 1;
                dynamicRepeatMultiArray.add(dynamicRepeat);
            } else if (maxObxCounter > 0) {
                DynamicRepeatMulti dynamicRepeat = new DynamicRepeatMulti();
                dynamicRepeat.setParentCode(partRemaining9);
                dynamicRepeat.setPartIndicator(partIndicator8);
                dynamicRepeat.setObx4counter(maxObxCounter + 1);
                counter = maxObxCounter + 1;
                dynamicRepeatMultiArray.add(dynamicRepeat);
            }

            repeatCountNum = counter;

            OBX obx = orderObservation.getOBSERVATION(1).getOBX();
            obx.getSetIDOBX().setValue(String.valueOf(obx2Inc + 1));

            CE observationIdentifier = obx.getObservationIdentifier();
            observationIdentifier.getIdentifier().setValue(partIndicator1);
            observationIdentifier.getText().setValue(partIndicator2);
            observationIdentifier.getNameOfCodingSystem().setValue(partIndicator3);
            observationIdentifier.getAlternateIdentifier().setValue(partIndicator4);
            observationIdentifier.getAlternateText().setValue(partIndicator5);
            observationIdentifier.getNameOfAlternateCodingSystem().setValue(partIndicator6);

            obx.getObservationSubID().setValue(String.valueOf(counter));
            obx.getObservationResultStatus().setValue("F");

            String questionDataType = messageElement.getDataElement().getQuestionDataTypeNND();
            if (questionDataType.equals("CWE") || splitPart == 2) {
                obx.getValueType().setValue("CWE");

                if(intIndicator > 0) {
                    String codedValue = "";
                    if (messageElement.getDataElement().getCweDataType().getCweCodedValue() != null) {
                        codedValue = messageElement.getDataElement().getCweDataType().getCweCodedValue();
                    }

                    String codedValueDescription = "";
                    if (messageElement.getDataElement().getCweDataType().getCweCodedValueDescription() != null) {
                        codedValueDescription = messageElement.getDataElement().getCweDataType().getCweCodedValueDescription();
                    }

                    String codedValueCodingSystem = "";
                    if (messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem() != null) {
                        codedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem();
                    }

                    String localCodedValue = "";
                    if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValue() != null) {
                        localCodedValue = messageElement.getDataElement().getCweDataType().getCweLocalCodedValue();
                    }

                    String localCodedValueDescription = "";
                    if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription() != null) {
                        localCodedValueDescription = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription();
                    }

                    String localCodedValueCodingSystem = "";
                    if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem() != null) {
                        localCodedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem();
                    }

                    String originalOtherText = "";
                    if (messageElement.getDataElement().getCweDataType().getCweOriginalText() != null) {
                        String textData = "^^^" + messageElement.getDataElement().getCweDataType().getCweOriginalText();
                        mapToRemoveSpecialCharacters(textData);
                        originalOtherText = "^^^" + textData;
                    }

                    String observationValue = String.join("^",
                            codedValue,
                            codedValueDescription,
                            codedValueCodingSystem,
                            localCodedValue,
                            localCodedValueDescription,
                            localCodedValueCodingSystem
                    ) + originalOtherText;

                    ST stData = (ST) obx.getObservationValue(1).getData();
                    stData.setValue(observationValue);
                    obx.getObservationValue(0).setData(stData);

                } else {
                    String observationValue = String.join("^", obx1, obx2, obx3);
                    ST stData = (ST) obx.getObservationValue(0).getData();
                    stData.setValue(observationValue);
                    obx.getObservationValue(0).setData(stData);
                }
            } else if (questionDataType.equals("TS")) {
                obx.getValueType().setValue("TS");
                String time = messageElement.getDataElement().getTsDataType().getTime().toString();
                String year = "";
                String month = "00";
                String day = "00";
                String hour = "00";
                String minute = "00";
                String second = "00";
                String milli = "000";
                String separator = ".";

                int stringSize = time.length();

                if (stringSize >= 4) year = time.substring(0, 4);
                if (stringSize >= 7) month = time.substring(4, 6);
                if (stringSize >= 10) day = time.substring(6, 8);
                if (stringSize >= 13) hour = time.substring(8, 10);
                if (stringSize >= 16) minute = time.substring(10, 12);
                if (stringSize >= 19) second = time.substring(12, 14);
                if (stringSize >= 23) milli = time.substring(14, 17);

                String formattedTime = year + month + day + hour + minute + second + separator + milli;
                ST stData = (ST) obx.getObservationValue(0).getData();
                stData.setValue(formattedTime);
                obx.getObservationValue(0).setData(stData);
            } else if (questionDataType.equals("TX")) {
                obx.getValueType().setValue("ST");

                String textData = "";
                if (messageElement.getDataElement().getTxDataType() != null &&
                        messageElement.getDataElement().getTxDataType().getTextData() != null) {

                    textData = messageElement.getDataElement().getTxDataType().getTextData();
                    textData = textData.replace("\n", " ");
                    textData = mapToRemoveSpecialCharacters(textData);
                }

                ST stData = (ST) obx.getObservationValue(0).getData();
                stData.setValue(textData);
                obx.getObservationValue(0).setData(stData);
            }
            else if (questionDataType.equals("ST")) {
                obx.getValueType().setValue("ST");

                String textData = "";
                if (messageElement.getDataElement().getStDataType() != null &&
                        messageElement.getDataElement().getStDataType().getStringData() != null) {

                    textData = messageElement.getDataElement().getStDataType().getStringData();
                    textData = mapToRemoveSpecialCharacters(textData);
                }

                ST stData = (ST) obx.getObservationValue(0).getData();
                stData.setValue(textData);
                obx.getObservationValue(0).setData(stData);
            }
            obx2Inc = obx2Inc + 1;
        }
    }

    private void mapToDynamicParentRptToRpt(MessageElement messageElement, int obx2Inc, String messageType, ORU_R01_ORDER_OBSERVATION orderObservation) throws DataTypeException {
        String parentCode = messageElement.getIndicatorCd();
        int intStart = parentCode.indexOf("|ParentRepeatBlock");

        String partIndicator1 = parentCode.substring(0, intStart);
        String parentQuestionIdentifier = partIndicator1.replace("||:", "");

        String questionId = messageElement.getQuestionIdentifierNND();
        String obsSubIdCounter = messageElement.getObservationSubID();

        int maxObxCounter = 0;
        int counter = 0;


        for (int x = 0; x < dynamicRepeatMultiArray.size(); x++) {
            DynamicRepeatMulti entry = dynamicRepeatMultiArray.get(x);

            if (entry.getParentCode().equals(parentQuestionIdentifier) &&
                    entry.getPartIndicator().equals(obsSubIdCounter)) {
                counter = entry.getObx4counter();
            }
            else if (entry.getParentCode().equals(parentQuestionIdentifier) && counter == 0) {
                if (maxObxCounter < entry.getObx4counter()) {
                    maxObxCounter = entry.getObx4counter();
                }
            }
        }
        if (maxObxCounter == 0 && counter == 0) {
            DynamicRepeatMulti dynamicRepeat = new DynamicRepeatMulti();
            dynamicRepeat.setParentCode(parentQuestionIdentifier);
            dynamicRepeat.setPartIndicator(obsSubIdCounter);
            dynamicRepeat.setObx4counter(1);
            dynamicRepeatMultiArray.add(dynamicRepeat);
            counter = dynamicRepeat.getObx4counter();
        } else if (maxObxCounter > 0) {
            DynamicRepeatMulti dynamicRepeat = new DynamicRepeatMulti();
            dynamicRepeat.setParentCode(parentQuestionIdentifier);
            dynamicRepeat.setPartIndicator(obsSubIdCounter);
            dynamicRepeat.setObx4counter(maxObxCounter + 1);
            dynamicRepeatMultiArray.add(dynamicRepeat);
            counter = dynamicRepeat.getObx4counter();
        }

        OBX obx = orderObservation.getOBSERVATION(1).getOBX();

        obx.getValueType().setValue(messageElement.getDataElement().getQuestionDataTypeNND());
        obx.getSetIDOBX().setValue(String.valueOf(obx2Inc + 1));

        obx.getObservationIdentifier().getIdentifier().setValue(messageElement.getQuestionIdentifierNND());
        obx.getObservationIdentifier().getText().setValue(messageElement.getQuestionLabelNND());
        obx.getObservationIdentifier().getNameOfCodingSystem().setValue(messageElement.getQuestionOID());

        obx.getObservationIdentifier().getAlternateIdentifier().setValue(messageElement.getQuestionIdentifier());
        obx.getObservationIdentifier().getAlternateText().setValue(messageElement.getQuestionLabel());
        obx.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");

        obx.getObservationResultStatus().setValue("F");
        obx.getObservationSubID().setValue(String.valueOf(counter));

        if (messageElement.getDataElement().getQuestionDataTypeNND().equals("CWE")) {
            String codedValue = "";
            if (messageElement.getDataElement().getCweDataType().getCweCodedValue() != null) {
                codedValue = messageElement.getDataElement().getCweDataType().getCweCodedValue();
            }

            String codedValueDescription = "";
            if (messageElement.getDataElement().getCweDataType().getCweCodedValueDescription() != null) {
                codedValueDescription = messageElement.getDataElement().getCweDataType().getCweCodedValueDescription();
            }

            String codedValueCodingSystem = "";
            if (messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem() != null) {
                codedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem();
            }

            String localCodedValue = "";
            if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValue() != null) {
                localCodedValue = messageElement.getDataElement().getCweDataType().getCweLocalCodedValue();
            }

            String localCodedValueDescription = "";
            if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription() != null) {
                localCodedValueDescription = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription();
            }

            String localCodedValueCodingSystem = "";
            if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem() != null) {
                localCodedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem();
            }

            String originalOtherText = "";
            if (messageElement.getDataElement().getCweDataType().getCweOriginalText() != null) {
                String textData = messageElement.getDataElement().getCweDataType().getCweOriginalText();
                textData = mapToRemoveSpecialCharacters(textData);
                originalOtherText = "^^^" + textData;
            }

            String finalValue = String.join("^",
                    codedValue,
                    codedValueDescription,
                    codedValueCodingSystem,
                    localCodedValue,
                    localCodedValueDescription,
                    localCodedValueCodingSystem
            ) + originalOtherText;

            ST stData = (ST) obx.getObservationValue(0).getData();
            stData.setValue(finalValue);
            obx.getObservationValue(0).setData(stData);
        }
        if ((messageElement.getDataElement().getQuestionDataTypeNND()).equals("TS")) {
            String time = messageElement.getDataElement().getTsDataType().getTime().toString();

            String year = time.length() >= 4 ? time.substring(0, 4) : "";
            String month = time.length() >= 6 ? time.substring(4, 6) : "00";
            String day = time.length() >= 8 ? time.substring(6, 8) : "00";
            String hour = time.length() >= 10 ? time.substring(8, 10) : "00";
            String minute = time.length() >= 12 ? time.substring(10, 12) : "00";
            String second = time.length() >= 14 ? time.substring(12, 14) : "00";
            String milli = time.length() >= 17 ? time.substring(14, 17) : "000";
            String separator = ".";

            String timeOut = year + month + day + hour + minute + second + separator + milli;

            //TODO - Check how to set value
            TS tsType = (TS) obx.getObservationValue(0).getData();
            tsType.getTime().setValue(timeOut);
            obx.getObservationValue(0).setData(tsType);
        }
        String dataType = messageElement.getDataElement().getQuestionDataTypeNND();

        if (dataType.equals("TX")) {
            obx.getValueType().setValue("ST");
            String textData = messageElement.getDataElement().getTxDataType().getTextData();
            textData = textData.replace("\n", " ");

            //TODO - Check how to set value
            ST stDataType = (ST) obx.getObservationValue(0).getData();
            stDataType.setValue(textData);
            obx.getObservationValue(0).setData(stDataType);
            // TODO - Might not be needed
//            obx.getObservationValue(0).setValue(textData);
        }

        if (dataType.equals("ST")) {
            obx.getValueType().setValue(dataType);
            String textData = messageElement.getDataElement().getStDataType().getStringData();
            //TODO - Check how to set value
            ST stDataType = (ST) obx.getObservationValue(0).getData();
            stDataType.setValue(textData);
            obx.getObservationValue(0).setData(stDataType);
            //TODO - Might not be needed
//            obx.getObservationValue(0).setValue(textData);
        }
        obx2Inc = obx2Inc + 1;
    }

    private void mapLabReportEventToOBR(NBSNNDIntermediaryMessage nbsnndIntermediaryMessage, LabReportEvent labReportEvent, int obrCounter, int labObrCounter, String messageType, int i, EIElement eiElement, ParentLink parentLink, int obxSubidCounter, String cachedOBX3data, ORU_R01 oruMessage) throws HL7Exception {
        // TODO - complete this method
        int labSubCounter = 0;
        int specimenCounter= 1;
        int isValidSPM=0;
        String resultStatus="";
        int reasonForStudyCounter=0;
        EIElement eiType = new EIElement();
        cachedOBX3data="";

        for (MessageElement messageElement: nbsnndIntermediaryMessage.getMessageElement()) {
            String questionDataTypeNND = messageElement.getDataElement().getQuestionDataTypeNND().trim();
            String questionIdentifierNND = messageElement.getQuestionIdentifierNND();
            String hl7Field = messageElement.getHl7SegmentField().trim();
            if (hl7Field.startsWith("OBR-")) {
                String dataElement = hl7Field.replace("OBR-", "");

                if (dataElement.startsWith("1.")) {
                } else if (dataElement.startsWith("2.")) {
                    mapToEIType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getPlacerOrderNumber());
                } else if (dataElement.startsWith("3.")) {
                    EI fillerOrder = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getFillerOrderNumber();
                    mapToEIType(messageElement, fillerOrder);

                    // TODO - Verify if it is okay to wrap it as string
                    eiType.setEntityIdentifier(String.valueOf(fillerOrder.getEntityIdentifier()));
                    eiType.setNamespaceID(String.valueOf(fillerOrder.getNamespaceID()));
                    eiType.setUniversalID(String.valueOf(fillerOrder.getUniversalID()));
                    eiType.setUniversalIDType(String.valueOf(fillerOrder.getUniversalIDType()));
                } else if (dataElement.startsWith("4.")) {
                    mapToCEType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getUniversalServiceIdentifier());
                } else if (dataElement.startsWith("5.")) {
                } else if (dataElement.startsWith("6.")) {
                } else if (dataElement.startsWith("7.")) {
                    mapToTS18DataType(messageElement.getDataElement().getTsDataType().getTime().toString(), oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getObservationDateTime());
                } else if (dataElement.startsWith("14.")) {
                } else if (dataElement.startsWith("16.")) {
                } else if (dataElement.startsWith("22.")) {
                    mapToTS18DataType(messageElement.getDataElement().getTsDataType().getTime().toString(), oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getResultsRptStatusChngDateTime());
                } else if (dataElement.startsWith("25.")) {
                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getResultStatus().setValue(messageElement.getDataElement().getIdDataType().getIdCodedValue());
                } else if (dataElement.startsWith("31.")) {
                    mapToCEType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getReasonForStudy(reasonForStudyCounter));
                    reasonForStudyCounter++;
                } else if (dataElement.startsWith("35.")) {
                    // mapToCEType(messageElement, out.getRace().get(raceCounterNK1));
                    // raceCounterNK1++;
                }
            } else if (hl7Field.startsWith("SPM-")) {
                String dataElement = hl7Field.replace("SPM-", "");

                if (dataElement.startsWith("1.")) {
                } else if (dataElement.startsWith("2.1")) {
                    mapToEIType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenID().getPlacerAssignedIdentifier());
                } else if (dataElement.startsWith("2.2")) {
                    mapToEIType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenID().getFillerAssignedIdentifier());
                } else if (dataElement.startsWith("4.")) {
                    mapToCWEType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenType());
                    isValidSPM = 1;
                } else if (dataElement.startsWith("8")) {
                    mapToCWEType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenSourceSite());
                } else if (dataElement.startsWith("11")) {
                } else if (dataElement.startsWith("12.0")) {
                    String quantity = messageElement.getDataElement().getNmDataType().getNum();
                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenCollectionAmount().getQuantity().setValue(quantity);
                } else if (dataElement.startsWith("12.1")) {
                } else if (dataElement.startsWith("12.2")) {
                    mapToCEType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenCollectionAmount().getUnits());
                } else if (dataElement.startsWith("14")) {
                    String desc = messageElement.getDataElement().getStDataType().getStringData();
                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenDescription(0).setValue(desc);
                } else if (dataElement.startsWith("17")) {
                    mapToTS18DataType(messageElement.getDataElement().getTsDataType().getTime().toString(), oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenCollectionDateTime().getRangeStartDateTime());
                } else if (dataElement.startsWith("18")) {
                }
            }
            if (isValidSPM == 0) {
                // TODO - this needs to be validated
                oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).removeOBX(0);
            } else {
                oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSetIDSPM().setValue("1");
            }
        }

            if (eiElement.getEntityIdentifier() != null && !eiElement.getEntityIdentifier().isEmpty()) {
                EI fillerAssignedIdentifier = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getParentNumber().getFillerAssignedIdentifier();
                fillerAssignedIdentifier.getEntityIdentifier().setValue(eiElement.getEntityIdentifier());
                fillerAssignedIdentifier.getNamespaceID().setValue(eiElement.getNamespaceID());
                fillerAssignedIdentifier.getUniversalID().setValue(eiElement.getUniversalID());
                fillerAssignedIdentifier.getUniversalIDType().setValue(eiElement.getUniversalIDType());
            }

            if (parentLink.getObservationValue() != null && !parentLink.getObservationValue().isEmpty()) {
                CE parentObservationIdentifier = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getParentResult().getParentObservationIdentifier();
                parentObservationIdentifier.getText().setValue(parentLink.getText());
                parentObservationIdentifier.getNameOfCodingSystem().setValue(parentLink.getNameOfCodingSystem());
                parentObservationIdentifier.getIdentifier().setValue(parentLink.getIdentifier());
                parentObservationIdentifier.getAlternateText().setValue(parentLink.getAlternateText());
                parentObservationIdentifier.getNameOfAlternateCodingSystem().setValue(parentLink.getNameOfAlternateCodingSystem());
                parentObservationIdentifier.getAlternateIdentifier().setValue(parentLink.getAlternateIdentifier());

                oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getParentResult().getParentObservationSubIdentifier().setValue(parentLink.getObservationSubID());
                oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getParentResult().getParentObservationValueDescriptor().setValue(parentLink.getObservationValue());
            }

            int resultedTestCounter = 0;
            int previousCounter = 0;
            int repeatCounter =0;

            List<LabReportEvent.ResultedTest> resultedTestList = labReportEvent.getResultedTest();
            for (int j = 0; j < resultedTestList.size(); j++) {
                String specimenCollectionDate = "";
                String dateTimeOfAnalysis = "";
                String OBXResult = "";
                String referenceRangeFrom = "";
                String referenceRangeTo = "";
                String interpretationFlag = "";
                int notesCounter = 0;
                OBXResult = "";
                referenceRangeTo = "";
                referenceRangeFrom = "";

                ResultMethod resultMethod = new ResultMethod();
                repeatCounter += 1;

                ParentLink parentLinkOBX = new ParentLink();

                mapResultedTestToOBX(
                        resultedTestList.get(j),
                        obrCounter,
                        labSubCounter,
                        OBXResult,
                        resultedTestCounter,
                        notesCounter,
                        referenceRangeFrom,
                        referenceRangeTo,
                        resultStatus,
                        messageType,
                        specimenCollectionDate,
                        obrCounter + 1,
                        eiType,
                        dateTimeOfAnalysis,
                        interpretationFlag,
                        obxSubidCounter,
                        cachedOBX3data,
                        resultMethod,
                        parentLinkOBX,
                        oruMessage
                );

                String identifier = "";
                String description = "";
                String descriptionValue = "";

                if (OBXResult.contains("^")) {
                    int part1 = OBXResult.indexOf("^");
                    identifier = OBXResult.substring(0, part1);

                    String rest = OBXResult.substring(part1 + 1);

                    int part2 = rest.indexOf("^");
                    if (part2 >= 0) {
                        description = rest.substring(0, part2);
                        descriptionValue = rest.substring(part2 + 1);
                    } else {
                        description = rest;
                    }
                }
                for(int k = previousCounter; k < oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().numFields(); k++) {
                    if(oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labObrCounter).getOBX().getObservationIdentifier().getIdentifier().isEmpty()) {
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationIdentifier().getIdentifier().setValue(identifier);
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationIdentifier().getNameOfCodingSystem().setValue(description);
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationIdentifier().getText().setValue(descriptionValue);
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationResultStatus().setValue(resultStatus);
                    }

                    String output = "";
                    String outputIndex = "";
                    String subString = "";

                    if (cachedOBX3data != null && !cachedOBX3data.isEmpty() && cachedOBX3data.contains(identifier + ":")) {
                        int start = cachedOBX3data.indexOf(identifier + ":");

                        if (start == 0) {
                            int contTest = cachedOBX3data.indexOf("||");
                            subString = cachedOBX3data.substring(0, contTest);
                            int counterFinder = subString.indexOf(":");
                            String obxSubidCounterTest = subString.substring(counterFinder + 1);
                            int obxSubidCounterTestInt = Integer.parseInt(obxSubidCounterTest);

                            cachedOBX3data = cachedOBX3data.replaceFirst(
                                    Pattern.quote(identifier + ":" + obxSubidCounterTest),
                                    identifier + ":" + (obxSubidCounterTestInt + 1)
                            );

                            obxSubidCounter = obxSubidCounterTestInt + 1;
                        } else {
                            int contTest = cachedOBX3data.indexOf(identifier + ":");
                            int lookaheadLength = Math.min(cachedOBX3data.length() - contTest, identifier.length() + 6); // safe bounds
                            String cachedString = cachedOBX3data.substring(contTest, contTest + lookaheadLength);

                            String cachedStringWithoutMarker = cachedString.split("\\|\\|")[0];

                            int colonIndex = cachedStringWithoutMarker.indexOf(":");
                            String counterNumber = cachedStringWithoutMarker.substring(colonIndex + 1);
                            int obxSubidCounterTestInt = Integer.parseInt(counterNumber);

                            cachedOBX3data = cachedOBX3data.replaceFirst(
                                    Pattern.quote(identifier + ":" + counterNumber),
                                    identifier + ":" + (obxSubidCounterTestInt + 1)
                            );

                            obxSubidCounter = obxSubidCounterTestInt + 1;
                        }
                    } else {
                        cachedOBX3data = cachedOBX3data + identifier + ":1||";
                        obxSubidCounter = 1;
                    }
                    parentLinkOBX.setObservationSubID(Integer.toString(obxSubidCounter));

                    if(referenceRangeFrom.isEmpty() && !referenceRangeTo.isEmpty()) {
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getReferencesRange().setValue(referenceRangeFrom + "-" + referenceRangeTo);
                    } else if (!referenceRangeFrom.isEmpty() || !referenceRangeTo.isEmpty()) {
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getReferencesRange().setValue(referenceRangeFrom + referenceRangeTo);
                    }

                    if (!specimenCollectionDate.isEmpty()) {
                        mapToDTM18(specimenCollectionDate, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getDateTimeOfTheObservation().getTime());
                    }
                    if (!dateTimeOfAnalysis.isEmpty()) {
                        mapToDTM18(dateTimeOfAnalysis, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getDateTimeOfTheAnalysis().getTime());
                    }
                    if (!interpretationFlag.isEmpty()) {
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getAbnormalFlags(0).setValue(interpretationFlag);
                    }
                    if (resultMethod != null && !resultMethod.getText().isEmpty()) {
                        CE observationMethod = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationMethod(0);
                        observationMethod.getText().setValue(resultMethod.getText());
                        observationMethod.getNameOfCodingSystem().setValue(resultMethod.getNameOfCodingSystem());
                        observationMethod.getIdentifier().setValue(resultMethod.getCode());
                    }

                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationSubID().setValue(Integer.toString(obxSubidCounter));
                    String cweOBX = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getValueType().toString();
                    String ceOBX = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getValueType().toString();
                    CE observationIdentifier = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationIdentifier();
                    if("CWE".equals(cweOBX) || "CE".equals(ceOBX)) {
                        parentLinkOBX.setIdentifier(observationIdentifier.getIdentifier().getValue());
                        parentLinkOBX.setNameOfCodingSystem(observationIdentifier.getNameOfCodingSystem().getValue());
                        parentLinkOBX.setText(observationIdentifier.getText().getValue());
                        parentLinkOBX.setAlternateIdentifier(observationIdentifier.getAlternateText().getValue());
                        parentLinkOBX.setNameOfAlternateCodingSystem(observationIdentifier.getNameOfAlternateCodingSystem().getValue());
                        parentLinkOBX.setAlternateText(observationIdentifier.getAlternateText().getValue());
                        parentLinkOBX.setObservationSubID(oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationSubID().getValue());
                        // TODO - Check the toString() method
                        parentLinkOBX.setObservationValue(oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationValue(0).toString());
                        mapToSusceptabilityOBX(
                                nbsnndIntermediaryMessage,
                                resultedTestList.get(j),
                                obrCounter,
                                labSubCounter,
                                resultedTestCounter,
                                obrCounter + 1,
                                messageType,
                                parentLinkOBX,
                                eiType,
                                oruMessage
                        );
                    }
                }
                previousCounter = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().numFields();
            }
    }

    private void mapToTS18DataType(String input, TS output) throws DataTypeException {
        // TODO - This method needs to be verified as the milli seconds in not available in ORU R01
        int stringSize = input.length();
        int year = Integer.parseInt(input.substring(0, 4));
        int month;
        int day;
        int hours;
        int minutes;
        float seconds;

        if (stringSize < 7) {
            month = 0;
        } else {
            month = Integer.parseInt(input.substring(4, 6));
        }

        if (stringSize < 10) {
            day = 0;
        } else {
            day = Integer.parseInt(input.substring(7, 9));
        }

        if (stringSize < 13) {
            hours = 0;
        } else {
            hours = Integer.parseInt(input.substring(10, 12));
        }

        if (stringSize < 16) {
            minutes = 0;
        } else {
            minutes = Integer.parseInt(input.substring(13, 15));
        }

        if (stringSize < 19) {
            seconds = 0;
        } else {
            seconds = Integer.parseInt(input.substring(16, 18));
        }

//        if(stringSize<23){
//            out.Time.Millis =000;
//            out.Time.seperator=".";
//        }else{
//            out.Time.seperator=".";
//            out.Time.Millis  = StrToInt(StrMid(in,20,3));
//        }

        output.getTime().setDateSecondPrecision(year, month, day, hours, minutes, seconds);
    }

    private void mapToSusceptabilityOBX(NBSNNDIntermediaryMessage nbsnndIntermediaryMessage, LabReportEvent.ResultedTest resultedTest, int obrCounter, int labSubCounter, int resultedTestCounter, int i, String messageType, ParentLink parentLink, EIElement eiElement, ORU_R01 oruMessage) throws HL7Exception {
        int labObrCounter;
        for(LabReportEvent labReportEvent : resultedTest.getLabReportEvent()) {
            obrCounter = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATIONReps();
            int obxSUSSubidCounter = 1;
            String cachedSUSOBX3data = "";
            if(!labReportEvent.getMessageElement().isEmpty()) {
                labObrCounter = obrCounter + 1;
                mapLabReportEventToOBR(nbsnndIntermediaryMessage,
                        labReportEvent,
                        obrCounter,
                        // TODO - Check this line
                        labObrCounter,
                        messageType,
                        1,
                        eiElement,
                        parentLink,
                        obxSUSSubidCounter,
                        cachedSUSOBX3data,
                        oruMessage);
            }
        }
    }

    private void mapToDTM18(String input, DTM output) throws DataTypeException {
        //TODO - Implement this method
        int stringSize = input.length();
        int year = Integer.parseInt(input.substring(0, 4));
        int month;
        int day;
        int hours;
        int minutes;
        float seconds;

        if (stringSize < 7) {
            month = 0;
        } else {
            month = Integer.parseInt(input.substring(4, 6));
        }

        if (stringSize < 10) {
            day = 0;
        } else {
            day = Integer.parseInt(input.substring(7, 9));
        }

        if (stringSize < 13) {
            hours = 0;
        } else {
            hours = Integer.parseInt(input.substring(10, 12));
        }

        if (stringSize < 16) {
            minutes = 0;
        } else {
            minutes = Integer.parseInt(input.substring(13, 15));
        }

        if (stringSize < 19) {
            seconds = 0;
        } else {
            seconds = Integer.parseInt(input.substring(16, 18));
        }

//        if(stringSize<23){
//            out.Time.Millis =000;
//            out.Time.seperator=".";
//        }else{
//            out.Time.seperator=".";
//            out.Time.Millis  = StrToInt(StrMid(in,20,3));
//        }

        output.setDateSecondPrecision(year, month, day, hours, minutes, seconds);
    }

    private void mapResultedTestToOBX(LabReportEvent.ResultedTest resultedTest, int obrSubCounter, int observationCounter, String OBXResult, int resultedTestCounter, int notesCounter,
                                      String referenceRangeFrom, String referenceRangeTo, String resultStatus, String messageType, String specimenCollectionDate,
                                      int obrCounter, EIElement eiElement, String dateTimeOfAnalysis, String interpretationFlag, int obxSubidCounter, String cachedOBX3data,
                                      ResultMethod resultMethod, ParentLink parentLink, ORU_R01 oruMessage) throws HL7Exception {
    // TODO - Implement this method
        int valueTypeChecker = 0;

        for(MessageElement messageElement : resultedTest.getMessageElement()) {
            String hl7Field = messageElement.getHl7SegmentField().trim();

            if(hl7Field.startsWith("OBX-")) {
                String dataElement = hl7Field.replace("OBX-", "");

                if (dataElement.contains("5.")) {
                    int counter = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getObservationValue().length;
                    if(counter > 0){
                        resultedTestCounter = resultedTestCounter + 1;
                    }
                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getSetIDOBX().setValue(String.valueOf(resultedTestCounter + 1));
                }

                if (dataElement.contains("1.")) {
                }
                else if (dataElement.contains("2.")) {
                }
                else if (dataElement.contains("3.")) {
                    String codedValue = mapCodedToStringValue(messageElement, OBXResult, parentLink);
                }
                else if (dataElement.contains("5.")) {
                    if (messageElement.getDataElement().getQuestionDataTypeNND().equals("SN_WITH_UNIT")) {
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getValueType().setValue("SN");
                        valueTypeChecker = 1;

                        String codedValue = "";
                        String codedValueDescription = "";
                        String codedValueCodingSystem = "";
                        String localCodedValue = "";
                        String localCodedValueDescription = "";
                        String localCodedValueCodingSystem = "";

                        if (messageElement.getDataElement().getSnunitDataType().getCeCodedValue() != null) {
                            codedValue = messageElement.getDataElement().getSnunitDataType().getCeCodedValue();
                        }

                        if (messageElement.getDataElement().getSnunitDataType().getCeCodedValueDescription() != null) {
                            codedValueDescription = messageElement.getDataElement().getSnunitDataType().getCeCodedValueDescription();
                        }

                        if (messageElement.getDataElement().getSnunitDataType().getCeCodedValueCodingSystem() != null) {
                            codedValueCodingSystem = messageElement.getDataElement().getSnunitDataType().getCeCodedValueCodingSystem();
                        }

                        if (messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValue() != null) {
                            localCodedValue = messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValue();
                        }

                        if (messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueDescription() != null) {
                            localCodedValueDescription = messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueDescription();
                        }

                        if (messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueCodingSystem() != null) {
                            localCodedValueCodingSystem = messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueCodingSystem();
                        }

                        CE currentObxUnits = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getUnits();

                        currentObxUnits.getIdentifier().setValue(codedValue);
                        currentObxUnits.getNameOfCodingSystem().setValue(codedValueCodingSystem);
                        currentObxUnits.getText().setValue(codedValueDescription);
                        currentObxUnits.getAlternateIdentifier().setValue(localCodedValue);
                        currentObxUnits.getNameOfAlternateCodingSystem().setValue(localCodedValueCodingSystem);
                        currentObxUnits.getAlternateText().setValue(localCodedValueDescription);
                    }
                    else {
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getValueType().setValue(messageElement.getDataElement().getQuestionDataTypeNND());
                        valueTypeChecker = 1;
                        }

                    String observationValue = mapToObservationValue(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getObservationValue(0).toString());
                    // TODO - Validate this loop to make sure this observationValue is right
                    ST stDataType = (ST) oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getObservationValue(0).getData();
                    stDataType.setValue(observationValue);
                    // mapToObservationValue(messageElement,out.PATIENT_RESULT.ORDER_OBSERVATION[obrSubCounter].OBSERVATION[observationCounter].OBX[resultedTestCounter].ObservationValue[0]);
                }
                else if (dataElement.contains("6.")) {
                    mapToCEType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getUnits());
                }
                else if (dataElement.contains("7.")) {
                    String dataLocator = dataElement.replace("7.", "");

                    if(messageElement.getQuestionIdentifier().equals("LAB119") || messageElement.getQuestionIdentifier().equals("NBS373")) {
                        referenceRangeFrom = messageElement.getDataElement().getStDataType().getStringData();
                    }
                    if(messageElement.getQuestionIdentifier().equals("LAB120") || messageElement.getQuestionIdentifier().equals("NBS374")) {
                        referenceRangeTo = messageElement.getDataElement().getStDataType().getStringData();
                    }
                }
                else if (dataElement.contains("8.")) {
                    interpretationFlag = messageElement.getDataElement().getIsDataType().getIsCodedValue();

                }
                else if (dataElement.contains("11.")) {
                    resultStatus =messageElement.getDataElement().getIdDataType().getIdCodedValue();

                }
                else if (dataElement.contains("14.")) {
                    specimenCollectionDate = messageElement.getDataElement().getTsDataType().getTime().toString();

                }
                else if (dataElement.contains("17.")) {
                    resultMethod.setCode(messageElement.getDataElement().getCeDataType().getCeCodedValue());
                    resultMethod.setNameOfCodingSystem(messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem());
                    resultMethod.setText(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
                }
                else if (dataElement.contains("19.")) {
                    dateTimeOfAnalysis = messageElement.getDataElement().getTsDataType().getTime().toString();
                }
                else if (dataElement.contains("23.1.")) {
                }
            }
        }

        String obxValueType = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getValueType().getValue();

        if(obxValueType == null || obxValueType.equals("")) {
//            setEmpty(oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getSPECIMEN(observationCounter));
            int length = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getSPECIMEN(observationCounter).getOBXReps();
            for (int counter = 0; counter < length; counter++) {
                oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getSPECIMEN(observationCounter).removeOBX(counter);
            }
        }
        if(valueTypeChecker == 0){
            int counter = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getObservationValue().length;
            if(counter > 0){
                resultedTestCounter =resultedTestCounter + 1;
            }
            ST stDataType = (ST) oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getObservationValue(0).getData();
            stDataType.setValue("\"\"");
            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getObservationValue(0).setData(stDataType);
            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getSetIDOBX().setValue(String.valueOf(resultedTestCounter + 1));
            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getValueType().setValue("TX");
        }
    }

    // TODO - Check where this return value is being used
    private String mapCodedToStringValue(MessageElement messageElement, String obxResult, ParentLink parentLink) {
        if (messageElement.getDataElement().getCeDataType() != null) {

            if (messageElement.getDataElement().getCeDataType().getCeCodedValue() != null
                    && !messageElement.getDataElement().getCeDataType().getCeCodedValue().isEmpty()) {
                String ceCodedValue = messageElement.getDataElement().getCeDataType().getCeCodedValue();
                obxResult = ceCodedValue;
                parentLink.setIdentifier(ceCodedValue);
            } else {
                obxResult = "MISSING";
            }

            if (messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem() != null
                    && !messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem().isEmpty()) {
                String ceCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem();
                parentLink.setNameOfCodingSystem(ceCodedValueCodingSystem);
                obxResult = obxResult + "^" + ceCodedValueCodingSystem;
            } else {
                obxResult = "MISSING";
            }

            if (messageElement.getDataElement().getCeDataType().getCeCodedValueDescription() != null
                    && !messageElement.getDataElement().getCeDataType().getCeCodedValueDescription().isEmpty()) {
                String ceCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeCodedValueDescription();
                parentLink.setText(ceCodedValueDescription);
                obxResult = obxResult + "^" + ceCodedValueDescription;
            } else {
                obxResult = "MISSING";
            }
        }

        if (messageElement.getDataElement().getCweDataType() != null) {
            if (messageElement.getDataElement().getCweDataType().getCweCodedValue() != null
                    && !messageElement.getDataElement().getCweDataType().getCweCodedValue().isEmpty()) {
                String cweCodedValue = messageElement.getDataElement().getCweDataType().getCweCodedValue();
                obxResult = cweCodedValue;
                parentLink.setIdentifier(cweCodedValue);
            } else {
                obxResult = "MISSING";
            }

            if (messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem() != null
                    && !messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem().isEmpty()) {
                String cweCodedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem();
                parentLink.setNameOfCodingSystem(cweCodedValueCodingSystem);
                obxResult = obxResult + "^" + cweCodedValueCodingSystem;
            } else {
                obxResult = obxResult + "^" + "MISSING";
            }

            if (messageElement.getDataElement().getCweDataType().getCweCodedValueDescription() != null
                    && !messageElement.getDataElement().getCweDataType().getCweCodedValueDescription().isEmpty()) {
                String cweCodedValueDescription = messageElement.getDataElement().getCweDataType().getCweCodedValueDescription();
                parentLink.setText(cweCodedValueDescription);
                obxResult = obxResult + "^" + cweCodedValueDescription;
            } else {
                obxResult = obxResult + "^" + "MISSING";
            }
        }
        return obxResult;
    }

    private String mapToObservationValue(MessageElement messageElement, String observationValue) {
        String dataType = messageElement.getDataElement().getQuestionDataTypeNND();

        if ("XPN".equals(dataType) || "SN".equals(dataType)) {
            if (messageElement.getDataElement().getSnDataType() != null) {
                String comparator = "";
                if (messageElement.getDataElement().getSnDataType().getComparator() != null && !messageElement.getDataElement().getSnDataType().getComparator().isEmpty()) {
                    comparator = messageElement.getDataElement().getSnDataType().getComparator();
                }

                String num1 = "";
                if (messageElement.getDataElement().getSnDataType().getNum1() != null && !messageElement.getDataElement().getSnDataType().getNum1().isEmpty()) {
                    num1 = messageElement.getDataElement().getSnDataType().getNum1();
                }

                String separatorSuffix = "";
                if (messageElement.getDataElement().getSnDataType().getSeparatorSuffix() != null && !messageElement.getDataElement().getSnDataType().getSeparatorSuffix().isEmpty()) {
                    separatorSuffix = messageElement.getDataElement().getSnDataType().getSeparatorSuffix();
                }

                String num2 = "";
                if (messageElement.getDataElement().getSnDataType().getNum2() != null && !messageElement.getDataElement().getSnDataType().getNum2().isEmpty()) {
                    num2 = messageElement.getDataElement().getSnDataType().getNum2();
                }
                observationValue = comparator + "^" +	num1 + "^" + separatorSuffix + "^" + num2;
            } else if (messageElement.getDataElement().getXpnDataType() != null) {
                String familyName = "";
                if(messageElement.getDataElement().getXpnDataType().getFamilyName() != null && messageElement.getDataElement().getXpnDataType().getFamilyName().isEmpty()) {
                    familyName = messageElement.getDataElement().getXpnDataType().getFamilyName();
                }

                String givenName = "";
                if(messageElement.getDataElement().getXpnDataType().getGivenName() != null && messageElement.getDataElement().getXpnDataType().getGivenName().isEmpty()) {
                    familyName = messageElement.getDataElement().getXpnDataType().getGivenName();
                }

                observationValue = familyName + "^" + givenName;
            }
        }

        if ("XTN".equals(dataType)) {
            String telecommunicationUseCode = "";
            if (messageElement.getDataElement().getXtnDataType().getTelecommunicationUseCode() != null &&
                    !messageElement.getDataElement().getXtnDataType().getTelecommunicationUseCode().isEmpty()) {
                telecommunicationUseCode = messageElement.getDataElement().getXtnDataType().getTelecommunicationUseCode();
            }

            String telecommunicationEquipmentType = "";
            if (messageElement.getDataElement().getXtnDataType().getTelecommunicationEquipmentType() != null &&
                    !messageElement.getDataElement().getXtnDataType().getTelecommunicationEquipmentType().isEmpty()) {
                telecommunicationEquipmentType = messageElement.getDataElement().getXtnDataType().getTelecommunicationEquipmentType();
            }

            String emailAddress = "";
            if (messageElement.getDataElement().getXtnDataType().getEmailAddress() != null &&
                    !messageElement.getDataElement().getXtnDataType().getEmailAddress().isEmpty()) {
                emailAddress = messageElement.getDataElement().getXtnDataType().getEmailAddress();
            }

            String areaOrCityCode = "";
            if (messageElement.getDataElement().getXtnDataType().getAreaOrCityCode() != null &&
                    !messageElement.getDataElement().getXtnDataType().getAreaOrCityCode().isEmpty()) {
                areaOrCityCode = messageElement.getDataElement().getXtnDataType().getAreaOrCityCode();
            }

            String phoneNumber = "";
            if (messageElement.getDataElement().getXtnDataType().getPhoneNumber() != null &&
                    !messageElement.getDataElement().getXtnDataType().getPhoneNumber().isEmpty()) {
                phoneNumber = messageElement.getDataElement().getXtnDataType().getPhoneNumber();
            }

            observationValue = "^" + telecommunicationUseCode + "^" + telecommunicationEquipmentType + "^" +
                    emailAddress + "^^" + areaOrCityCode + "^" + phoneNumber;
        }


        if ("SN_WITH_UNIT".equals(dataType)) {
            String comparator = "";
            if (messageElement.getDataElement().getSnunitDataType().getComparator() != null && !messageElement.getDataElement().getSnunitDataType().getComparator().isEmpty()) {
                comparator = messageElement.getDataElement().getSnunitDataType().getComparator();
            }

            String num1 = "";
            if (messageElement.getDataElement().getSnunitDataType().getNum1() != null && !messageElement.getDataElement().getSnunitDataType().getNum1().isEmpty()) {
                num1 = messageElement.getDataElement().getSnunitDataType().getNum1();
            }

            String separatorSuffix = "";
            if (messageElement.getDataElement().getSnunitDataType().getSeparatorSuffix() != null && !messageElement.getDataElement().getSnunitDataType().getSeparatorSuffix().isEmpty()) {
                separatorSuffix = messageElement.getDataElement().getSnunitDataType().getSeparatorSuffix();
            }

            String num2 = "";
            if (messageElement.getDataElement().getSnunitDataType().getNum2() != null && !messageElement.getDataElement().getSnunitDataType().getNum2().isEmpty()) {
                num2 = messageElement.getDataElement().getSnunitDataType().getNum2();
            }
            observationValue = comparator + "^" +	num1 + "^" + separatorSuffix + "^" + num2;
        }

        if ("ST".equals(dataType)) {
            String stringData = messageElement.getDataElement().getStDataType().getStringData();
            observationValue = stringData;
        }

        if ("TX".equals(dataType)) {
            String textData = messageElement.getDataElement().getTxDataType().getTextData();
            observationValue = textData.replace("\n", " ");
        }

        if ("ID".equals(dataType)) {
            String idCodedValue = messageElement.getDataElement().getIdDataType().getIdCodedValue();
            observationValue = idCodedValue;
        }

        if ("IS".equals(dataType)) {
            String isCodedValue = messageElement.getDataElement().getIsDataType().getIsCodedValue();
            observationValue = isCodedValue;
        }

        if ("CWE".equals(dataType)) {
            String codedValue = "";
            if (messageElement.getDataElement().getCweDataType().getCweCodedValue() != null
                    && !messageElement.getDataElement().getCweDataType().getCweCodedValue().isEmpty()) {
                codedValue = messageElement.getDataElement().getCweDataType().getCweCodedValue();
            }

            String codedValueDescription = "";
            if (messageElement.getDataElement().getCweDataType().getCweCodedValueDescription() != null
                    && !messageElement.getDataElement().getCweDataType().getCweCodedValueDescription().isEmpty()) {
                codedValueDescription = messageElement.getDataElement().getCweDataType().getCweCodedValueDescription();
            }

            String codedValueCodingSystem = "";
            if (messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem() != null
                    && !messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem().isEmpty()) {
                codedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem();
            }

            String localCodedValue = "";
            if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValue() != null
                    && !messageElement.getDataElement().getCweDataType().getCweLocalCodedValue().isEmpty()) {
                localCodedValue = messageElement.getDataElement().getCweDataType().getCweLocalCodedValue();
            }

            String localCodedValueDescription = "";
            if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription() != null
                    && !messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription().isEmpty()) {
                localCodedValueDescription = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription();
            }

            String localCodedValueCodingSystem = "";
            if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem() != null
                    && !messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem().isEmpty()) {
                localCodedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem();
            }

            String originalText = "";
            if (messageElement.getDataElement().getCweDataType().getCweOriginalText() != null
                    && !messageElement.getDataElement().getCweDataType().getCweOriginalText().isEmpty()) {
                originalText = "^^^" + messageElement.getDataElement().getCweDataType().getCweOriginalText();
            }

            observationValue = codedValue + "^" +
                    codedValueDescription + "^" +
                    codedValueCodingSystem + "^" +
                    localCodedValue + "^" +
                    localCodedValueDescription + "^" +
                    localCodedValueCodingSystem +
                    originalText;
        }

        if ("CE".equals(dataType)) {
            String codedValue = "";
            if (messageElement.getDataElement().getCeDataType().getCeCodedValue() != null
                    && !messageElement.getDataElement().getCeDataType().getCeCodedValue().isEmpty()) {
                codedValue = messageElement.getDataElement().getCeDataType().getCeCodedValue();
            }

            String codedValueDescription = "";
            if (messageElement.getDataElement().getCeDataType().getCeCodedValueDescription() != null
                    && !messageElement.getDataElement().getCeDataType().getCeCodedValueDescription().isEmpty()) {
                codedValueDescription = messageElement.getDataElement().getCeDataType().getCeCodedValueDescription();
            }

            String codedValueCodingSystem = "";
            if (messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem() != null
                    && !messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem().isEmpty()) {
                codedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem();
            }

            String localCodedValue = "";
            if (messageElement.getDataElement().getCeDataType().getCeLocalCodedValue() != null
                    && !messageElement.getDataElement().getCeDataType().getCeLocalCodedValue().isEmpty()) {
                localCodedValue = messageElement.getDataElement().getCeDataType().getCeLocalCodedValue();
            }

            String localCodedValueDescription = "";
            if (messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription() != null
                    && !messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription().isEmpty()) {
                localCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription();
            }

            String localCodedValueCodingSystem = "";
            if (messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem() != null
                    && !messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem().isEmpty()) {
                localCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem();
            }

            observationValue = codedValue + "^" +
                    codedValueDescription + "^" +
                    codedValueCodingSystem + "^" +
                    localCodedValue + "^" +
                    localCodedValueDescription + "^" +
                    localCodedValueCodingSystem;
        }
        return observationValue;
    }



//    public void mapToDTM18(String in, DTM out) {
//        int length = in.length();
//        // TODO - Check this method
//        out.setYear(parseIntSafe(in, 0, 4));
//        out.setMonth(length >= 7 ? parseIntSafe(in, 4, 6) : null);
//        out.setDay(length >= 10 ? parseIntSafe(in, 6, 8) : null);
//        out.setHours(length >= 13 ? parseIntSafe(in, 8, 10) : null);
//        out.setMinutes(length >= 16 ? parseIntSafe(in, 10, 12) : null);
//        out.setSeconds(length >= 19 ? parseIntSafe(in, 12, 14) : null);
//
//        if (length >= 23) {
//            out.setSeparator(".");
//            out.setMillis(parseIntSafe(in, 14, 17));
//        } else {
//            out.setSeparator(".");
//            out.setMillis(null);
//        }
//
//        if (length > 23) {
//            out.setGmtOffset(in.substring(17));
//        }
//    }

//    private Integer parseIntSafe(String str, int start, int end) {
//        try {
//            if (start >= str.length()) return null;
//            end = Math.min(end, str.length());
//            return Integer.parseInt(str.substring(start, end));
//        } catch (NumberFormatException e) {
//            return null;
//        }
//    }


    public void mapToCEType(MessageElement input, CE output) throws DataTypeException {
        if (input.getDataElement().getCeDataType() != null) {
            MessageElement.DataElement.CeDataType ce = input.getDataElement().getCeDataType();

            if (ce.getCeCodedValue() != null) {
                output.getIdentifier().setValue(ce.getCeCodedValue());
            } else {
                output.getIdentifier().setValue("MISSING");
            }

            if (ce.getCeCodedValueCodingSystem() != null) {
                output.getNameOfCodingSystem().setValue(ce.getCeCodedValueCodingSystem());
            } else {
                output.getNameOfCodingSystem().setValue("MISSING");
            }

            if (ce.getCeCodedValueDescription() != null) {
                output.getText().setValue(ce.getCeCodedValueDescription());
            } else {
                output.getText().setValue("MISSING");
            }
        }

        if (input.getDataElement().getCweDataType() != null) {
            MessageElement.DataElement.CweDataType cwe = input.getDataElement().getCweDataType();

            if (cwe.getCweCodedValue() != null) {
                output.getIdentifier().setValue(cwe.getCweCodedValue());
            } else {
                output.getIdentifier().setValue("MISSING");
            }

            if (cwe.getCweCodedValueCodingSystem() != null) {
                output.getNameOfCodingSystem().setValue(cwe.getCweCodedValueCodingSystem());
            } else {
                output.getNameOfCodingSystem().setValue("MISSING");
            }

            if (cwe.getCweCodedValueDescription() != null) {
                output.getText().setValue(cwe.getCweCodedValueDescription());
            } else {
                output.getText().setValue("MISSING");
            }

            if (cwe.getCweLocalCodedValue() != null) {
                output.getAlternateIdentifier().setValue(cwe.getCweLocalCodedValue());
            } else {
                output.getAlternateIdentifier().setValue("MISSING");
            }

            if (cwe.getCweLocalCodedValueCodingSystem() != null) {
                output.getNameOfAlternateCodingSystem().setValue(cwe.getCweLocalCodedValueCodingSystem());
            } else {
                output.getNameOfAlternateCodingSystem().setValue("MISSING");
            }

            if (cwe.getCweLocalCodedValueDescription() != null) {
                output.getAlternateText().setValue(cwe.getCweLocalCodedValueDescription());
            } else {
                output.getAlternateText().setValue("MISSING");
            }
        }
    }

    private void mapToCWEType(MessageElement input, CWE output) throws DataTypeException {
        MessageElement.DataElement.CweDataType cweDataType = input.getDataElement().getCweDataType();
        if(cweDataType.getCweCodedValue() != null)
        {
            output.getIdentifier().setValue(cweDataType.getCweCodedValue());
        } else {
            output.getIdentifier().setValue("MISSING");
        }
        if(cweDataType.getCweCodedValueCodingSystem() != null)
        {
            output.getNameOfCodingSystem().setValue(cweDataType.getCweCodedValueCodingSystem());
        } else {
            output.getNameOfCodingSystem().setValue("MISSING");
        }
        if(cweDataType.getCweCodedValueDescription() != null)
        {
            output.getText().setValue(cweDataType.getCweCodedValueDescription());
        } else {
            output.getText().setValue("MISSING");
        }

    }

    private void mapToEIType(MessageElement input, EI output) throws DataTypeException {
        EiDataType eiDataType = input.getDataElement().getEiDataType();
        if(eiDataType.getEntityIdentifier() != null) {
            output.getEntityIdentifier().setValue(eiDataType.getEntityIdentifier());
        }
        if(eiDataType.getNamespaceId() != null) {
            output.getNamespaceID().setValue(eiDataType.getNamespaceId());
        }
        if(eiDataType.getUniversalId() != null) {
            output.getUniversalID().setValue(eiDataType.getUniversalId());
        }
        if(eiDataType.getUniversalIdType() != null) {
            output.getUniversalIDType().setValue(eiDataType.getUniversalIdType());
        }
    }

    /**
     * Processes each field of the MSH segment found in the  XML file.
     * This method extracts the relevant data from the MessageElement and
     * updates the MSH object.
     *
     * @param messageElement The XML element representing a specific MSH field,
     *                       including its attributes and values.
     * @param msh The MSH object that is being built, which will be updated with
     *            data from the provided messageElement.
     */

    private void processMSHFields(MessageElement messageElement, MSH msh) throws DataTypeException {
        String mshField = messageElement.getHl7SegmentField().trim();
        String mshFieldValue = "";
        if (mshField.startsWith("MSH-3.1")){
            mshFieldValue = messageElement.getDataElement().getIsDataType().getIsCodedValue().trim();
            msh.getSendingApplication().getNamespaceID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-3.2")){
            mshFieldValue = messageElement.getDataElement().getStDataType().getStringData().trim();
            msh.getSendingApplication().getUniversalID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-3.3")){
            mshFieldValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            msh.getSendingApplication().getUniversalIDType().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-4.1")){
            mshFieldValue = messageElement.getDataElement().getIsDataType().getIsCodedValue().trim();
            msh.getSendingFacility().getNamespaceID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-4.2")){
            mshFieldValue = messageElement.getDataElement().getStDataType().getStringData().trim();
            msh.getSendingFacility().getUniversalID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-4.3")){
            mshFieldValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            msh.getSendingFacility().getUniversalIDType().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-5.1")){
            mshFieldValue = messageElement.getDataElement().getIsDataType().getIsCodedValue().trim();
            msh.getReceivingApplication().getNamespaceID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-5.2")){
            mshFieldValue = messageElement.getDataElement().getStDataType().getStringData().trim();
            msh.getReceivingApplication().getUniversalID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-5.3")){
            mshFieldValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            msh.getReceivingApplication().getUniversalIDType().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-6.1")){
            mshFieldValue = messageElement.getDataElement().getIsDataType().getIsCodedValue().trim();
            msh.getReceivingFacility().getNamespaceID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-6.2")){
            mshFieldValue = messageElement.getDataElement().getStDataType().getStringData().trim();
            msh.getReceivingFacility().getUniversalID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-6.3")){
            mshFieldValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            msh.getReceivingFacility().getUniversalIDType().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-9.3")){
            mshFieldValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            msh.getMessageType().getMessageStructure().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-10.0")){
            mshFieldValue = messageElement.getDataElement().getStDataType().getStringData().trim();
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            String currentTime = now.format(formatter);
            msh.getMessageControlID().setValue(mshFieldValue + currentTime);
        }else if (mshField.startsWith("MSH-11.1")){
            mshFieldValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            msh.getProcessingID().getProcessingID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-12.1")){
            mshFieldValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            msh.getVersionID().getVersionID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-21")){
            if (Objects.equals(messageElement.getOrderGroupId(), "1")){
                switch (mshField) {
                    case "MSH-21.0" -> {
                        isSingleProfile = false;
                        entityIdentifierGroup1 = messageElement.getDataElement().getStDataType().getStringData().trim();
                    }
                    case "MSH-21.1"-> {
                        nndMessageVersion = messageElement.getDataElement().getStDataType().getStringData().trim();
                        nameSpaceIDGroup1 = mshFieldValue;
                        msh.getMessageProfileIdentifier(0).getEntityIdentifier().setValue(nndMessageVersion);
                    }

                    case "MSH-21.2" -> {
                        String nameSpaceID = messageElement.getDataElement().getIsDataType().getIsCodedValue().trim();
                        msh.getMessageProfileIdentifier(0).getNamespaceID().setValue(nameSpaceID);
                    }
                    case "MSH-21.3" -> {
                        String universalID = messageElement.getDataElement().getStDataType().getStringData().trim();
                        msh.getMessageProfileIdentifier(0).getUniversalID().setValue(universalID);
                    }
                    case "MSH-21.4" -> {
                        String universalIDType = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
                        msh.getMessageProfileIdentifier(0).getUniversalIDType().setValue(universalIDType);
                    }
                }
            }else if (Objects.equals(messageElement.getOrderGroupId(), "2")){
                switch (mshField) {
                    case "MSH-21.0" ->{
                        messageType = messageElement.getDataElement().getStDataType().getStringData().trim();
                        entityIdentifierGroup2 = messageElement.getDataElement().getStDataType().getStringData().trim();
                        if (entityIdentifierGroup2.equals(Constants.GENERIC_MMG_VERSION)){
                            genericMMGv20 = true;
                        }
                    }
                    case "MSH-21.2" -> nameSpaceIDGroup2 = messageElement.getDataElement().getIsDataType().getIsCodedValue().trim();
                    case "MSH-21.3" -> universalIDGroup2 = messageElement.getDataElement().getStDataType().getStringData().trim();
                    case "MSH-21.4" -> universalIDTypeGroup2 = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
                }
            }

            //process MSH21 field
            if (isSingleProfile){
                msh.getMessageProfileIdentifier(0).getEntityIdentifier().setValue(entityIdentifierGroup2);
                msh.getMessageProfileIdentifier(0).getNamespaceID().setValue(nameSpaceIDGroup2);
                msh.getMessageProfileIdentifier(0).getUniversalID().setValue(universalIDGroup2);
                msh.getMessageProfileIdentifier(0).getUniversalIDType().setValue(universalIDTypeGroup2);
            }else{
                msh.getMessageProfileIdentifier(0).getEntityIdentifier().setValue(entityIdentifierGroup1);
                msh.getMessageProfileIdentifier(0).getNamespaceID().setValue(nameSpaceIDGroup1);
                msh.getMessageProfileIdentifier(0).getUniversalID().setValue(universalIDGroup1);
                msh.getMessageProfileIdentifier(0).getUniversalIDType().setValue(universalIDTypeGroup1);

                msh.getMessageProfileIdentifier(1).getEntityIdentifier().setValue(entityIdentifierGroup2);
                msh.getMessageProfileIdentifier(1).getNamespaceID().setValue(nameSpaceIDGroup2);
                msh.getMessageProfileIdentifier(1).getUniversalID().setValue(universalIDGroup2);
                msh.getMessageProfileIdentifier(1).getUniversalIDType().setValue(universalIDTypeGroup2);
            }
        }
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS");
        String currentTime = now.format(formatter);
        msh.getDateTimeOfMessage().getTime().setValue(currentTime);
    }
    /**
     * Processes each field of the PID segment found in the  XML file.
     * This method extracts the relevant data from the MessageElement and
     * updates the PID object.
     *
     * @param messageElement The XML element representing a specific PID field,
     *                       including its attributes and values.
     * @param pid The PID object that is being built, which will be updated with
     *            data from the provided messageElement.
     */
    private void processPIDFields(MessageElement messageElement, PID pid) throws DataTypeException {
        String pidField = messageElement.getHl7SegmentField().trim();
        String pidFieldValue = "";
        String questionDataTypeNND = messageElement.getDataElement().getQuestionDataTypeNND().trim();
        String questionIdentifierNND = messageElement.getQuestionIdentifierNND().trim();

        if (pidField.startsWith("PID-3.1")){
            //out.PATIENT_RESULT.PATIENT.PID.PatientIdentifierList.IDNumber= in.MessageElement[i].dataElement.LocalComplex#1#Grp1.cxDataType.cxData.#PCDATA;
            pid.getPatientIdentifierList(0).getIDNumber().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-3.4.1")){
            pid.getPatientIdentifierList(0).getAssigningAuthority().getNamespaceID().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-3.4.2")){
            pid.getPatientIdentifierList(0).getAssigningAuthority().getUniversalID().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-3.4.3")){
            pid.getPatientIdentifierList(0).getAssigningAuthority().getUniversalIDType().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-7.0")){
            pidFieldValue = messageElement.getDataElement().getTsDataType().getTime().toString();
            String dateFormat = getDateFormat(pidFieldValue, questionDataTypeNND, questionIdentifierNND,"PID-7");
            pid.getPid7_DateTimeOfBirth().getTime().setValue(dateFormat);
        }else if (pidField.startsWith("PID-8.0")) {
            pid.getPid8_AdministrativeSex().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-10.0")) {
            //TODO - need to find an XML message with PID-10 in order to extract values from the correct data type field
            int size = pid.getPid10_RaceReps();
            pid.getPid10_Race(raceIndex).getIdentifier().setValue(pidFieldValue);
            //pid.getPid10_Race(raceIndex).getText().setValue(pidFieldValue);
            //pid.getPid10_Race(raceIndex).getAlternateIdentifier().setValue(pidFieldValue);
            //pid.getPid10_Race(raceIndex).getAlternateText().setValue(pidFieldValue);
            //pid.getPid10_Race(raceIndex).getCe6_NameOfAlternateCodingSystem().setValue(pidFieldValue);
            raceIndex += 1;
        }else if (pidField.startsWith("PID-11.3")) {
            pid.getPid11_PatientAddress(cityIndex).getCity().setValue(pidFieldValue);
            cityIndex += 1;
        }else if (pidField.startsWith("PID-11.4")) {
            pid.getPid11_PatientAddress(stateIndex).getStateOrProvince().setValue(pidFieldValue);
            stateIndex += 1;
        }else if (pidField.startsWith("PID-11.5")) {
            pid.getPid11_PatientAddress(zipcodeIndex).getZipOrPostalCode().setValue(pidFieldValue);
            zipcodeIndex += 1;
        }else if (pidField.startsWith("PID-11.6")) {
            pid.getPid11_PatientAddress(countryIndex).getCountry().setValue(pidFieldValue);
            countryIndex += 1;
        }else if (pidField.startsWith("PID-11.7")) {
            pid.getPid11_PatientAddress(addressTypeIndex).getAddressType().setValue(pidFieldValue);
            addressTypeIndex += 1;
        }else if (pidField.startsWith("PID-11.9")) {
            pid.getPid11_PatientAddress(countryIndex).getCountyParishCode().setValue(pidFieldValue);
            countryIndex += 1;
        }else if (pidField.startsWith("PID-11.10")) {
            pid.getPid11_PatientAddress(0).getCensusTract().setValue(pidFieldValue);
        } else if (pidField.startsWith("PID-15.0")) {
            //TODO - need to find an XML message with PID-15 in order to extract values from the correct data type field
            pid.getPid15_PrimaryLanguage().getIdentifier().setValue(pidFieldValue);
            pid.getPid15_PrimaryLanguage().getText().setValue(pidFieldValue);
            pid.getPid15_PrimaryLanguage().getNameOfCodingSystem().setValue(pidFieldValue);
            pid.getPid15_PrimaryLanguage().getAlternateIdentifier().setValue(pidFieldValue);
            pid.getPid15_PrimaryLanguage().getAlternateText().setValue(pidFieldValue);
            pid.getPid15_PrimaryLanguage().getNameOfAlternateCodingSystem().setValue(pidFieldValue);

        } else if (pidField.startsWith("PID-16.0")) {
            //TODO - need to find an XML message with PID-16 in order to extract values from the correct data type field
            pid.getPid16_MaritalStatus().getIdentifier().setValue(pidFieldValue);
            pid.getPid16_MaritalStatus().getText().setValue(pidFieldValue);
            pid.getPid16_MaritalStatus().getNameOfCodingSystem().setValue(pidFieldValue);
            pid.getPid16_MaritalStatus().getAlternateIdentifier().setValue(pidFieldValue);
            pid.getPid16_MaritalStatus().getAlternateText().setValue(pidFieldValue);
            pid.getPid16_MaritalStatus().getNameOfAlternateCodingSystem().setValue(pidFieldValue);
        } else if (pidField.startsWith("PID-17.0")) {
            //TODO - need to find an XML message with PID-17 in order to extract values from the correct data type field
            pid.getPid17_Religion().getIdentifier().setValue(pidFieldValue);
            pid.getPid17_Religion().getText().setValue(pidFieldValue);
            pid.getPid17_Religion().getNameOfCodingSystem().setValue(pidFieldValue);
            pid.getPid17_Religion().getAlternateIdentifier().setValue(pidFieldValue);
            pid.getPid17_Religion().getAlternateText().setValue(pidFieldValue);
            pid.getPid17_Religion().getNameOfAlternateCodingSystem().setValue(pidFieldValue);
        } else if (pidField.startsWith("PID-22.0")) {
            //TODO - need to find an XML message with PID-22 in order to extract values from the correct data type field
            pid.getPid22_EthnicGroup(0).getIdentifier().setValue(pidFieldValue);
            pid.getPid22_EthnicGroup(0).getText().setValue(pidFieldValue);
            pid.getPid22_EthnicGroup(0).getNameOfCodingSystem().setValue(pidFieldValue);
            pid.getPid22_EthnicGroup(0).getAlternateIdentifier().setValue(pidFieldValue);
            pid.getPid22_EthnicGroup(0).getAlternateText().setValue(pidFieldValue);
            pid.getPid22_EthnicGroup(0).getNameOfAlternateCodingSystem().setValue(pidFieldValue);
        } else if (pidField.startsWith("PID-23.0")) {
            pid.getPid23_BirthPlace().setValue(pidFieldValue);
        } else if (pidField.startsWith("PID-24.0")) {
            pid.getPid24_MultipleBirthIndicator().setValue(pidFieldValue);
        } else if (pidField.startsWith("PID-25.0")) {
            pid.getPid25_BirthOrder().setValue(pidFieldValue);
        } else if (pidField.startsWith("PID-26.0")) {
            pid.getPid26_Citizenship(citizenshipTypeIndex).getIdentifier().setValue(pidFieldValue);
            pid.getPid26_Citizenship(citizenshipTypeIndex).getText().setValue(pidFieldValue);
            pid.getPid26_Citizenship(citizenshipTypeIndex).getNameOfCodingSystem().setValue(pidFieldValue);
            pid.getPid26_Citizenship(citizenshipTypeIndex).getAlternateIdentifier().setValue(pidFieldValue);
            pid.getPid26_Citizenship(citizenshipTypeIndex).getAlternateText().setValue(pidFieldValue);
            pid.getPid26_Citizenship(citizenshipTypeIndex).getNameOfAlternateCodingSystem().setValue(pidFieldValue);
            citizenshipTypeIndex += 1;
        } else if (pidField.startsWith("PID-27.0")) {
            pid.getPid27_VeteransMilitaryStatus().getIdentifier().setValue(pidFieldValue);
            pid.getPid27_VeteransMilitaryStatus().getText().setValue(pidFieldValue);
            pid.getPid27_VeteransMilitaryStatus().getNameOfCodingSystem().setValue(pidFieldValue);
            pid.getPid27_VeteransMilitaryStatus().getAlternateIdentifier().setValue(pidFieldValue);
            pid.getPid27_VeteransMilitaryStatus().getAlternateText().setValue(pidFieldValue);
            pid.getPid27_VeteransMilitaryStatus().getNameOfAlternateCodingSystem().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-29.0")) {
            pidFieldValue = messageElement.getDataElement().getTsDataType().getTime().toString().trim();
            String dateFormat = getDateFormat(pidFieldValue, questionDataTypeNND, questionIdentifierNND,"PID-29.0");
            pid.getPid29_PatientDeathDateAndTime().getTime().setValue(dateFormat);
        }else if (pidField.startsWith("PID-30.0")) {
            pid.getPid30_PatientDeathIndicator().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-31.0")) {
            pid.getPid31_IdentityUnknownIndicator().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-32.0")) {
            pid.getPid32_IdentityReliabilityCode(identityReliabilityCodeIndex).setValue(pidFieldValue);
            identityReliabilityCodeIndex +=1;
        }else if (pidField.startsWith("PID-33.0")) {
            String dateFormat = getDateFormat(pidFieldValue, questionDataTypeNND, questionIdentifierNND, "PID-33.0");
            pid.getPid33_LastUpdateDateTime().getTime().setValue(dateFormat);
        }else if (pidField.startsWith("PID-34.1")) {
            pid.getPid34_LastUpdateFacility().getNamespaceID().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-34.2")) {
            pid.getPid34_LastUpdateFacility().getUniversalID().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-34.3")) {
            pid.getPid34_LastUpdateFacility().getUniversalIDType().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-35")) {
            pid.getPid35_SpeciesCode().getIdentifier().setValue(pidFieldValue);
            pid.getPid35_SpeciesCode().getText().setValue(pidFieldValue);
            pid.getPid35_SpeciesCode().getNameOfCodingSystem().setValue(pidFieldValue);
            pid.getPid35_SpeciesCode().getAlternateIdentifier().setValue(pidFieldValue);
            pid.getPid35_SpeciesCode().getAlternateText().setValue(pidFieldValue);
            pid.getPid35_SpeciesCode().getNameOfAlternateCodingSystem().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-36")) {
            pid.getPid36_BreedCode().getIdentifier().setValue(pidFieldValue);
            pid.getPid36_BreedCode().getText().setValue(pidFieldValue);
            pid.getPid36_BreedCode().getNameOfCodingSystem().setValue(pidFieldValue);
            pid.getPid36_BreedCode().getAlternateIdentifier().setValue(pidFieldValue);
            pid.getPid36_BreedCode().getAlternateText().setValue(pidFieldValue);
            pid.getPid36_BreedCode().getNameOfAlternateCodingSystem().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-37")) {
            pid.getPid37_Strain().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-38")) {
            pid.getPid38_ProductionClassCode().getIdentifier().setValue(pidFieldValue);
            pid.getPid38_ProductionClassCode().getText().setValue(pidFieldValue);
            pid.getPid38_ProductionClassCode().getNameOfCodingSystem().setValue(pidFieldValue);
            pid.getPid38_ProductionClassCode().getAlternateIdentifier().setValue(pidFieldValue);
            pid.getPid38_ProductionClassCode().getAlternateText().setValue(pidFieldValue);
            pid.getPid38_ProductionClassCode().getNameOfAlternateCodingSystem().setValue(pidFieldValue);
        }
    }
    /**
     * Processes each field of the NK1 segment found in the  XML file.
     * This method extracts the relevant data from the MessageElement and
     * updates the NK1 object.
     *
     * @param messageElement The XML element representing a specific NK1 field,
     *                       including its attributes and values.
     * @param nk1 The NK1 object that is being built, which will be updated with
     *            data from the provided messageElement.
     */
    private void processNK1Fields(MessageElement messageElement, NK1 nk1) throws DataTypeException {
        String nk1Field = messageElement.getHl7SegmentField().trim();
        String ceCodedValue = messageElement.getDataElement().getCeDataType().getCeCodedValue().trim();
        String ceCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeCodedValueDescription().trim();
        String ceCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem().trim();
        String ceLocalCodedValue = messageElement.getDataElement().getCeDataType().getCeLocalCodedValue().trim();
        String ceLocalCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription().trim();
        String ceLocalCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem().trim();
        nk1.getSetIDNK1().setValue("1");

        if (nk1Field.equals("NK1-28.0")){
            nk1.getNk128_EthnicGroup(0).getIdentifier().setValue(ceCodedValue);
            nk1.getNk128_EthnicGroup(0).getText().setValue(ceCodedValueDescription);
            nk1.getNk128_EthnicGroup(0).getNameOfCodingSystem().setValue(ceCodedValueCodingSystem);
            nk1.getNk128_EthnicGroup(0).getAlternateIdentifier().setValue(ceLocalCodedValue);
            nk1.getNk128_EthnicGroup(0).getAlternateText().setValue(ceLocalCodedValueDescription);
            nk1.getNk128_EthnicGroup(0).getNameOfAlternateCodingSystem().setValue(ceLocalCodedValueCodingSystem);
            nk1.getNk128_EthnicGroup(0).getNameOfAlternateCodingSystem().setValue(ceLocalCodedValueCodingSystem);
        }else if (nk1Field.equals("NK1-35.0")){
            nk1.getNk135_Race(nk1RaceInc).getIdentifier().setValue(ceCodedValue);
            nk1.getNk135_Race(nk1RaceInc).getText().setValue(ceCodedValueDescription);
            nk1.getNk135_Race(nk1RaceInc).getNameOfCodingSystem().setValue(ceCodedValueCodingSystem);
            nk1.getNk135_Race(nk1RaceInc).getAlternateIdentifier().setValue(ceLocalCodedValue);
            nk1.getNk135_Race(nk1RaceInc).getAlternateText().setValue(ceLocalCodedValueDescription);
            nk1.getNk135_Race(nk1RaceInc).getNameOfAlternateCodingSystem().setValue(ceLocalCodedValueCodingSystem);
            nk1RaceInc +=1;
        }
    }
    /**
     * Processes each field of the OBR segment found in the  XML file.
     * This method extracts the relevant data from the MessageElement and
     * updates the OBR object.
     *
     * @param messageElement The XML element representing a specific OBR field,
     *                       including its attributes and values.
     * @param obr The OBR object that is being built, which will be updated with
     *            data from the provided messageElement.
     */
    private void processOBRFields(MessageElement messageElement, OBR obr) throws DataTypeException {
        String obrField = messageElement.getHl7SegmentField().trim();
        String obrFieldValue = ""; //initialize to an empty string, depends on the OBR segment field
        String orderGroupID = messageElement.getOrderGroupId();
        String questionDataTypeNND = messageElement.getDataElement().getQuestionDataTypeNND().trim();
        String questionIdentifierNND = messageElement.getQuestionIdentifierNND();

        if (obrField.startsWith("OBR-3.1")){
            obrFieldValue = messageElement.getDataElement().getStDataType().getStringData().trim();
            obr.getObr3_FillerOrderNumber().getEntityIdentifier().setValue(obrFieldValue);
            stateLocalID = obr.getObr3_FillerOrderNumber().getEntityIdentifier().getValue();
        }else if (obrField.startsWith("OBR-3.2") && Objects.equals(orderGroupID, "1")) {

            obr.getObr3_FillerOrderNumber().getNamespaceID().setValue(obrFieldValue);
        }else if (obrField.startsWith("OBR-3.2") && Objects.equals(orderGroupID, "2")){
            fillerOrderNumberNameSpaceIDGroup2 = obrFieldValue;
        }else if (obrField.startsWith("OBR-3.3") && Objects.equals(orderGroupID, "1")){
            obr.getObr3_FillerOrderNumber().getUniversalIDType().setValue(obrFieldValue);
        }else if (obrField.startsWith("OBR-3.3") && Objects.equals(orderGroupID, "2")){
            obr.getObr3_FillerOrderNumber().getUniversalIDType().setValue(obrFieldValue);
            fillerOrderNumberUniversalID2 = obrFieldValue;
        }else if (obrField.startsWith("OBR-3.4") && Objects.equals(orderGroupID, "1")){
            obr.getObr3_FillerOrderNumber().getUniversalIDType().setValue(obrFieldValue);
        }else if (obrField.startsWith("OBR-3.4") && Objects.equals(orderGroupID, "2")){
            fillerOrderNumberUniversalIDType2 = obrFieldValue;
        }else if (obrField.startsWith("OBR-4.1") && Objects.equals(orderGroupID, "1")) {
            obr.getObr4_UniversalServiceIdentifier().getIdentifier().setValue("68991-9");
            universalServiceIdentifierGroup1 = obrFieldValue;
        }else if (obrField.startsWith("OBR-4.1") && Objects.equals(orderGroupID, "2")){
            universalServiceIdentifierGroup2 = obrFieldValue;
        }else if (obrField.startsWith("OBR-4.2") && Objects.equals(orderGroupID, "1")){
            obr.getObr4_UniversalServiceIdentifier().getAlternateIdentifier().setValue("Epidemiologic Information");
            universalServiceIDTextGroup1 = obrFieldValue;
        }else if (obrField.startsWith("OBR-4.2") && Objects.equals(orderGroupID, "2")){
            universalServiceIdentifierGroup2 = obrFieldValue;
        }else if (obrField.startsWith("OBR-4.3") && Objects.equals(orderGroupID, "1")){
            obr.getObr4_UniversalServiceIdentifier().getNameOfCodingSystem().setValue("LN");
            universalServiceIDNameOfCodingSystemGroup1 = obrFieldValue;
        }else if (obrField.startsWith("OBR-4.3") && Objects.equals(orderGroupID, "2")){
            universalServiceIDNameOfCodingSystemGroup2 = obrFieldValue;
        }else if (obrField.startsWith("OBR-7.0")){
            obrFieldValue = messageElement.getDataElement().getTsDataType().getTime().toString().trim();
            String dateFormat = getDateFormat(obrFieldValue, questionDataTypeNND, questionIdentifierNND,"OBR-7.0");
            obr.getObr7_ObservationDateTime().getTime().setValue(dateFormat);
        }else if (obrField.startsWith("OBR-22.0")){
            obrFieldValue = messageElement.getDataElement().getTsDataType().getTime().toString().trim();
            String dateFormat = getDateFormat(obrFieldValue, questionDataTypeNND, questionIdentifierNND,"OBR-22.0");
            obr.getObr22_ResultsRptStatusChngDateTime().getTime().setValue(dateFormat);
        }else if (obrField.startsWith("OBR-25.0")){
            obr.getObr25_ResultStatus().setValue(obrFieldValue);
        }else if (obrField.startsWith("OBR-31.0")){
            String conditionCode = messageElement.getDataElement().getCeDataType().getCeCodedValue().trim();
            String codedValueDescription = messageElement.getDataElement().getCeDataType().getCeCodedValueDescription().trim();
            String codedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem().trim();
            String localCodedValue = messageElement.getDataElement().getCeDataType().getCeLocalCodedValue().trim();
            String localCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription().trim();
            String localCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem().trim();

            // instantiate class to process OBR-31 field
            String service = "";
            String action = "";
            String serviceActionConditionCode = "";

            Optional<ServiceActionPairModel> serviceActionPair = iServiceActionPairRepository.findByMessageProfileId(messageType);
            if(serviceActionPair.isPresent()) {
                service = serviceActionPair.get().getService();
                action = serviceActionPair.get().getAction();
                serviceActionConditionCode = serviceActionPair.get().getConditionCode();
            }

            //process the results and update OBR-31 field
            if (Objects.equals(service, "") || Objects.equals(action, "")){
                obr.getObr31_ReasonForStudy(0).getIdentifier().setValue(conditionCode);
            }else if(Objects.equals(serviceActionConditionCode, "")){
                obr.getObr31_ReasonForStudy(0).getIdentifier().setValue(serviceActionConditionCode);
            }else{
                obr.getObr31_ReasonForStudy(0).getIdentifier().setValue(conditionCode);
            }

            //update other fields
            obr.getObr31_ReasonForStudy(0).getText().setValue(codedValueDescription);
            obr.getObr31_ReasonForStudy(0).getNameOfCodingSystem().setValue(codedValueCodingSystem);
            obr.getObr31_ReasonForStudy(0).getAlternateIdentifier().setValue(localCodedValue);
            obr.getObr31_ReasonForStudy(0).getAlternateText().setValue(localCodedValueDescription);
            obr.getObr31_ReasonForStudy(0).getCe6_NameOfAlternateCodingSystem().setValue(localCodedValueCodingSystem);
        }
    }

    /**
     * Processes each field of the OBX segment found in the  XML file.
     * This method extracts the relevant data from the MessageElement and
     * updates the OBX object.
     *
     * @param messageElement The XML element representing a specific OBX field,
     *                       including its attributes and values.
     * @param obx The OBX object that is being built, which will be updated with
     *            data from the provided messageElement.
     */
    private void processOBXFields(MessageElement messageElement, ORU_R01_ORDER_OBSERVATION obx) throws HL7Exception {
        String obxField = messageElement.getHl7SegmentField().trim();
        String obxFieldValue = "";
//        if ((obxField.equals("OBX-3.0") || obxField.equals("OBX-5.9"))&& messageElement.getQuestionMap()!=null&& messageElement.getQuestionMap().trim().equals("|")){
//            mapToQuestionMap(messageElement, obx2Inc, obx);
//        }else
        if (obxField.equals("OBX-3.0")){
            int obxOrderGroupID = 0;
            int obxInc = 1;
            int obx5ValueInc = 0;
            String obx5ObservationSubID = null;
            boolean obxFound = false;
            String messageTypePattern = "CongenitalSyphilis_MMG_V1.0";

            Pattern pattern = Pattern.compile(messageTypePattern);
            String questionIdentifier = messageElement.getQuestionIdentifier().trim();
            String questionIdentifierNND = messageElement.getQuestionIdentifierNND().trim();
            // find Match
            Matcher matcher = pattern.matcher(messageType);
            if (matcher.find() && (questionIdentifier.equals("LAB588_MTH") || questionIdentifier.equals("INV290_MTH") || questionIdentifier.equals("INV291_MTH") ||
                    questionIdentifier.equals("STD123_MTH") || questionIdentifier.equals("LAB167_MTH"))) {
                //extract index of ObservationSubID
                int count = Integer.parseInt(messageElement.getObservationSubID().trim());
                if (count > dupRepeatCongenitalCounter) {
                    dupRepeatCongenitalCounter = count;
                }
            }else if ( questionIdentifierNND.equals("INV290") || questionIdentifierNND.equals("INV291")){
                inv290Inv291Counter = Integer.parseInt(messageElement.getObservationSubID().trim());
            }

            if (messageElement.getOrderGroupId().trim().equals("1")) {
                obxInc = obx1Inc;
                obxOrderGroupID = 0;
            }else{
                obxInc = obx2Inc;
                obxOrderGroupID = 1;
            }
            maxObr = obxOrderGroupID;
            maxObx = obxInc +1;

            //TODO for loop for multiple OBX processing
            for (Map.Entry<String, String> obxElement: obxRepeatingElementArray.entrySet()) {
                if (questionIdentifierNND.equals("INV290") || questionIdentifierNND.equals("INV291")) {
                    obxFound = false;
                }else if (Objects.equals(obxElement.getValue(), questionIdentifierNND)){
                    if (messageElement.getQuestionGroupSeqNbr()!=null && messageElement.getQuestionGroupSeqNbr()!=null) {
                        if (obxRepeatingElementArray.get(Constants.QUESTION_GROUP_SEQUENCE_NUMBER).equals(messageElement.getQuestionGroupSeqNbr().trim()) &&
                                obxRepeatingElementArray.get(Constants.OBSERVATION_SUB_ID).equals(messageElement.getObservationSubID().trim())) {
                            obxFound = true;
                        }
                    }else if ( messageElement.getQuestionGroupSeqNbr()==null && messageElement.getObservationSubID()!=null){
                        if (obxRepeatingElementArray.get(Constants.QUESTION_GROUP_SEQUENCE_NUMBER).equals(messageElement.getQuestionGroupSeqNbr().trim()) &&
                                obxRepeatingElementArray.get(Constants.OBSERVATION_SUB_ID).equals(messageElement.getObservationSubID().trim())) {
                            obxFound = true;
                        }
                    }else if (messageElement.getQuestionGroupSeqNbr()!=null && messageElement.getQuestionGroupSeqNbr()==null){
                        if (obxRepeatingElementArray.get(Constants.QUESTION_GROUP_SEQUENCE_NUMBER).equals(messageElement.getQuestionGroupSeqNbr().trim()) &&
                                obxRepeatingElementArray.get(Constants.OBSERVATION_SUB_ID)==null) {
                            obxFound = true;
                        }
                    }else if (messageElement.getQuestionGroupSeqNbr()==null && messageElement.getObservationSubID()==null){
                        obxFound = true;
                    }

                    //HEP specific code for repeating INV826/INV827
                    if (questionIdentifierNND.equals("INV826") || questionIdentifierNND.equals("INV827")) {
                        obxFound = false;
                    }

                    if (questionIdentifier.equals("INV826b") || questionIdentifier.equals("INV827b")) {
                        obxFound = false;
                    }

                    if (obxFound) {
                        //found existing element
                        int valueInc = Integer.parseInt(obxRepeatingElementArray.get("valueInc"));
                        obxRepeatingElementArray.put("valueInc", String.valueOf(valueInc+1));
                        obx5ValueInc = Integer.parseInt(obxRepeatingElementArray.get("valueInc"));
                        obxInc = Integer.parseInt(obxRepeatingElementArray.get("obxInc"));
                        obx5ObservationSubID = obxRepeatingElementArray.get(Constants.OBSERVATION_SUB_ID);
                    }

                }
            }
            if (!obxFound) {
                //Need to insert this NNDUID into array
                //Add NND UID, set value incrementor to 0 and store current obxInc
                //build map for obx element

                obxRepeatingElementArray.put("elementUID", messageElement.getQuestionIdentifierNND().trim());

                if (messageElement.getQuestionGroupSeqNbr()!=null){
                    obxRepeatingElementArray.put("questionGroupSequenceNumber", messageElement.getQuestionGroupSeqNbr().trim());
                }else{
                    obxRepeatingElementArray.put("questionGroupSequenceNumber", null);
                }

                if (messageElement.getObservationSubID()!=null){
                    obxRepeatingElementArray.put(Constants.OBSERVATION_SUB_ID, messageElement.getObservationSubID().trim());
                }else{
                    obxRepeatingElementArray.put(Constants.OBSERVATION_SUB_ID, null);
                }
                obxRepeatingElementArray.put("valueInc","0");
                obxRepeatingElementArray.put("obxInc",String.valueOf(obxInc));
            }
            /* This code will cover the situation with TB investigation where value is based off of question_identifer='INV121' and
			   question_identifier_nnd='INV177' and it is populated from frontend.*/
            if (questionIdentifierNND.equals("INV177")){
                inv177Found = true;
            }
            if (questionIdentifier.equals("INV111") || questionIdentifierNND.equals("INV120") || questionIdentifierNND.equals("INV121")){

                if (questionIdentifier.equals("INV111") && questionIdentifierNND.equals("DT")) {
                    String year = String.valueOf(messageElement.getDataElement().getDtDataType().getDate().getYear());
                    String month = String.valueOf(messageElement.getDataElement().getDtDataType().getDate().getMonth());
                    String day = String.valueOf(messageElement.getDataElement().getDtDataType().getDate().getDay());
                    newDate = year+month+day;
                }else{
                    String year = "";
                    try{
                        if (messageElement.getDataElement().getTsDataType() != null) {
                            if (messageElement.getDataElement().getTsDataType().getYear()!=null){
                                year = messageElement.getDataElement().getTsDataType().getYear().trim();
                            }
                            String month = String.valueOf(messageElement.getDataElement().getTsDataType().getTime().getMonth());
                            String day = String.valueOf(messageElement.getDataElement().getTsDataType().getTime().getDay());
                            newDate = year+month+day;
                        }
                    } catch (Exception e){
                        logger.error("processOBXFields exception occurred {} ",e.getMessage());
                    }

                }

                if (inv177Date.isEmpty()){
                    inv177Date = newDate;
                }
                obx.getOBSERVATION().getOBX().getSetIDOBX().setValue(String.valueOf(obxInc+1));

                if (messageElement.getObservationSubID()!=null){
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationSubID().setValue(messageElement.getObservationSubID().trim());
                }

                if (questionIdentifierNND.equals("SN_WITH_UNIT")){

                    obx.getOBSERVATION().getOBX().getValueType().setValue("SN");
                }else{
                    obx.getOBSERVATION().getOBX().getValueType().setValue(messageElement.getDataElement().getQuestionDataTypeNND().trim());
                }

                if (!obxFound) {
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().setValue(questionIdentifierNND);
                    String questionLabelNND = messageElement.getQuestionLabelNND().trim();
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getText().setValue(questionLabelNND);
                    String questionOID = messageElement.getQuestionOID().trim();
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getNameOfCodingSystem().setValue(questionOID);
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getAlternateIdentifier().setValue(questionIdentifier);
                    String questionLabel = messageElement.getQuestionLabel().trim();
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getAlternateText().setValue(questionLabel);

                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");

                    if (messageType.contains("CongenitalSyphilis_MMG_V1.0") && questionIdentifier.equals("LAB588")
                            || questionIdentifier.equals("INV290") || questionIdentifier.equals("INV291") || questionIdentifier.equals("STD123")|| questionIdentifier.equals("LAB167") || questionIdentifier.equals("STD123_1")){
                        obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationSubID().setValue("-"+messageElement.getObservationSubID().trim());

                    }else if (messageElement.getObservationSubID()!=null){
                        obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationSubID().setValue(messageElement.getObservationSubID().trim());
                    }else if (messageElement.getQuestionGroupSeqNbr()!=null){
                        obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationSubID().setValue(messageElement.getQuestionGroupSeqNbr().trim());
                    }

                }
                //XPN datatype
                if (messageElement.getDataElement().getQuestionDataTypeNND().equals("XPN") || messageElement.getDataElement().getQuestionDataTypeNND().equals("SN")){
                    String comparator = "";
                    if (messageElement.getDataElement().getSnDataType().getComparator()!=null){
                        comparator = messageElement.getDataElement().getSnDataType().getComparator().trim();
                    }
                    String num1 = "";
                    if (messageElement.getDataElement().getSnDataType().getNum1()!=null){
                        num1 = messageElement.getDataElement().getSnDataType().getNum1().trim();
                    }
                    String separatorSuffix = "";
                    if (messageElement.getDataElement().getSnDataType().getSeparatorSuffix()!=null){
                        separatorSuffix = messageElement.getDataElement().getSnDataType().getSeparatorSuffix().trim();
                    }

                    String num2 = "";
                    if (messageElement.getDataElement().getSnDataType().getNum2()!=null){
                        num2 = messageElement.getDataElement().getSnDataType().getNum2().trim();
                    }
                    SN snDT = (SN)obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();
                    snDT.getComparator().setValue(comparator);
                    snDT.getNum1().setValue(num1);
                    snDT.getSeparatorSuffix().setValue(separatorSuffix);
                    snDT.getNum2().setValue(num2);
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(snDT);

                    if (messageElement.getDataElement().getQuestionDataTypeNND().trim().equals("SN") &&
                            (questionIdentifier.equals("INV827b") || questionIdentifier.equals("11920_8"))
                    ){
                        obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationSubID().setValue("2");

                    }


                }

                //XTN datatype
                if (messageElement.getDataElement().getQuestionDataTypeNND().equals("XTN")){
                    String telecommunicationUseCode = "";
                    if (messageElement.getDataElement().getXtnDataType().getTelecommunicationUseCode()!=null){
                        telecommunicationUseCode = messageElement.getDataElement().getXtnDataType().getTelecommunicationUseCode().trim();
                    }
                    String telecommunicationEquipmentType = "";
                    if (messageElement.getDataElement().getXtnDataType().getTelecommunicationEquipmentType()!=null){
                        telecommunicationEquipmentType = messageElement.getDataElement().getXtnDataType().getTelecommunicationEquipmentType().trim();
                    }
                    String emailAddress = "";
                    if (messageElement.getDataElement().getXtnDataType().getEmailAddress()!=null){
                        emailAddress = messageElement.getDataElement().getXtnDataType().getEmailAddress().trim();
                    }
                    String areaOrCityCode = "";
                    if (messageElement.getDataElement().getXtnDataType().getAreaOrCityCode()!=null){
                        areaOrCityCode = messageElement.getDataElement().getXtnDataType().getAreaOrCityCode().trim();
                    }

                    String phoneNumber = "";
                    if (messageElement.getDataElement().getXtnDataType().getPhoneNumber()!=null){
                        phoneNumber = messageElement.getDataElement().getXtnDataType().getPhoneNumber().trim();
                    }

                    //build XTN object
                    XTN xtnDataType = (XTN)obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();

                    xtnDataType.getTelecommunicationUseCode().setValue(telecommunicationUseCode);
                    xtnDataType.getTelecommunicationEquipmentType().setValue(telecommunicationEquipmentType);
                    xtnDataType.getEmailAddress().setValue(emailAddress);
                    xtnDataType.getAreaCityCode().setValue(areaOrCityCode);
                    xtnDataType.getTelephoneNumber().setValue(phoneNumber);

                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(xtnDataType);
                }

                //XPN datatype
                if (messageElement.getDataElement().getQuestionDataTypeNND().equals("XPN")){
                    String familyName = "";
                    if (messageElement.getDataElement().getXpnDataType().getFamilyName()!=null){
                        familyName = messageElement.getDataElement().getXpnDataType().getFamilyName().trim();
                    }
                    String givenName = "";
                    if (messageElement.getDataElement().getXpnDataType().getGivenName()!=null){
                        givenName = messageElement.getDataElement().getXpnDataType().getGivenName().trim();
                    }

                    //Build XPN object
                    XPN xpn  = (XPN) obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();
                    xpn.getXpn1_FamilyName().getFn1_Surname().setValue(familyName);
                    xpn.getXpn2_GivenName().setValue(givenName);

                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(xpn);
                }
                //SN data type with unit
                if (messageElement.getDataElement().getQuestionDataTypeNND().trim().equals("SN_WITH_UNIT")){
                    String comparator = "";
                    if (messageElement.getDataElement().getSnunitDataType().getComparator()!=null){
                        comparator = messageElement.getDataElement().getSnunitDataType().getComparator().trim();
                    }

                    String num1 = "";
                    if (messageElement.getDataElement().getSnunitDataType().getNum1()!=null){
                        num1 = messageElement.getDataElement().getSnunitDataType().getNum1().trim();
                    }

                    String separatorSuffix = "";
                    if (messageElement.getDataElement().getSnunitDataType().getSeparatorSuffix()!=null){
                        separatorSuffix = messageElement.getDataElement().getSnunitDataType().getSeparatorSuffix().trim();
                    }
                    String num2 = "";
                    if (messageElement.getDataElement().getSnunitDataType().getNum2()!=null){
                        num2 = messageElement.getDataElement().getSnunitDataType().getNum2().trim();
                    }
                    SN snDataType = (SN)obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();
                    snDataType.getComparator().setValue(comparator);
                    snDataType.getNum1().setValue(num1);
                    snDataType.getSeparatorSuffix().setValue(separatorSuffix);
                    snDataType.getNum2().setValue(num2);
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(snDataType);

                    String codedValue = "";
                    if (messageElement.getDataElement().getSnunitDataType().getCeCodedValue()!=null){
                        codedValue = messageElement.getDataElement().getSnunitDataType().getCeCodedValue().trim();
                    }
                    String codedValueDescription = "";
                    if (messageElement.getDataElement().getSnunitDataType().getCeCodedValueDescription()!=null){
                        codedValueDescription = messageElement.getDataElement().getSnunitDataType().getCeCodedValueDescription().trim();
                    }

                    String codedValueCodingSystem = "";
                    if (messageElement.getDataElement().getSnunitDataType().getCeCodedValueCodingSystem()!=null){
                        codedValueCodingSystem = messageElement.getDataElement().getSnunitDataType().getCeCodedValueCodingSystem().trim();
                    }

                    String localCodedValue = "";
                    if (messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValue()!=null){
                        localCodedValue = messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValue().trim();
                    }
                    String localCodedValueDescription = "";
                    if (messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueDescription()!=null){
                        localCodedValueDescription = messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueDescription().trim();
                    }

                    String localCodedValueCodingSystem = "";
                    if (messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueCodingSystem()!=null){
                        localCodedValueCodingSystem = messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueCodingSystem().trim();
                    }

                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getUnits().getIdentifier().setValue(codedValue);
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getUnits().getText().setValue(codedValueDescription);
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getUnits().getNameOfCodingSystem().setValue(codedValueCodingSystem);
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getUnits().getAlternateIdentifier().setValue(localCodedValue);
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getUnits().getAlternateText().setValue(localCodedValueDescription);
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getUnits().getNameOfAlternateCodingSystem().setValue(localCodedValueCodingSystem);
                }

                if ((messageType.contains("Measles_MMG_V1.0") || messageType.contains("Rubella_MMG_V1.0")
                        || messageType.contains("CRS_MMG_V1.0") || messageType.contains("Varicella_MMG_V3.0") ||
                        messageType.contains("Pertussis_MMG_V1.0")|| messageType.contains("Mumps_MMG_V1.0")))
                {
                    String stData = messageElement.getDataElement().getStDataType().getStringData().trim();
                    String questionMap = messageElement.getQuestionMap().trim();
                    String cxData = messageElement.getDataElement().getCxDataType().getCxData().trim();
                    if (questionIdentifierNND.equals("LAB143")) {
                        String combined = questionIdentifierNND.trim()+"~"+obx5ObservationSubID;


                        String output = "";
                        if (!cxData.isEmpty() && cxData.contains(combined)){
                            int start = cxData.indexOf(combined);
                            String substring = cxData.substring(start);
                            int end = substring.indexOf("|"); //if not found, will return -1
                            if (end == -1){
                                end = substring.length() - (questionIdentifierNND.trim().length()+ messageElement.getObservationSubID().length()+1);
                            }
                            String cxString = substring.substring(0, end);

                            if (cxString.contains(":")) {
                                output = cxString.substring(cxString.indexOf(":") + 1);
                            } else {
                                output = cxString;
                            }

                            int part1 = output.indexOf("^");
                            String identifier = output.substring(0, part1);
                            String rest = output.substring(part1 + 1);

                            int part2 = rest.indexOf("^");
                            String description = rest.substring(0, part2);
                            String descriptionValue = rest.substring(part2 + 1);
                            obx.getOBSERVATION(1).getOBX().getValueType().setValue("ST");
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getSetIDOBX().setValue(String.valueOf(obxInc+1));
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().setValue(questionIdentifierNND);
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getText().setValue(messageElement.getQuestionLabelNND().trim());
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getNameOfCodingSystem().setValue(messageElement.getQuestionOID().trim());
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getAlternateIdentifier().setValue(messageElement.getQuestionIdentifier().trim());
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getAlternateText().setValue(messageElement.getQuestionLabelNND());
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");
                            ST stDataType = (ST)obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(0).getData();
                            stDataType.setValue(messageElement.getDataElement().getStDataType().getStringData().trim());
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(0).setData(stDataType);
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationResultStatus().setValue("F");
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationSubID().setValue(messageElement.getObservationSubID().trim());
                            obxInc +=1;
                            obx.getOBSERVATION(1).getOBX().getValueType().setValue("CX");
                            obx.getOBSERVATION(obxInc).getOBX().getSetIDOBX().setValue(String.valueOf(obxInc+1));
                            obx.getOBSERVATION(obxInc).getOBX().getObservationIdentifier().getIdentifier().setValue(identifier);
                            obx.getOBSERVATION(obxInc).getOBX().getObservationIdentifier().getText().setValue(description);
                            obx.getOBSERVATION(obxInc).getOBX().getObservationIdentifier().getNameOfCodingSystem().setValue(messageElement.getQuestionOID().trim());
                            obx.getOBSERVATION(obxInc).getOBX().getObservationIdentifier().getAlternateIdentifier().setValue(identifier);
                            obx.getOBSERVATION(obxInc).getOBX().getObservationIdentifier().getAlternateText().setValue(description);
                            obx.getOBSERVATION(obxInc).getOBX().getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");
                            String stringData = messageElement.getDataElement().getStDataType().getStringData().trim();
                            ST stTypeForObservationValue = (ST) obx.getOBSERVATION(obxInc).getOBX().getObservationValue(0).getData();
                            stTypeForObservationValue.setValue(descriptionValue+"^^^&"+stringData+"&ISO");
                            obx.getOBSERVATION(obxInc).getOBX().getObservationValue(0).setData(stTypeForObservationValue);
                            obx.getOBSERVATION(obxInc).getOBX().getObservationResultStatus().setValue("F");
                            obx.getOBSERVATION(obxInc).getOBX().getObservationSubID().setValue(messageElement.getObservationSubID().trim());
                            stData += "|"+questionIdentifierNND+"~"+obx5ObservationSubID+":"+stringData;
                        }else{
                            obx.getOBSERVATION(1).getOBX().getValueType().setValue("ST");
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getSetIDOBX().setValue(String.valueOf(obxInc+1));
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().setValue(questionIdentifierNND);
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getText().setValue(questionIdentifierNND);
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getNameOfCodingSystem().setValue(messageElement.getQuestionOID().trim());
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getAlternateIdentifier().setValue(questionIdentifierNND);
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getAlternateText().setValue(messageElement.getQuestionLabelNND().trim());
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");

                        }

                    }else if (questionIdentifierNND.equals("CX") && !questionMap.isEmpty() ) {
                        if (!stData.isEmpty()){
                            int startIndex = stData.indexOf(questionMap+"~"+messageElement.getObservationSubID().trim());
                            String subString = stData.substring(stData.length()-startIndex);
                            int endIndex = subString.indexOf("|");
                            if (endIndex == -1){
                                endIndex = subString.length()- (questionMap+"~"+messageElement.getObservationSubID().trim()).length()-1;
                            }

                            String STString = subString.substring(endIndex);
                            obx.getOBSERVATION(1).getOBX().getValueType().setValue("CX");
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getSetIDOBX().setValue(String.valueOf(obxInc+1));
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().setValue(questionIdentifierNND);
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getText().setValue(messageElement.getQuestionLabel().trim());
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getAlternateIdentifier().setValue(messageElement.getQuestionLabelNND());
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");

                            CX cxDataType = (CX)obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(0).getData();
                            cxDataType.getCx1_IDNumber().setValue(cxData);
                            cxDataType.getCx4_AssigningAuthority().getUniversalID().setValue(STString);
                            cxDataType.getCx4_AssigningAuthority().getNamespaceID().setValue("&ISO");
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(0).setData(cxDataType);
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationResultStatus().setValue("F");
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationSubID().setValue(messageElement.getObservationSubID().trim());

                        }else{
                            cxData +=cxData +"|"+ questionMap + "~" + messageElement.getObservationSubID().trim() + ":" + questionIdentifierNND+"^"+messageElement.getQuestionLabelNND().trim()+"^" +messageElement.getDataElement().getCxDataType().getCxData().trim();
                            obxInc -=1;
                        }
                    }
                }else if (messageType.contains("Arbo_Case_Map_v1.0") && questionIdentifier.equals("INV173") && messageElement.getDataElement().getQuestionDataTypeNND().trim().equals("ST")){
                    isDefaultNull = false;
                    ST stringData = (ST) obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();
                    stringData.setValue(messageElement.getDataElement().getStDataType().getStringData().trim());
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(stringData);
                }else if (messageElement.getDataElement().getQuestionDataTypeNND().trim().equals("ST")){
                    ST stringData = (ST) obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();
                    stringData.setValue(messageElement.getDataElement().getStDataType().getStringData().trim());
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(stringData);
                }
                //TX datatype
                if (messageElement.getDataElement().getQuestionDataTypeNND().trim().equals("TX")){
                    TX textData = (TX) obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();
                    String td = messageElement.getDataElement().getTxDataType().getTextData().trim();
                    textData.setValue(td);
                    if (questionIdentifierNND.equals("77999-1") && genericMMGv20) {
                        textData.setValue(td+hcw);
                        hcwObxInc = obxInc;
                        hcwObxOrderGroupId = obxOrderGroupID;
                        hcwObx5ValueInc = obx5ValueInc;
                        obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(textData);
                        hcwTextBeforeCodedInd = true;
                    }
                }
                //ID datatype
                if (messageElement.getDataElement().getQuestionDataTypeNND().trim().equals("ID")){
                    String idCodedValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
                    ID idType = (ID) obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();
                    idType.setValue(idCodedValue);
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(idType);
                }
                // IS datatype
                if (messageElement.getDataElement().getQuestionDataTypeNND().trim().equals("IS")){
                    String isCodedValue = messageElement.getDataElement().getIsDataType().getIsCodedValue().trim();
                    IS isType = (IS) obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();
                    isType.setValue(isCodedValue);
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(isType);
                }
                // CWE datatype
                if (messageElement.getDataElement().getQuestionDataTypeNND().trim().equals("CWE")){
                    String codedValue = "";
                    String codedValueDescription = "";
                    String codedValueCodingSystem = "";
                    String localCodedValue = "";
                    String localCodedValueDescription = "";
                    String localCodedValueCodingSystem = "";
                    if (messageElement.getDataElement().getCweDataType().getCweCodedValue()!=null){
                        codedValue = messageElement.getDataElement().getCweDataType().getCweCodedValue().trim();
                    }
                    if (messageElement.getDataElement().getCweDataType().getCweCodedValueDescription()!=null){
                        codedValueDescription = messageElement.getDataElement().getCweDataType().getCweCodedValueDescription().trim();
                    }
                    if (messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem()!=null){
                        codedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem().trim();
                    }

                    if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValue()!=null){
                        localCodedValue = messageElement.getDataElement().getCweDataType().getCweLocalCodedValue().trim();
                    }
                    if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription()!=null){
                        localCodedValueDescription = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription().trim();
                    }
                    if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem()!=null){
                        localCodedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem().trim();
                    }

                    String originalText = "";
                    if (messageElement.getDataElement().getCweDataType().getCweOriginalText()!=null){
                        originalText = messageElement.getDataElement().getCweDataType().getCweOriginalText().trim();
                        if (!originalText.isEmpty()){
                            originalText = originalText.replace("\\","\\E\\");
                            originalText = originalText.replace("|","\\F\\");
                            originalText = originalText.replace("~","\\R\\");
                            originalText = originalText.replace("^","\\S\\");
                            originalText = originalText.replace("&","\\T\\");
                        }
                    }

                    if (messageType.contains("Arbo_Case_Map_v1.0")|| messageType.contains("Gen_Case_Map_v1.0") || messageType.contains("TB_Case_Map_v2.0") || messageType.contains("Var_Case_Map_v2.0")){
                        CWE cweDatatype = (CWE) obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();
                        if (codedValue.isEmpty()){
                            cweDatatype.getCwe5_AlternateText().setValue(localCodedValueDescription);
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(cweDatatype);
                        }else{
                            cweDatatype.getAlternateText().setValue(codedValue+"^"+codedValueDescription+"^"+codedValueCodingSystem+"^"+originalText);
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(cweDatatype);
                        }
                    }else {
                        if (codedValue.isEmpty()){
                            CWE cweDataTYpe = (CWE)obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();
                            if (messageElement.getDataElement().getCweDataType().getCweCodedValue()==null){
                                codedValueDescription = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem().trim();
                                if (!codedValueDescription.isEmpty()){
                                    codedValueDescription = codedValueDescription.replace("\\","\\E\\");
                                    codedValueDescription = codedValueDescription.replace("|","\\F\\");
                                    codedValueDescription = codedValueDescription.replace("~","\\R\\");
                                    codedValueDescription = codedValueDescription.replace("^","\\S\\");
                                    codedValueDescription = codedValueDescription.replace("&","\\T\\");
                                }
                                cweDataTYpe.getCwe9_OriginalText().setValue(codedValueDescription);
                                obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(cweDataTYpe);
                            }else if (Objects.equals(messageElement.getDataElement().getCweDataType().getCweCodedValue(), "OTH")){
                                cweDataTYpe.getCwe9_OriginalText().setValue(localCodedValue+"^"+localCodedValueDescription+"^"+localCodedValueCodingSystem+"^^^^^^"+originalText);
                                obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(cweDataTYpe);
                            }else{
                                cweDataTYpe.getCwe9_OriginalText().setValue(localCodedValue+"^"+localCodedValueDescription+"^L^^^^^^"+originalText);
                                obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(cweDataTYpe);
                            }
                        }else{
                            CWE cweDataType = (CWE) obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();
                            cweDataType.getCwe9_OriginalText().setValue(codedValue+"^"+codedValueDescription+"^"+codedValueCodingSystem+"^"+localCodedValue+"^"+localCodedValueDescription+"^"+localCodedValueCodingSystem+"^^^"+originalText);
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(cweDataType);
                        }
                    }
                }
                //CE datatype
                if (messageElement.getDataElement().getQuestionDataTypeNND().equals("CE")){
                    String codedValue = "";
                    if (messageElement.getDataElement().getCeDataType().getCeCodedValue()!=null){
                        codedValue = messageElement.getDataElement().getCeDataType().getCeCodedValue().trim();
                    }
                    String codedValueDescription = "";
                    if (messageElement.getDataElement().getCeDataType().getCeCodedValueDescription()!=null){
                        codedValueDescription = messageElement.getDataElement().getCeDataType().getCeCodedValueDescription().trim();
                    }
                    String codedValueCodingSystem = "";
                    if (messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem()!=null){
                        codedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem().trim();
                    }
                    String localCodedValue = "";
                    if (messageElement.getDataElement().getCeDataType().getCeLocalCodedValue()!=null){
                        localCodedValue = messageElement.getDataElement().getCeDataType().getCeLocalCodedValue().trim();
                    }
                    String localCodedValueCodingSystem = "";
                    if (messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem()!=null){
                        localCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem();
                    }

                    CE ceDataType = (CE) obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();
                    ceDataType.getCe1_Identifier().setValue(codedValue);
                    ceDataType.getCe2_Text().setValue(codedValueDescription);
                    ceDataType.getCe3_NameOfCodingSystem().setValue(codedValueCodingSystem);
                    ceDataType.getCe4_AlternateIdentifier().setValue(localCodedValue);
                    ceDataType.getCe6_NameOfAlternateCodingSystem().setValue(localCodedValueCodingSystem);

                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(ceDataType);
                }
                //DT datatype
                if (messageElement.getDataElement().getQuestionDataTypeNND().equals("DT") && INV162RepeatIndicator && questionIdentifier.equals("INV162")){
                    // do nothing as this is a repeat date and we only keep the first date
                }else if (messageElement.getDataElement().getQuestionDataTypeNND().equals("DT")){
                    //DT dtDataType = (DT) obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();
                    Primitive dtDataType = (Primitive) obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();

                    if (questionIdentifier.equals("INV162")){
                        INV162RepeatIndicator = true;
                    }
                    if (messageElement.getDataElement().getDtDataType().getYear()!=null){
                        dtDataType.setValue(messageElement.getDataElement().getDtDataType().getYear().trim());
                        obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(dtDataType);
                    }else{
                        int year = messageElement.getDataElement().getDtDataType().getDate().getYear();
                        int month = messageElement.getDataElement().getDtDataType().getDate().getMonth();
                        int day = messageElement.getDataElement().getDtDataType().getDate().getDay();
//                        dtDataType.setYearMonthDayPrecision(year, month, day);
                        dtDataType.setValue(String.valueOf(year) + String.valueOf(month) + String.valueOf(day));
                        obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(dtDataType);
                    }
                    //HEP specific code for repeating INV826/INV
                    if (questionIdentifierNND.equals("INV826")){
                        obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationSubID().setValue("1");
                    }
                    if (questionIdentifierNND.equals("INV826b")){
                        obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationSubID().setValue("2");
                    }
                }
                //TS data type
                if (messageElement.getDataElement().getQuestionDataTypeNND().equals("TS")){
                    String timeOutput = "";
                    TS tsDataType = (TS) obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();

                    if (messageElement.getDataElement().getTsDataType().getYear()!=null){
                        timeOutput = getDateFormat(messageElement.getDataElement().getTsDataType().getYear().trim(),questionIdentifierNND, messageType,messageElement.getDataElement().getQuestionDataTypeNND().trim());
                        tsDataType.getTs2_DegreeOfPrecision().setValue(timeOutput);
                        obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(tsDataType);
                    }else{
                        timeOutput = getDateFormat(messageElement.getDataElement().getTsDataType().getTime().toString(),questionIdentifierNND, messageType,messageElement.getDataElement().getQuestionDataTypeNND().trim());
                        tsDataType.getTs1_Time().setValue(timeOutput);
                        obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(tsDataType);
                    }
                }
                // NM datatype
                if (messageElement.getDataElement().getQuestionDataTypeNND().equals("NM")){
                    NM nmDataType = (NM) obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();
                    nmDataType.setValue(messageElement.getDataElement().getNmDataType().getNum().trim());
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).setData(nmDataType);
                }
                //Literal value "F" specified in messaging spec as ALWAYS being sent here
                obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationSubID().setValue("F");

                String existingObservationIdentifier = obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().toString();
                if (existingObservationIdentifier.equals("2653") ||
                        existingObservationIdentifier.equals("3304") ||
                        existingObservationIdentifier.equals("6816") ||
                        existingObservationIdentifier.equals("N0000166993") ||
                        existingObservationIdentifier.equals("PHC1160") ||
                        existingObservationIdentifier.equals("PHC1166") ||
                        existingObservationIdentifier.equals("PHC1167") ||
                        existingObservationIdentifier.equals("PHC1308") ){
                    drugCounter +=1;
                    String codedText = "";
                    // String observationValue = obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(0).toString().trim();
                    switch (existingObservationIdentifier) {
                        case "2653" -> {
                            codedText = obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().getValue().trim() + "^Cocaine^2.16.840.1.113883.6.88";
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().setValue(codedText);
                        }
                        case "3304" -> {
                            codedText = obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().getValue().trim() + "^Heroin^2.16.840.1.113883.6.88";
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().setValue(codedText);
                        }
                        case "6816" -> {
                            codedText = obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().getValue().trim() + "^Methamphetamines^2.16.840.1.113883.6.88";
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().setValue(codedText);
                        }
                        case "N0000166993" -> {
                            codedText = obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().getValue().trim() + "^Crack^2.16.840.1.113883.3.26.1.5";
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().setValue(codedText);
                        }
                        case "PHC1160" -> {
                            codedText = obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().getValue().trim() + "^Erectile dysfunction medications (e.g., Viagra)^2.16.840.1.114222.4.5.274";
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().setValue(codedText);
                        }
                        case "PHC1166" -> {
                            codedText = obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().getValue().trim() + "^Nitrates/Poppers^2.16.840.1.114222.4.5.274";
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().setValue(codedText);
                        }
                        case "PHC1167" -> {
                            codedText = obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().getValue().trim() + "^No drug use reported^2.16.840.1.114222.4.5.274";
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().setValue(codedText);
                        }
                        case "PHC1308" -> {
                            codedText = obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().getValue().trim() + "^Other Drugs Used^2.16.840.1.114222.4.5.274";
                            obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().setValue(codedText);
                        }
                    }

                    // TODO Check this piece

                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationSubID().setValue("2");
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getIdentifier().setValue("STD115");
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getText().setValue("Drugs Used");
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getNameOfCodingSystem().setValue("2.16.840.1.114222.4.5.232");
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getAlternateIdentifier().setValue("STD115");
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getAlternateText().setValue("Drugs Used");
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");
                    obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationSubID().setValue(String.valueOf(drugCounter));
                    obx2Inc +=1;

                }
            }
        }
    }

    private String getDateFormat(String pidFieldValue, String questionDataTypeNND, String questionIdentifierNND, String segmentField) {
        Map<String, String > fields = new HashMap<>();
        fields.put(Constants.HL_SEVEN_SEGMENT_FIELD, pidFieldValue);
        fields.put("mmgVersion", messageType);
        fields.put("inputDataType", questionDataTypeNND);
        fields.put("questionIdentifier", questionIdentifierNND);
        fields.put("hl7Segment", segmentField);
        // datatypeProcessor = new DataTypeProcessor(iDataTypeLookupRepository);
        return dataTypeProcessor.processFields(fields);

    }

    private void mapToQuestionMap(MessageElement messageElement, int counter, ORU_R01_ORDER_OBSERVATION orderObservation) throws DataTypeException {
        //TODO - Implement this method
        String indPartMain = "";
        String indPart1 = "";
        String indPart2 = "";
        String indPart3 = "";
        String indPart4 = "";
        String indPart5 = "";
        String indPart6 = "";

        String questPart1 = "";
        String questPart2 = "";
        String questPart3 = "";
        String questPart4 = "";
        String questPart5 = "";
        String questPart6 = "";

        String quest2Part1 = "";
        String quest2Part2 = "";
        String quest2Part3 = "";
        String quest2Part4 = "";
        String quest2Part5 = "";
        String quest2Part6 = "";

        String otherText = "";
        int obx4Counter = 1;
        int mappedAsOtherInt = 0;
        String unkcode = "";
        String unkObx5 = "";
        String subStringRightInd = "";
        String mappedValue = "";
        String indicatorCdRaw = messageElement.getIndicatorCd();
        String indicatorCode = (indicatorCdRaw != null) ? indicatorCdRaw.trim() : "";
        String mappedIndicatorCode = (indicatorCdRaw != null) ? indicatorCdRaw.trim() : "";
        //we need to find index positions of the characters below

        int startInd = indicatorCode.indexOf("|");
        int checkPoint = indicatorCode.indexOf(":>");
        int splitCounter = indicatorCode.split("\\|").length; // Properly count splits
        int mapToQuestionId = indicatorCode.indexOf("|:");

        if (splitCounter == 3) {
            mappedIndicatorCode = indicatorCode.substring(mapToQuestionId + 2);
            indicatorCode = indicatorCode.substring(0, mapToQuestionId);
        }

        if (checkPoint > 0) {
            int gtIndex = indicatorCode.indexOf(">");
            if (gtIndex > 0) {
                mappedValue = indicatorCode.substring(0, gtIndex);
            }

            if (startInd != -1 && startInd + 1 < indicatorCode.length()) {
                indicatorCode = indicatorCode.substring(startInd + 1);
            }

            int nextPipeIndex = indicatorCode.indexOf("|");
            if (nextPipeIndex != -1) {
                unkObx5 = indicatorCode.substring(0, nextPipeIndex);
                if (nextPipeIndex + 1 < indicatorCode.length()) {
                    subStringRightInd = indicatorCode.substring(nextPipeIndex + 1);
                }
            }
        } else {
            if (startInd != -1 && startInd + 1 < indicatorCode.length()) {
                subStringRightInd = indicatorCode.substring(startInd + 1);
            }
        }

        // For parts related to "^" symbol
        //indPartMain = "^" + indicatorCode.substring(0, startInd) + "^";

        int endInd = indicatorCode.indexOf("|");
        String subStringLeftInd = (startInd != -1) ? indicatorCode.substring(0, startInd) : indicatorCode;
        indPartMain = "^" + subStringLeftInd + "^";

        if (subStringRightInd.contains("^")) {
            int startPartInt1 = subStringRightInd.indexOf("^");
            indPart1 = subStringRightInd.substring(0, startPartInt1);

            String RemainingPart1 = subStringRightInd.substring(startPartInt1 + 1);
            int startPartInt2 = RemainingPart1.indexOf("^");
            indPart2 = RemainingPart1.substring(0, startPartInt2);

            String RemainingPart2 = RemainingPart1.substring(startPartInt2 + 1);
            int startPartInt3 = RemainingPart2.indexOf("^");
            indPart3 = RemainingPart2.substring(0, startPartInt3);

            String RemainingPart3 = RemainingPart2.substring(startPartInt3 + 1);
            int startPartInt4 = RemainingPart3.indexOf("^");
            indPart4 = RemainingPart3.substring(0, startPartInt4);

            String RemainingPart4 = RemainingPart3.substring(startPartInt4 + 1);
            int startPartInt5 = RemainingPart4.indexOf("^");
            indPart5 = RemainingPart4.substring(0, startPartInt5);

            indPart6 = RemainingPart4.substring(startPartInt5 + 1);
        }

        String questionMap = (messageElement.getQuestionMap() != null) ? messageElement.getQuestionMap().trim() : "";
//        int start = questionMap.indexOf("|");
        String subStringRight = "";
//        int end = questionMap.indexOf("|");
        String subStringLeft = "";
        if (questionMap.contains("|")) {
            int index = questionMap.indexOf("|");
            subStringLeft = questionMap.substring(0, index);
            subStringRight = questionMap.substring(index + 1);
        }

        if (subStringLeft.contains("^")) {
            String remainingPart = subStringLeft;
            questPart1 = remainingPart.substring(0, remainingPart.indexOf("^"));
            remainingPart = remainingPart.substring(remainingPart.indexOf("^") + 1);

            questPart2 = remainingPart.substring(0, remainingPart.indexOf("^"));
            remainingPart = remainingPart.substring(remainingPart.indexOf("^") + 1);

            questPart3 = remainingPart.substring(0, remainingPart.indexOf("^"));
            remainingPart = remainingPart.substring(remainingPart.indexOf("^") + 1);

            questPart4 = remainingPart.substring(0, remainingPart.indexOf("^"));
            remainingPart = remainingPart.substring(remainingPart.indexOf("^") + 1);

            questPart5 = remainingPart.substring(0, remainingPart.indexOf("^"));
            questPart6 = remainingPart.substring(remainingPart.indexOf("^") + 1);
        }

        if (subStringRight.contains("^")) {
            String remainingPart = subStringRight;
            quest2Part1 = remainingPart.substring(0, remainingPart.indexOf("^"));
            remainingPart = remainingPart.substring(remainingPart.indexOf("^") + 1);

            quest2Part2 = remainingPart.substring(0, remainingPart.indexOf("^"));
            remainingPart = remainingPart.substring(remainingPart.indexOf("^") + 1);

            quest2Part3 = remainingPart.substring(0, remainingPart.indexOf("^"));
            remainingPart = remainingPart.substring(remainingPart.indexOf("^") + 1);

            quest2Part4 = remainingPart.substring(0, remainingPart.indexOf("^"));
            remainingPart = remainingPart.substring(remainingPart.indexOf("^") + 1);

            quest2Part5 = remainingPart.substring(0, remainingPart.indexOf("^"));
            quest2Part6 = remainingPart.substring(remainingPart.indexOf("^") + 1);
        }

        String dataElementCodedValue = "";
        String questionDataType = "";

        if(messageElement.getDataElement().getCweDataType() != null) {
            dataElementCodedValue = messageElement.getDataElement().getCweDataType().getCweCodedValue();
        }

        if(messageElement.getDataElement().getQuestionDataTypeNND() != null) {
            questionDataType = messageElement.getDataElement().getQuestionDataTypeNND();
        }

        if (!mappedValue.isEmpty() && !mappedValue.equals(dataElementCodedValue)) {
        } else if ("SN_WITH_UNIT".equals(questionDataType)) {
            int checkerNum = 0;

            for (DiscreteRepeat item : discreteRepeatSNTypeArray) {
                if (item.getMappedIndicatorCode().equals(mappedIndicatorCode)) {
                    obx4Counter = item.getObx4counter();
                    checkerNum = 1;
                    break;
                }
            }

            if (checkerNum == 0) {
                DiscreteRepeat newItem = new DiscreteRepeat();
                newItem.setObx4counter(discreteRepeatSNTypeArray.size());
                obx4Counter = newItem.getObx4counter();
                newItem.setCode(questPart1);
                newItem.setMappedIndicatorCode(mappedIndicatorCode);
                newItem.setCounter(obx2Inc);
                discreteRepeatSNTypeArray.add(newItem);
            }

            OBX obx = orderObservation.getOBSERVATION(1).getOBX();
            obx.getSetIDOBX().setValue(String.valueOf(obx2Inc + 1));
            obx.getValueType().setValue("SN");

            CE observationIdentifier = orderObservation.getOBSERVATION(1).getOBX().getObservationIdentifier();
            observationIdentifier.getIdentifier().setValue(questPart1);
            observationIdentifier.getText().setValue(questPart2);
            observationIdentifier.getNameOfCodingSystem().setValue(questPart3);
            observationIdentifier.getAlternateIdentifier().setValue(messageElement.getQuestionIdentifierNND());
            observationIdentifier.getAlternateText().setValue(messageElement.getQuestionLabelNND());
            observationIdentifier.getNameOfAlternateCodingSystem().setValue(questPart6);

            obx.getObservationSubID().setValue(String.valueOf(obx4Counter));

            String value = "^" + messageElement.getDataElement().getSnunitDataType().getNum1();
            ST observationValueStType = (ST) obx.getObservationValue(0).getData();
            observationValueStType.setValue(value);
            obx.getObservationValue(0).setData(observationValueStType);

            obx.getUnits().getIdentifier().setValue(messageElement.getDataElement().getSnunitDataType().getCeCodedValue());
            obx.getUnits().getText().setValue(messageElement.getDataElement().getSnunitDataType().getCeCodedValueDescription());
            obx.getUnits().getNameOfCodingSystem().setValue(messageElement.getDataElement().getSnunitDataType().getCeCodedValueCodingSystem());
            obx.getObservationResultStatus().setValue("F");

            obx2Inc++;
        } else if (messageElement.getQuestionIdentifierNND().contains("_OTH")) {
            int checkerNum = 0;
            String tester = "";

            int obxSize = orderObservation.getOBSERVATION(0).getOBX().numFields();
            // TODO - Check this length
            for (int j = 0; j < obxSize; j++) {
                String originalText = messageElement.getDataElement().getStDataType().getStringData();

                if (originalText != null && !originalText.isEmpty()) {
                    originalText = originalText.replace("\\", "\\E\\");
                    originalText = originalText.replace("|", "\\F\\");
                    originalText = originalText.replace("~", "\\R\\");
                    originalText = originalText.replace("^", "\\S\\");
                    originalText = originalText.replace("&", "\\T\\");
                }

                OBX obx = orderObservation.getOBSERVATION(0).getOBX();

                if (obx.getObservationIdentifier().getIdentifier().getValue().equals(questPart1) &&
                        obx.getObservationValue(0).getData().toString().startsWith("OTH^")) {
                    String updatedValue = obx.getObservationValue(0) + "^^^^^^" + originalText;
                    ST stTempType = (ST) obx.getObservationValue(0).getData();
                    stTempType.setValue(updatedValue);
                    obx.getObservationValue(0).setData(stTempType);
                    tester = "mapped";
                }
            }

            if (tester.isEmpty()) {
                otherText = messageElement.getDataElement().getStDataType().getStringData();

                if (otherText != null && !otherText.isEmpty()) {
                    otherText = otherText.replace("\\", "\\E\\");
                    otherText = otherText.replace("|", "\\F\\");
                    otherText = otherText.replace("~", "\\R\\");
                    otherText = otherText.replace("^", "\\S\\");
                    otherText = otherText.replace("&", "\\T\\");
                }

                boolean found = false;
                for (DiscreteRepeat dr : discreteRepeatArray) {
                    if (dr.getCode().equals(questPart1)) {
                        dr.setOtherText(otherText);
                        found = true;
                    }
                }

                if (discreteRepeatArray.isEmpty()) {
                    DiscreteRepeat discreteRepeat = new DiscreteRepeat();
                    // discreteRepeat.setCounter(1);
                    discreteRepeat.setCode(questPart1);
                    discreteRepeat.setOtherText(otherText);
                    discreteRepeatArray.add(discreteRepeat);
                }
            } else {
                for (DiscreteRepeat dr : discreteRepeatArray) {
                    if (dr.getCode().equals(questPart1)) {
                        dr.setOtherText(otherText);
                        checkerNum = 1;
                    }
                }

                if (checkerNum == 0) {
                    DiscreteRepeat discreteRepeat = new DiscreteRepeat();
                    discreteRepeat.setCounter(1);
                    discreteRepeat.setCode(questPart1);
                    discreteRepeat.setOtherText(otherText);
                    discreteRepeatArray.add(discreteRepeat);
                    mappedAsOtherInt = 1;
                }
            }
        } else if (indPartMain.contains("^" + dataElementCodedValue + "^")
                || (unkObx5 != null && !unkObx5.isEmpty())) {

            int checkerNum = 0;
            String id = messageElement.getQuestionIdentifier();

            if (splitCounter == 3) {
                int checkerNumDetail = 0;
                for (DiscreteRepeat dr : discreteRepeatSNTypeArray) {
                    if (dr.getMappedIndicatorCode().equals(mappedIndicatorCode)) {
                        obx4Counter = dr.getObx4counter();
                        checkerNumDetail = 1;
                    }
                }
                if (checkerNumDetail == 0) {
                    DiscreteRepeat discreteRepeat = new DiscreteRepeat();
                    discreteRepeat.setObx4counter(discreteRepeatSNTypeArray.size());
                    obx4Counter = discreteRepeat.getObx4counter();
                    discreteRepeat.setCode(questPart1);
                    discreteRepeat.setMappedIndicatorCode(mappedIndicatorCode);
                    discreteRepeat.setCounter(obx2Inc);
                    discreteRepeatSNTypeArray.add(discreteRepeat);
                }
            }

            for (DiscreteRepeat dr : discreteRepeatArray) {
                if (dr.getCode().equals(questPart1) && mappedAsOtherInt == 0) {
                    dr.setCounter(dr.getCounter() + 1);
                    checkerNum = 1;
                    otherText = dr.getOtherText();
                    obx4Counter = dr.getCounter();
                }
            }

            if (checkerNum == 0 && mappedAsOtherInt == 0) {
                DiscreteRepeat discreteRepeat = new DiscreteRepeat();
                discreteRepeat.setCounter(1);
                discreteRepeat.setCode(questPart1);
                discreteRepeatArray.add(discreteRepeat);
            }
            
            // Case 2
            orderObservation.getOBSERVATION(1).getOBX().getSetIDOBX().setValue(String.valueOf(counter + 1));
            orderObservation.getOBSERVATION(1).getOBX().getValueType().setValue("CWE");
            
            OBX obx1 = orderObservation.getOBSERVATION(1).getOBX();

            obx1.getObservationIdentifier().getIdentifier().setValue(questPart1);
            obx1.getObservationIdentifier().getText().setValue(questPart2);
            obx1.getObservationIdentifier().getNameOfCodingSystem().setValue(questPart3);
            obx1.getObservationIdentifier().getAlternateIdentifier().setValue(questPart4);
            obx1.getObservationIdentifier().getAlternateText().setValue(questPart5);
            obx1.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue(questPart6);

            if (checkPoint > 0) {
                ST stObservationValue = (ST) obx1.getObservationValue(0).getData();
                stObservationValue.setValue(messageElement.getDataElement().getCweDataType().getCweCodedValue() + "^" +
                        messageElement.getDataElement().getCweDataType().getCweCodedValueDescription() + "^" +
                        messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem());
                obx1.getObservationValue(0).setData(stObservationValue);
            } else {
                ST stObservationValue = (ST) obx1.getObservationValue(0).getData();
                stObservationValue.setValue(quest2Part1 + "^" + quest2Part2 + "^" + quest2Part3);
                obx1.getObservationValue(0).setData(stObservationValue);
            }

            if (messageElement.getQuestionIdentifierNND().equals("OTH") && otherText != null && !otherText.isEmpty()) {
                //TODO - Check value here
                String val = obx1.getObservationValue(0).toString() + "^^^^^^" + otherText;
                ST stObservationValue = (ST) obx1.getObservationValue(0).getData();
                stObservationValue.setValue(val);
                obx1.getObservationValue(0).setData(stObservationValue);
            }

            obx1.getObservationSubID().setValue(String.valueOf(obx4Counter));
            obx1.getObservationResultStatus().setValue("F");

            obx2Inc++;

            orderObservation.getOBSERVATION(1).getOBX().getSetIDOBX().setValue(String.valueOf(obx2Inc + 1));
            orderObservation.getOBSERVATION(1).getOBX().getValueType().setValue("CWE");

            OBX obx2 = orderObservation.getOBSERVATION(1).getOBX();

            obx2.getObservationIdentifier().getIdentifier().setValue(indPart1);
            obx2.getObservationIdentifier().getText().setValue(indPart2);
            obx2.getObservationIdentifier().getNameOfCodingSystem().setValue(indPart3);
            obx2.getObservationIdentifier().getAlternateIdentifier().setValue(indPart4);
            obx2.getObservationIdentifier().getAlternateText().setValue(indPart5);
            obx2.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue(indPart6);

            if (unkObx5 != null && !unkObx5.isEmpty()) {
                ST stObservationValue = (ST) obx2.getObservationValue(0).getData();
                stObservationValue.setValue(unkObx5);
                obx2.getObservationValue(0).setData(stObservationValue);
            } else {
                ST stObservationValue = (ST) obx2.getObservationValue(0).getData();
                stObservationValue.setValue(messageElement.getDataElement().getCweDataType().getCweCodedValue() + "^" +
                        messageElement.getDataElement().getCweDataType().getCweCodedValueDescription() + "^" +
                        messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem());
                obx2.getObservationValue(0).setData(stObservationValue);
            }

            obx2.getObservationSubID().setValue(String.valueOf(obx4Counter));
            obx2.getObservationResultStatus().setValue("F");

            obx2Inc++;
        }
    }

    private static String encodeToBase64(String hl7Message){

        return Base64.getEncoder().encodeToString(hl7Message.getBytes());
    }

    private void mapToNK1Element(MessageElement messageElement, NK1 nk1) throws DataTypeException {
        String hl7Field = messageElement.getHl7SegmentField().trim();
        if (hl7Field.startsWith("NK1-")) {
            String dataElement = hl7Field.substring(4); // Remove "NK1-"
            
            if (dataElement.startsWith("3.")) {
                // Map to CE type for Relationship
                String ceCodedValue = messageElement.getDataElement().getCeDataType().getCeCodedValue().trim();
                String ceCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeCodedValueDescription().trim();
                String ceCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem().trim();
                String ceLocalCodedValue = messageElement.getDataElement().getCeDataType().getCeLocalCodedValue().trim();
                String ceLocalCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription().trim();
                String ceLocalCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem().trim();
                
                nk1.getRelationship().getIdentifier().setValue(ceCodedValue);
                nk1.getRelationship().getText().setValue(ceCodedValueDescription);
                nk1.getRelationship().getNameOfCodingSystem().setValue(ceCodedValueCodingSystem);
                nk1.getRelationship().getAlternateIdentifier().setValue(ceLocalCodedValue);
                nk1.getRelationship().getAlternateText().setValue(ceLocalCodedValueDescription);
                nk1.getRelationship().getNameOfAlternateCodingSystem().setValue(ceLocalCodedValueCodingSystem);
                
            } else if (dataElement.startsWith("4.")) {
                // Map to XAD type for Address
                String dataLocator = dataElement.substring(2);
                mapToXADType(messageElement, dataLocator, nk1.getAddress());
                
            } else if (dataElement.startsWith("14.")) {
                // Map to CE type for MaritalStatus
                String ceCodedValue = messageElement.getDataElement().getCeDataType().getCeCodedValue().trim();
                String ceCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeCodedValueDescription().trim();
                String ceCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem().trim();
                String ceLocalCodedValue = messageElement.getDataElement().getCeDataType().getCeLocalCodedValue().trim();
                String ceLocalCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription().trim();
                String ceLocalCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem().trim();
                
                nk1.getMaritalStatus().getIdentifier().setValue(ceCodedValue);
                nk1.getMaritalStatus().getText().setValue(ceCodedValueDescription);
                nk1.getMaritalStatus().getNameOfCodingSystem().setValue(ceCodedValueCodingSystem);
                nk1.getMaritalStatus().getAlternateIdentifier().setValue(ceLocalCodedValue);
                nk1.getMaritalStatus().getAlternateText().setValue(ceLocalCodedValueDescription);
                nk1.getMaritalStatus().getNameOfAlternateCodingSystem().setValue(ceLocalCodedValueCodingSystem);
                
            } else if (dataElement.startsWith("16.")) {
                // Map to TS type for DateTimeOfBirth
                String dataLocator = dataElement.substring(2);
                mapToTSDayMonthYearType(messageElement, nk1.getDateTimeOfBirth());
                
            } else if (dataElement.startsWith("28.")) {
                // Map to CE type for EthnicGroup
                String dataLocator = dataElement.substring(2);
                String ceCodedValue = messageElement.getDataElement().getCeDataType().getCeCodedValue().trim();
                String ceCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeCodedValueDescription().trim();
                String ceCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem().trim();
                String ceLocalCodedValue = messageElement.getDataElement().getCeDataType().getCeLocalCodedValue().trim();
                String ceLocalCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription().trim();
                String ceLocalCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem().trim();
                
                nk1.getEthnicGroup(0).getIdentifier().setValue(ceCodedValue);
                nk1.getEthnicGroup(0).getText().setValue(ceCodedValueDescription);
                nk1.getEthnicGroup(0).getNameOfCodingSystem().setValue(ceCodedValueCodingSystem);
                nk1.getEthnicGroup(0).getAlternateIdentifier().setValue(ceLocalCodedValue);
                nk1.getEthnicGroup(0).getAlternateText().setValue(ceLocalCodedValueDescription);
                nk1.getEthnicGroup(0).getNameOfAlternateCodingSystem().setValue(ceLocalCodedValueCodingSystem);
                
            } else if (dataElement.startsWith("35.")) {
                // Map to CE type for Race
                String dataLocator = dataElement.substring(2);
                String ceCodedValue = messageElement.getDataElement().getCeDataType().getCeCodedValue().trim();
                String ceCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeCodedValueDescription().trim();
                String ceCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem().trim();
                String ceLocalCodedValue = messageElement.getDataElement().getCeDataType().getCeLocalCodedValue().trim();
                String ceLocalCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription().trim();
                String ceLocalCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem().trim();
                
                nk1.getRace(nk1RaceInc).getIdentifier().setValue(ceCodedValue);
                nk1.getRace(nk1RaceInc).getText().setValue(ceCodedValueDescription);
                nk1.getRace(nk1RaceInc).getNameOfCodingSystem().setValue(ceCodedValueCodingSystem);
                nk1.getRace(nk1RaceInc).getAlternateIdentifier().setValue(ceLocalCodedValue);
                nk1.getRace(nk1RaceInc).getAlternateText().setValue(ceLocalCodedValueDescription);
                nk1.getRace(nk1RaceInc).getNameOfAlternateCodingSystem().setValue(ceLocalCodedValueCodingSystem);
                
                nk1RaceInc++;
            }
        }
    }

    private void mapToXADType(MessageElement messageElement, String dataLocator, XAD[] xad) throws DataTypeException {
        // Implementation for mapping to XAD type
        // This would handle address components like street, city, state, etc.
        // Similar to how other data types are mapped
    }

    private void mapToTSDayMonthYearType(MessageElement messageElement, TS ts) throws DataTypeException {
        // Implementation for mapping to TS type with day/month/year precision
        // This would handle date/time components
        // Similar to how other data types are mapped
    }
}
