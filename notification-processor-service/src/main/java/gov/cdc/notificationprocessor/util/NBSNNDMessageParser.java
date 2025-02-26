package gov.cdc.notificationprocessor.util;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.MSH;

import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.PID;
import gov.cdc.notificationprocessor.constants.Constants;
import gov.cdc.notificationprocessor.consumer.KafkaConsumerService;
import gov.cdc.notificationprocessor.service.DateTypeProcessing;
import gov.cdc.notificationprocessor.service.OBR31SegmentProcessing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class NBSNNDMessageParser extends DefaultHandler {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private String currentElement = "";
    private Boolean isSingleProfile = true;
    private Boolean isWithinMessageElement = false;
    private Boolean genericMMG = false;
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

    private final StringBuilder currentField = new StringBuilder();

    private MSH msh;
    private String entityIdentifierGroup1; // msh21.1
    private String nameSpaceIDGroup1; //msh21.2
    private String universalIDGroup1; //msh21.3
    private String universalIDTypeGroup1; //msh21.4

    private String entityIdentifierGroup2; // msh21.1
    private String nameSpaceIDGroup2; //msh21.2
    private String universalIDGroup2; //msh21.3
    private String universalIDTypeGroup2; //msh21.4
    private String nndMessageVersion;
    private String messageType;
    private String stateLocalID;
    private Integer raceIndex;
    private Integer cityIndex;
    private Integer stateIndex;
    private Integer zipcodeIndex;
    private Integer countryIndex;
    private Integer addressTypeIndex;
    private Integer citizenshipTypeIndex;
    private Integer identityReliabilityCodeIndex;



    //private OBX obx;
    private PID pid;
    private OBR obr;
    private ORU_R01 oruMessage;

    public Map<String,String> segmentFieldsWithValues = new HashMap<>();

    @Override
    public void startDocument() {
        logger.info("Starting the parsing process");
        oruMessage = new ORU_R01();
        msh = oruMessage.getMSH();
        pid = oruMessage.getPATIENT_RESULT().getPATIENT().getPID();
        obr = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR();
        stateLocalID = "";
        raceIndex = 0;
        cityIndex = 0;
        stateIndex = 0;
        zipcodeIndex = 0;
        countryIndex = 0;
        addressTypeIndex = 0;
        citizenshipTypeIndex = 0;
        identityReliabilityCodeIndex = 0;

        try {
            // set static fields
            msh.getFieldSeparator().setValue(Constants.FIELD_SEPARATOR);
            msh.getEncodingCharacters().setValue(Constants.ENCODING_CHARACTERS);
            msh.getMessageType().getMessageCode().setValue(Constants.MESSAGE_CODE);
            msh.getMessageType().getTriggerEvent().setValue(Constants.MESSAGE_TRIGGER_EVENT);
            pid.getSetIDPID().setValue("1");
            pid.getPatientName(0).getNameTypeCode().setValue("S");
            obr.getObr1_SetIDOBR().setValue("1");
        } catch (DataTypeException e) {
            throw new RuntimeException(e);
        }


    }
    @Override
    public void endDocument() {
        logger.info("Completed the parsing process");
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        if (qName.equals(Constants.MESSAGE_ELEMENT)) {
            isWithinMessageElement = true;
            currentElement = qName;
            currentField.setLength(0);
        }else if (isWithinMessageElement) {
            currentElement = qName;
            currentField.setLength(0);
        }

    }

    @Override
    public void characters(char[] ch, int start, int length) {
        if (!currentElement.isEmpty()){
            currentField.append(ch, start, length);
        }

    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if (qName.equals(Constants.MESSAGE_ELEMENT)) {
            logger.info("Completed <MessageElement> processing for {}", currentElement);
            isWithinMessageElement = false;
            currentField.setLength(0);

            if (segmentFieldsWithValues.get(Constants.HL_SEVEN_SEGMENT_FIELD).startsWith("MSH")) {
                try {
                    processMSHFields(segmentFieldsWithValues);
                } catch (DataTypeException e) {
                    throw new RuntimeException(e);
                }

            }else if (segmentFieldsWithValues.get(Constants.HL_SEVEN_SEGMENT_FIELD).startsWith("PID")) {
                try {
                    processPIDFields(segmentFieldsWithValues);
                }catch (DataTypeException e) {
                    throw new RuntimeException(e);
                }
            }else if (segmentFieldsWithValues.get(Constants.HL_SEVEN_SEGMENT_FIELD).startsWith("OBR")) {
                try {
                    processOBRFields(segmentFieldsWithValues);
                }catch (DataTypeException e) {
                    throw new RuntimeException(e);
                }
            }
        }else if (isWithinMessageElement) {
            switch (currentElement) {
                case Constants.HL_SEVEN_SEGMENT_FIELD:
                    String segmentField = currentField.toString().trim();
                    segmentFieldsWithValues.put(Constants.HL_SEVEN_SEGMENT_FIELD, segmentField);
                    break;
                case Constants.ORDER_GROUP_ID:
                    String orderGroupID = currentField.toString().trim();
                    segmentFieldsWithValues.put(Constants.ORDER_GROUP_ID, orderGroupID);
                    break;
                case Constants.STRING_DATATYPE, Constants.ID_CODED_VALUE, Constants.TIME_DATA_TYPE:
                    String dataType = currentField.toString().trim();
                    segmentFieldsWithValues.put(Constants.HL_SEVEN_SEGMENT_FIELD_VALUE, dataType);
                    break;
                case Constants.QUESTION_DATA_TYPE_NND:
                    String questionDatatypeNND = currentField.toString().trim();
                    segmentFieldsWithValues.put(Constants.QUESTION_DATA_TYPE_NND, questionDatatypeNND);
                    break;
                case Constants.QUESTION_IDENTIFIER_NND:
                    String questionIdentifierNND = currentField.toString().trim();
                    segmentFieldsWithValues.put(Constants.QUESTION_IDENTIFIER_NND, questionIdentifierNND);
                    break;
                case Constants.CE_CONDITION_CODE:
                    String conditionCode = currentField.toString().trim();
                    segmentFieldsWithValues.put(Constants.CE_CONDITION_CODE, conditionCode);
                    break;

            }
        }
        logger.info("MSH message is {} ", oruMessage.getMessage());
    }

    /**
     * sets the value for all MSH fields found in the xml
     * @param mshSegmentFields object that holds fields necessary for processing MSH fields
     */
    private void processMSHFields(Map<String, String> mshSegmentFields) throws DataTypeException {
        String mshField = mshSegmentFields.get(Constants.HL_SEVEN_SEGMENT_FIELD);
        String mshFieldValue = mshSegmentFields.get(Constants.HL_SEVEN_SEGMENT_FIELD_VALUE);
        if (mshField.startsWith("MSH-3.1")){
            msh.getSendingApplication().getNamespaceID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-3.2")){
            msh.getSendingApplication().getUniversalID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-3.3")){
            msh.getSendingApplication().getUniversalIDType().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-4.1")){
            msh.getSendingFacility().getNamespaceID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-4.2")){
            msh.getSendingFacility().getUniversalID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-4.3")){
            msh.getSendingFacility().getUniversalIDType().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-5.1")){
            msh.getReceivingApplication().getNamespaceID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-5.2")){
            msh.getReceivingApplication().getUniversalID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-5.3")){
            msh.getReceivingApplication().getUniversalIDType().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-6.1")){
            msh.getReceivingFacility().getNamespaceID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-6.2")){
            msh.getReceivingFacility().getUniversalID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-6.3")){
            msh.getReceivingApplication().getUniversalIDType().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-9.3")){
            msh.getMessageType().getMessageStructure().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-10.0")){
            msh.getMessageControlID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-11.1")){
            msh.getProcessingID().getProcessingID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-12.1")){
            msh.getVersionID().getVersionID().setValue(mshFieldValue);
        }else if (mshField.startsWith("MSH-21")){
            if (Objects.equals(segmentFieldsWithValues.get(Constants.ORDER_GROUP_ID), "1")){

                switch (mshField) {
                    case "MSH-21.0" -> {
                        isSingleProfile = false;
                        entityIdentifierGroup1 = mshFieldValue;
                    }
                    case "MSH-21.1"-> {
                        nndMessageVersion = mshFieldValue;
                        nameSpaceIDGroup1 = mshFieldValue;
                        msh.getMessageProfileIdentifier(0).getEntityIdentifier().setValue(nndMessageVersion);
                    }

                    case "MSH-21.2" -> nameSpaceIDGroup1 = mshFieldValue;
                    case "MSH-21.3" -> universalIDGroup1 = mshFieldValue;
                    case "MSH-21.4" -> universalIDTypeGroup1 = mshFieldValue;
                }
            }else if (Objects.equals(segmentFieldsWithValues.get(Constants.ORDER_GROUP_ID), "2")){
                switch (mshField) {
                    case "MSH-21.0" ->{
                        messageType = mshFieldValue;
                        entityIdentifierGroup2 = mshFieldValue;
                        if (entityIdentifierGroup2.equals(Constants.GENERIC_MMG_VERSION)){
                            genericMMG = true;
                        }
                    }
                    case "MSH-21.2" -> nameSpaceIDGroup2 = mshFieldValue;
                    case "MSH-21.3" -> universalIDGroup2 = mshFieldValue;
                    case "MSH-21.4" -> universalIDTypeGroup2 = mshFieldValue;
                }
            }

            logger.info("MSH-21 message is {} ", segmentFieldsWithValues);

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

    private void processPIDFields(Map<String, String> pidSegmentFields) throws DataTypeException {
        String pidField = pidSegmentFields.get(Constants.HL_SEVEN_SEGMENT_FIELD);
        String pidFieldValue = pidSegmentFields.get(Constants.HL_SEVEN_SEGMENT_FIELD_VALUE);
        String questionDataTypeNND = pidSegmentFields.get(Constants.QUESTION_DATA_TYPE_NND);
        String questionIdentifierNND = pidSegmentFields.get(Constants.QUESTION_IDENTIFIER_NND);
        if (pidField.startsWith("PID-3.1")){
            pid.getPatientIdentifierList(0).getIDNumber().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-3.4.1")){
            pid.getPatientIdentifierList(0).getAssigningAuthority().getNamespaceID().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-3.4.2")){
            pid.getPatientIdentifierList(0).getAssigningAuthority().getUniversalID().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-3.4.3")){
            pid.getPatientIdentifierList(0).getAssigningAuthority().getUniversalIDType().setValue(pidFieldValue);
        }else if (pidField.startsWith("PID-7.0")){
            String dateFormat = getDateFormat(pidFieldValue, questionDataTypeNND, questionIdentifierNND,"PID-7");
            logger.info("dateFormat for pid-7 {}", dateFormat);
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



    private void processOBRFields(Map<String, String> obrSegmentFields) throws DataTypeException {
        String obrField = obrSegmentFields.get(Constants.HL_SEVEN_SEGMENT_FIELD);
        String obrFieldValue = obrSegmentFields.get(Constants.HL_SEVEN_SEGMENT_FIELD_VALUE);
        String orderGroupID = obrSegmentFields.get(Constants.ORDER_GROUP_ID);
        String questionDataTypeNND = obrSegmentFields.get(Constants.QUESTION_DATA_TYPE_NND);;
        String questionIdentifierNND = obrSegmentFields.get(Constants.QUESTION_IDENTIFIER_NND);
        String conditionCode = obrSegmentFields.get(Constants.CE_CONDITION_CODE);
        if (obrField.startsWith("OBR-3.1")){
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
            String dateFormat = getDateFormat(obrFieldValue, questionDataTypeNND, questionIdentifierNND,"OBR-7.0");
            obr.getObr7_ObservationDateTime().getTime().setValue(dateFormat);
        }else if (obrField.startsWith("OBR-22.0")){
            String dateFormat = getDateFormat(obrFieldValue, questionDataTypeNND, questionIdentifierNND,"OBR-22.0");
            obr.getObr22_ResultsRptStatusChngDateTime().getTime().setValue(dateFormat);
        }else if (obrField.startsWith("OBR-25.0")){
            obr.getObr25_ResultStatus().setValue(obrFieldValue);
        }else if (obrField.startsWith("OBR-31.0")){
            //TODO - custom function is needed, below just placeholder
            Map<String,String> fields = new HashMap<>();
            fields.put("conditionCode", conditionCode);
            fields.put("status_code", "ACTIVE");
            fields.put("message_profile_id", messageType);
            OBR31SegmentProcessing segment = new OBR31SegmentProcessing();
            segment.process(fields);
            obr.getObr31_ReasonForStudy(0).getText().setValue(obrFieldValue);
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
