package gov.cdc.notificationprocessor.util;

import ca.uhn.hl7v2.model.DataTypeException;

import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.MSH;

import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.PID;

import gov.cdc.notificationprocessor.constants.Constants;

import gov.cdc.notificationprocessor.model.MessageElement;
import gov.cdc.notificationprocessor.model.NBSNNDIntermediaryMessage;

import gov.cdc.notificationprocessor.service.DateTypeProcessing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
    Boolean genericMMG = false;
    String nameSpaceIDGroup2 = "";
    String universalIDGroup2 = "";
    String universalIDTypeGroup2 = "";

    private String stateLocalID;
    private Integer raceIndex = 0;
    private Integer cityIndex = 0;
    private Integer stateIndex = 0;
    private Integer zipcodeIndex = 0;
    private Integer countryIndex = 0;
    private Integer addressTypeIndex = 0;
    private Integer citizenshipTypeIndex = 0;
    private Integer identityReliabilityCodeIndex = 0;

    private static final Logger logger = LoggerFactory.getLogger(HL7MessageBuilder.class);

    public void handler() throws DataTypeException {
        ORU_R01 oruMessage = new ORU_R01();
        MSH msh = oruMessage.getMSH();
        PID pid = oruMessage.getPATIENT_RESULT().getPATIENT().getPID();
        OBR obr = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR();
        OBX obx = null; //TODO

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
            }else if (segmentField.startsWith("PID")){
                processPIDFields(messageElement, pid);
            }

        }
        logger.info("Final message: {} ", oruMessage.getMessage());
    }

    /**
     * sets the value for all MSH fields found in the xml
     */
    private void processMSHFields(MessageElement messageElement, MSH msh) throws DataTypeException {
        logger.info("Begining of the message {} ", messageElement.getHl7SegmentField());
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
}
