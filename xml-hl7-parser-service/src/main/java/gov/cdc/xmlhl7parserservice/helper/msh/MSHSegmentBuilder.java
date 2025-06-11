package gov.cdc.xmlhl7parserservice.helper.msh;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import gov.cdc.xmlhl7parserservice.helper.MessageState;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Component
public class MSHSegmentBuilder {
    private final MessageState messageState;

    public MSHSegmentBuilder(MessageState messageState) {
        this.messageState = messageState;
    }
    public static final String GENERIC_MMG_VERSION = "Generic_MMG_V2.0";


    public void processMSHFields(MessageElement messageElement, MSH msh) throws DataTypeException {
        String mshField = messageElement.getHl7SegmentField().trim();
        String mshFieldValue = "";

        if (mshField.startsWith("MSH-3.1")) {
            mshFieldValue = messageElement.getDataElement().getIsDataType().getIsCodedValue().trim();
            msh.getSendingApplication().getNamespaceID().setValue(mshFieldValue);
        } else if (mshField.startsWith("MSH-3.2")) {
            mshFieldValue = messageElement.getDataElement().getStDataType().getStringData().trim();
            msh.getSendingApplication().getUniversalID().setValue(mshFieldValue);
        } else if (mshField.startsWith("MSH-3.3")) {
            mshFieldValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            msh.getSendingApplication().getUniversalIDType().setValue(mshFieldValue);
        } else if (mshField.startsWith("MSH-4.1")) {
            mshFieldValue = messageElement.getDataElement().getIsDataType().getIsCodedValue().trim();
            msh.getSendingFacility().getNamespaceID().setValue(mshFieldValue);
        } else if (mshField.startsWith("MSH-4.2")) {
            mshFieldValue = messageElement.getDataElement().getStDataType().getStringData().trim();
            msh.getSendingFacility().getUniversalID().setValue(mshFieldValue);
        } else if (mshField.startsWith("MSH-4.3")) {
            mshFieldValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            msh.getSendingFacility().getUniversalIDType().setValue(mshFieldValue);
        } else if (mshField.startsWith("MSH-5.1")) {
            mshFieldValue = messageElement.getDataElement().getIsDataType().getIsCodedValue().trim();
            msh.getReceivingApplication().getNamespaceID().setValue(mshFieldValue);
        } else if (mshField.startsWith("MSH-5.2")) {
            mshFieldValue = messageElement.getDataElement().getStDataType().getStringData().trim();
            msh.getReceivingApplication().getUniversalID().setValue(mshFieldValue);
        } else if (mshField.startsWith("MSH-5.3")) {
            mshFieldValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            msh.getReceivingApplication().getUniversalIDType().setValue(mshFieldValue);
        } else if (mshField.startsWith("MSH-6.1")) {
            mshFieldValue = messageElement.getDataElement().getIsDataType().getIsCodedValue().trim();
            msh.getReceivingFacility().getNamespaceID().setValue(mshFieldValue);
        } else if (mshField.startsWith("MSH-6.2")) {
            mshFieldValue = messageElement.getDataElement().getStDataType().getStringData().trim();
            msh.getReceivingFacility().getUniversalID().setValue(mshFieldValue);
        } else if (mshField.startsWith("MSH-6.3")) {
            mshFieldValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            msh.getReceivingFacility().getUniversalIDType().setValue(mshFieldValue);
        } else if (mshField.startsWith("MSH-9.3")) {
            mshFieldValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            msh.getMessageType().getMsg1_MessageCode().setValue(mshFieldValue.split("_")[0]);
            msh.getMessageType().getMsg2_TriggerEvent().setValue(mshFieldValue.split("_")[1]);
            msh.getMessageType().getMsg3_MessageStructure().setValue(mshFieldValue);
        } else if (mshField.startsWith("MSH-10.0")) {
            mshFieldValue = messageElement.getDataElement().getStDataType().getStringData().trim();
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");
            String currentTime = now.format(formatter);
            msh.getMessageControlID().setValue(mshFieldValue + currentTime);
        } else if (mshField.startsWith("MSH-11.1")) {
            mshFieldValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            msh.getProcessingID().getProcessingID().setValue(mshFieldValue);
        } else if (mshField.startsWith("MSH-12.1")) {
            mshFieldValue = messageElement.getDataElement().getIdDataType().getIdCodedValue().trim();
            msh.getVersionID().getVersionID().setValue(mshFieldValue);
        } else if (mshField.startsWith("MSH-21")) {
            processMSH21Fields(messageElement, msh);
        }

        // Set message date/time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss.SSS");
        String currentTime = now.format(formatter);
        msh.getDateTimeOfMessage().getTime().setValue(currentTime);
    }

    private void processMSH21Fields(MessageElement messageElement, MSH msh) throws DataTypeException {
        String mshField = messageElement.getHl7SegmentField().trim();
        
        if (Objects.equals(messageElement.getOrderGroupId(), "1")) {
            switch (mshField) {
                case "MSH-21.0" -> {
                    messageState.setIsSingleProfile(false);
                    messageState.setEntityIdentifierGroup1(messageElement.getDataElement().getStDataType().getStringData().trim());
                }
                case "MSH-21.1" -> {
                    messageState.setNndMessageVersion(messageElement.getDataElement().getStDataType().getStringData().trim());
                    msh.getMessageProfileIdentifier(0).getEntityIdentifier().setValue(messageElement.getDataElement().getStDataType().getStringData());
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
        } else if (Objects.equals(messageElement.getOrderGroupId(), "2")) {
            switch (mshField) {
                case "MSH-21.0" -> {
                    messageState.setMessageType(messageElement.getDataElement().getStDataType().getStringData().trim());
                    messageState.setEntityIdentifierGroup2(messageElement.getDataElement().getStDataType().getStringData().trim());
                    if (messageState.getEntityIdentifierGroup2().equals(GENERIC_MMG_VERSION)) {
                        messageState.setGenericMMGv20(true);
                    }
                }
                case "MSH-21.2" -> messageState.setNameSpaceIDGroup2(messageElement.getDataElement().getIsDataType().getIsCodedValue().trim());
                case "MSH-21.3" -> messageState.setUniversalIDGroup2(messageElement.getDataElement().getStDataType().getStringData().trim());
                case "MSH-21.4" -> messageState.setUniversalIDTypeGroup2(messageElement.getDataElement().getIdDataType().getIdCodedValue().trim());
            }
        }
    }

    public void setSingleProfileMSH21(MSH msh) throws DataTypeException {
        msh.getMessageProfileIdentifier(1).getEntityIdentifier().setValue(messageState.getEntityIdentifierGroup2());
        msh.getMessageProfileIdentifier(1).getNamespaceID().setValue(messageState.getNameSpaceIDGroup2());
        msh.getMessageProfileIdentifier(1).getUniversalID().setValue(messageState.getUniversalIDGroup2());
        msh.getMessageProfileIdentifier(1).getUniversalIDType().setValue(messageState.getUniversalIDTypeGroup2());
    }

    public void setMultiProfileMSH21(MSH msh) throws DataTypeException {
        msh.getMessageProfileIdentifier(1).getEntityIdentifier().setValue(messageState.getEntityIdentifierGroup1());
        msh.getMessageProfileIdentifier(1).getNamespaceID().setValue(messageState.getNameSpaceIDGroup2());
        msh.getMessageProfileIdentifier(1).getUniversalID().setValue(messageState.getUniversalIDGroup2());
        msh.getMessageProfileIdentifier(1).getUniversalIDType().setValue(messageState.getUniversalIDTypeGroup2());

        msh.getMessageProfileIdentifier(2).getEntityIdentifier().setValue(messageState.getEntityIdentifierGroup2());
        msh.getMessageProfileIdentifier(2).getNamespaceID().setValue(messageState.getNameSpaceIDGroup2());
        msh.getMessageProfileIdentifier(2).getUniversalID().setValue(messageState.getUniversalIDGroup2());
        msh.getMessageProfileIdentifier(2).getUniversalIDType().setValue(messageState.getUniversalIDTypeGroup2());
    }
} 