package gov.cdc.xmlhl7parserservice.helper.pid;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.segment.PID;
import gov.cdc.xmlhl7parserservice.helper.MessageState;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import gov.cdc.xmlhl7parserservice.util.HL7DateFormatUtil;
import org.springframework.stereotype.Component;

@Component
public class PIDSegmentBuilder {
    private final MessageState messageState;
    private final HL7DateFormatUtil dateFormatUtil;

    public PIDSegmentBuilder(MessageState messageState, HL7DateFormatUtil dateFormatUtil) {
        this.messageState = messageState;
        this.dateFormatUtil = dateFormatUtil;
    }

    public void processPIDFields(MessageElement messageElement, PID pid) throws DataTypeException {
        String pidField = messageElement.getHl7SegmentField().trim();
        String pidFieldValue = "";
        String questionDataTypeNND = messageElement.getDataElement().getQuestionDataTypeNND().trim();
        String questionIdentifierNND = messageElement.getQuestionIdentifierNND().trim();

        if (pidField.startsWith("PID-3.1")) {
            if (messageElement.getDataElement().getQuestionDataTypeNND().equals("CX")) {
                pid.getPid3_PatientIdentifierList(0).getIDNumber().setValue(messageElement.getDataElement().getCxDataType().getCxData());
            }
            if (messageElement.getDataElement().getQuestionDataTypeNND().equals("ST")) {
                pid.getPid3_PatientIdentifierList(0).getIDNumber().setValue(messageElement.getDataElement().getStDataType().getStringData());
            }
        } else if (pidField.startsWith("PID-3.4.1")) {
            pid.getPid3_PatientIdentifierList(0).getAssigningAuthority().getNamespaceID().setValue(messageElement.getDataElement().getIsDataType().getIsCodedValue());
        } else if (pidField.startsWith("PID-3.4.2")) {
            pid.getPid3_PatientIdentifierList(0).getAssigningAuthority().getUniversalID().setValue(messageElement.getDataElement().getStDataType().getStringData());
        } else if (pidField.startsWith("PID-3.4.3")) {
            pid.getPid3_PatientIdentifierList(0).getAssigningAuthority().getUniversalIDType().setValue(messageElement.getDataElement().getIdDataType().getIdCodedValue());
        } else if (pidField.startsWith("PID-5.7")) {
            pid.getPid5_PatientName(0).getNameTypeCode().setValue(messageElement.getDataElement().getIdDataType().getIdCodedValue());
        } else if (pidField.startsWith("PID-7.0")) {
            pidFieldValue = messageElement.getDataElement().getTsDataType().getTime().toString();
            String dateFormat = dateFormatUtil.formatDate(pidFieldValue, questionDataTypeNND, questionIdentifierNND, "PID-7");
            pid.getPid7_DateTimeOfBirth().getTime().setValue(dateFormat);
        } else if (pidField.startsWith("PID-8.0") && messageElement.getDataElement().getIdDataType() != null) {
            pid.getPid8_AdministrativeSex().setValue(messageElement.getDataElement().getIdDataType().getIdCodedValue());
        } else if (pidField.startsWith("PID-10.0")) {
            processPID10Fields(messageElement, pid);
        } else if (pidField.startsWith("PID-11.3")) {
            pid.getPid11_PatientAddress(messageState.getCityIndex()).getCity().setValue(messageElement.getDataElement().getStDataType().getStringData());
            messageState.setCityIndex(messageState.getCityIndex() + 1);
        } else if (pidField.startsWith("PID-11.4")) {
            pid.getPid11_PatientAddress(messageState.getStateIndex()).getStateOrProvince().setValue(messageElement.getDataElement().getCweDataType().getCweCodedValue());
            messageState.setStateIndex(messageState.getStateIndex() + 1);
        } else if (pidField.startsWith("PID-11.5")) {
            pid.getPid11_PatientAddress(messageState.getZipcodeIndex()).getZipOrPostalCode().setValue(messageElement.getDataElement().getStDataType().getStringData());
            messageState.setZipcodeIndex(messageState.getZipcodeIndex() + 1);
        } else if (pidField.startsWith("PID-11.6")) {
            pid.getPid11_PatientAddress(messageState.getCountryIndex()).getCountry().setValue(messageElement.getDataElement().getIdDataType().getIdCodedValue());
            messageState.setCountryIndex(messageState.getCountryIndex() + 1);
        } else if (pidField.startsWith("PID-11.7")) {
            pid.getPid11_PatientAddress(messageState.getAddressTypeIndex()).getAddressType().setValue(messageElement.getDataElement().getIdDataType().getIdCodedValue());
            messageState.setAddressTypeIndex(messageState.getAddressTypeIndex() + 1);
        } else if (pidField.startsWith("PID-11.9")) {
            pid.getPid11_PatientAddress(messageState.getCountryIndex()).getCountyParishCode().setValue(messageElement.getDataElement().getIsDataType().getIsCodedValue());
            messageState.setCountryIndex(messageState.getCountryIndex() + 1);
        } else if (pidField.startsWith("PID-11.10")) {
            pid.getPid11_PatientAddress(0).getCensusTract().setValue(messageElement.getDataElement().getIsDataType().getIsCodedValue());
        } else if (pidField.startsWith("PID-15.0")) {
            processPID15Fields(messageElement, pid);
        }
    }

    private void processPID10Fields(MessageElement messageElement, PID pid) throws DataTypeException {
        pid.getPid10_Race(messageState.getRaceIndex()).getIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValue());
        pid.getPid10_Race(messageState.getRaceIndex()).getText().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
        pid.getPid10_Race(messageState.getRaceIndex()).getNameOfCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem());
        pid.getPid10_Race(messageState.getRaceIndex()).getAlternateIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValue());
        pid.getPid10_Race(messageState.getRaceIndex()).getAlternateText().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
        pid.getPid10_Race(messageState.getRaceIndex()).getCe6_NameOfAlternateCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
        messageState.setRaceIndex(messageState.getRaceIndex() + 1);
    }

    private void processPID15Fields(MessageElement messageElement, PID pid) throws DataTypeException {
        pid.getPid15_PrimaryLanguage().getIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValue());
        pid.getPid15_PrimaryLanguage().getText().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
        pid.getPid15_PrimaryLanguage().getNameOfCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem());
        pid.getPid15_PrimaryLanguage().getAlternateIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValue());
        pid.getPid15_PrimaryLanguage().getAlternateText().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
    }
} 