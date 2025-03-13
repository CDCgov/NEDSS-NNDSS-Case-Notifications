package gov.cdc.notificationprocessor.util;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;

import ca.uhn.hl7v2.model.v25.datatype.*;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.*;

import com.google.gson.Gson;
import gov.cdc.notificationprocessor.constants.Constants;

import gov.cdc.notificationprocessor.model.MessageElement;
import gov.cdc.notificationprocessor.model.NBSNNDIntermediaryMessage;

import gov.cdc.notificationprocessor.service.DateTypeProcessing;
import gov.cdc.notificationprocessor.service.OBR31SegmentProcessing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HL7MessageBuilder{
    NBSNNDIntermediaryMessage nbsnndIntermediaryMessage;

    public HL7MessageBuilder(NBSNNDIntermediaryMessage nbsnndIntermediaryMessage) {
        this.nbsnndIntermediaryMessage = nbsnndIntermediaryMessage;
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
    Boolean genericMMG = false;
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

    private static final Logger logger = LoggerFactory.getLogger(HL7MessageBuilder.class);

    public void handler() throws HL7Exception {
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
            }else if (segmentField.startsWith("OBX")) {
                processOBXFields(messageElement,obx);
            }

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
            }else if (messageElement.getQuestionIdentifierNND().trim().equals("223366009") && genericMMG){
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
            /* TO BE DONE
            }else if(StrFind(in.MessageElement[i].indicatorCd.#PCDATA, "ParentRepeatBlock")>-1){
			MapToDynamincParentRptToRpt(in.MessageElement[i], obx2Inc,messageType, out.PATIENT_RESULT.ORDER_OBSERVATION[0]);
	        }else if(StrFind(in.MessageElement[i].indicatorCd.#PCDATA, "DiscAsRepeat")>-1){
			MapToDynamicRootlDiscToRepeat(in.MessageElement[i], obx2Inc, out.PATIENT_RESULT.ORDER_OBSERVATION[0]);
	   }else if(StrFind(in.MessageElement[i].indicatorCd.#PCDATA, "DiscCdToMultiOBS")>-1){
			MapToDisRepeat(in.MessageElement[i], obx2Inc, out.PATIENT_RESULT.ORDER_OBSERVATION[0]);
	   }else if(StrFind(in.MessageElement[i].indicatorCd.#PCDATA, "RepeatToMultiNND")>-1){
			MapToRepeatToMultiNND(in.MessageElement[i], obx2Inc, out.PATIENT_RESULT.ORDER_OBSERVATION[0]);
	   }else if ((in.MessageElement[i].hl7SegmentField.#PCDATA == "OBX-3.0" || in.MessageElement[i].hl7SegmentField.#PCDATA == "OBX-5.9" )&&  StrFind(in.MessageElement[i].questionMap.#PCDATA, "|") > 0){
				MapToQuestionMap(in.MessageElement[i], obx2Inc, out.PATIENT_RESULT.ORDER_OBSERVATION[0]);
             */
        }
        logger.info("Final message: {} ", oruMessage.getMessage());
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
            msh.getReceivingApplication().getUniversalIDType().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-9.3")){
            mshFieldValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            msh.getMessageType().getMessageStructure().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-10.0")){
            mshFieldValue = messageElement.getDataElement().getStDataType().getStringData().trim();
            msh.getMessageControlID().setValue(mshFieldValue);
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
                        msh.getMessageProfileIdentifier(0).getNamespaceID().setValue(universalID);
                    }
                    case "MSH-21.4" -> {
                        String universalIDType = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
                        msh.getMessageProfileIdentifier(0).getNamespaceID().setValue(universalIDType);
                    }
                }
            }else if (Objects.equals(messageElement.getOrderGroupId(), "2")){
                switch (mshField) {
                    case "MSH-21.0" ->{
                        messageType = messageElement.getDataElement().getStDataType().getStringData().trim();
                        entityIdentifierGroup2 = messageElement.getDataElement().getStDataType().getStringData().trim();
                        if (entityIdentifierGroup2.equals(Constants.GENERIC_MMG_VERSION)){
                            genericMMG = true;
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
            // build map with input parameters needed for matching
            Map<String,String> fields = new HashMap<>();
            fields.put("conditionCode", conditionCode);
            fields.put("status_code", "ACTIVE");
            fields.put("message_profile_id", messageType);
            // instantiate class to process OBR-31 field
            OBR31SegmentProcessing segment = new OBR31SegmentProcessing();
            // result holds string to be deserialized
            String result = segment.process(fields);
            Gson gson = new Gson();
            MappedFields mappedServiceActionCondition = gson.fromJson(result, MappedFields.class);

            //process the results and update OBR-31 field
            if (Objects.equals(mappedServiceActionCondition.mappedService, "") || Objects.equals(mappedServiceActionCondition.mappedAction, "")){
                obr.getObr31_ReasonForStudy(0).getIdentifier().setValue(conditionCode);
            }else if(Objects.equals(mappedServiceActionCondition.mappedConditionCode, "")){
                obr.getObr31_ReasonForStudy(0).getIdentifier().setValue(mappedServiceActionCondition.mappedConditionCode);
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
        if ((obxField.equals("OBX-3.0") || obxField.equals("OBX-5.9"))&& messageElement.getQuestionMap()!=null&& messageElement.getQuestionMap().trim().equals("|")){
            mapToQuestionMap(messageElement, obx2Inc);
        }else if (obxField.equals("OBX-3.0")){
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
                        if (messageElement.getDataElement().getTsDataType().getYear()!=null){
                            year = messageElement.getDataElement().getTsDataType().getYear().trim();
                        }
                        String month = String.valueOf(messageElement.getDataElement().getTsDataType().getTime().getMonth());
                        String day = String.valueOf(messageElement.getDataElement().getTsDataType().getTime().getDay());
                        newDate = year+month+day;
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
                    if (questionIdentifierNND.equals("77999-1") && genericMMG) {
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
                    DT dtDataType = (DT) obx.getOBSERVATION(obxOrderGroupID).getOBX().getObservationValue(obx5ValueInc).getData();
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
                        dtDataType.setYearMonthDayPrecision(year, month, day);
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
        DateTypeProcessing dateTypeProcessingService = new DateTypeProcessing();
        return dateTypeProcessingService.process(fields);

    }
    public static class MappedFields {
        public String mappedService;
        public String mappedAction;
        public String mappedConditionCode;
    }

    private void mapToQuestionMap(MessageElement messageElement, int obx2Inc){
        //TODO
        String indPartMain="";
        String indPart1="";
        String indPart2="";
        String indPart3="";
        String indPart4="";
        String indPart5="";
        String indPart6="";

        String questPart1="";
        String questPart2="";
        String questPart3="";
        String questPart4="";
        String questPart5="";
        String questPart6="";

        String quest2Part1="";
        String quest2Part2="";
        String quest2Part3="";
        String quest2Part4="";
        String quest2Part5="";
        String quest2Part6="";

        String otherText="";
        int obx4Counter=1;
        int mappedAsOtherInt=0;
        String unkcode="";
        String unkObx5="";
        String subStringRightInd  = "";
        String mappedValue ="";
        String indicatorCode = messageElement.getIndicatorCd().trim(); //in.indicatorCd.#PCDATA;
        String mappedIndicatorCode = messageElement.getIndicatorCd().trim();
        //we need to find index positions of the characters below

        int startInd = indicatorCode.indexOf("|");//(indicatorCode, "|");
        int checkPoint = indicatorCode.indexOf(":>");
        int splitCounter= indicatorCode.indexOf("|");
        int mapToQuestionId =indicatorCode.indexOf("|:");

        if (splitCounter==3){
            //extract substring after-> |:
            mappedIndicatorCode = indicatorCode.substring(mapToQuestionId+2);
            //extract after startIndex
            indicatorCode = indicatorCode.substring(startInd+1);

        }

        if (checkPoint >0){
            // extract string before >
            mappedValue = indicatorCode.substring(0,indicatorCode.indexOf(">"));
            //extract string after startIndex
            indicatorCode = indicatorCode.substring(0,indicatorCode.indexOf("|"));
            //extract part before the | character
            unkObx5 = indicatorCode.substring(0,indicatorCode.indexOf("|"));

            subStringRightInd = indicatorCode.substring(indicatorCode.indexOf("|")+1);
        }else{
            subStringRightInd  = indicatorCode.substring(startInd+1);
        }

        // For parts related to "^" symbol
        indPartMain = "^" + indicatorCode.substring(0, startInd) + "^";

        if (subStringRightInd.contains("^")) {
            String remainingPart = subStringRightInd;
            indPart1 = remainingPart.substring(0, remainingPart.indexOf("^"));
            remainingPart = remainingPart.substring(remainingPart.indexOf("^") + 1);

            indPart2 = remainingPart.substring(0, remainingPart.indexOf("^"));
            remainingPart = remainingPart.substring(remainingPart.indexOf("^") + 1);

            indPart3 = remainingPart.substring(0, remainingPart.indexOf("^"));
            remainingPart = remainingPart.substring(remainingPart.indexOf("^") + 1);

            indPart4 = remainingPart.substring(0, remainingPart.indexOf("^"));
            remainingPart = remainingPart.substring(remainingPart.indexOf("^") + 1);

            indPart5 = remainingPart.substring(0, remainingPart.indexOf("^"));
            indPart6 = remainingPart.substring(remainingPart.indexOf("^") + 1);
        }

        String questionMap = messageElement.getQuestionMap().trim();
        int start = questionMap.indexOf("|");
        String subStringRight = questionMap.substring(start + 1);
        int end = questionMap.indexOf("|");
        String subStringLeft = questionMap.substring(0, start);

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

    }
}
