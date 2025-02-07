package gov.cdc.notificationprocessor.util;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.MSH;

import gov.cdc.notificationprocessor.constants.Constants;
import gov.cdc.notificationprocessor.consumer.KafkaConsumerService;
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
    //private OBX obx;
    //private PID pid;
    private ORU_R01 oruMessage;

    public Map<String,String> segmentFieldsWithValues = new HashMap<>();

    @Override
    public void startDocument() {
        logger.info("Starting the parsing process");
        oruMessage = new ORU_R01();
        msh = oruMessage.getMSH();
        try {
            msh.getFieldSeparator().setValue(Constants.FIELD_SEPARATOR);
            msh.getEncodingCharacters().setValue(Constants.ENCODING_CHARACTERS);
            msh.getMessageType().getMessageCode().setValue(Constants.MESSAGE_CODE);
            msh.getMessageType().getTriggerEvent().setValue(Constants.MESSAGE_TRIGGER_EVENT);
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
                case Constants.STRING_DATATYPE, Constants.ID_CODED_VALUE:
                    String dataType = currentField.toString().trim();
                    segmentFieldsWithValues.put(Constants.HL_SEVEN_SEGMENT_FIELD_VALUE, dataType);
                    break;

                case Constants.QUESTION_DATA_TYPE_NND:
                    String questionIdentifierNND = currentField.toString().trim();
                    segmentFieldsWithValues.put(Constants.QUESTION_DATA_TYPE_NND, questionIdentifierNND);
                    break;
            }
        }
        logger.info("MSH message is {} ", oruMessage.getMessage());
    }

    /**
     * sets the value for all MSH fields found in the xml
     * @param mshSegmentFields object that holds fields for processing MSH fields
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

                    case "MSH-21.2" -> nameSpaceIDGroup1 = mshFieldValue;//msh.getMessageProfileIdentifier(0).getNamespaceID().setValue(mshFieldValue);
                    case "MSH-21.3" -> universalIDGroup1 = mshFieldValue;//msh.getMessageProfileIdentifier(0).getUniversalID().setValue(mshFieldValue);
                    case "MSH-21.4" -> universalIDTypeGroup1 = mshFieldValue;//msh.getMessageProfileIdentifier(0).getUniversalIDType().setValue(mshFieldValue);
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
}
