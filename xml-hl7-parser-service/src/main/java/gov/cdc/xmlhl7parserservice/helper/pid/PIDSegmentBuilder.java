package gov.cdc.xmlhl7parserservice.helper.pid;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.segment.PID;
import gov.cdc.xmlhl7parserservice.helper.MessageState;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import gov.cdc.xmlhl7parserservice.util.HL7DateFormatUtil;
import org.springframework.stereotype.Component;

import java.util.Arrays;

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
            if (pid.getPid5_PatientName(0).getNameTypeCode() != null) {
                pid.getPid5_PatientName(0).getNameTypeCode().setValue("");
                pid.getPid5_PatientName(1).getNameTypeCode().setValue(messageElement.getDataElement().getIdDataType().getIdCodedValue());
            }
            else {
                pid.getPid5_PatientName(0).getNameTypeCode().setValue(messageElement.getDataElement().getIdDataType().getIdCodedValue());
            }
        } else if (pidField.startsWith("PID-7.0")) {
            pidFieldValue = messageElement.getDataElement().getTsDataType().getTime().toString();
            String dateFormat = dateFormatUtil.formatDate(pidFieldValue, questionDataTypeNND, questionIdentifierNND, "PID-7");
            pid.getPid7_DateTimeOfBirth().getTime().setValue(dateFormat);
        } else if (pidField.startsWith("PID-8.0") && messageElement.getDataElement().getIsDataType() != null) {
            pid.getPid8_AdministrativeSex().setValue(messageElement.getDataElement().getIsDataType().getIsCodedValue());
        } else if (pidField.startsWith("PID-10.0")) {
            pid.getPid10_Race(messageState.getRaceIndex()).getIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValue());
            pid.getPid10_Race(messageState.getRaceIndex()).getText().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
            pid.getPid10_Race(messageState.getRaceIndex()).getNameOfCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem());
            pid.getPid10_Race(messageState.getRaceIndex()).getAlternateIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValue());
            pid.getPid10_Race(messageState.getRaceIndex()).getAlternateText().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
            pid.getPid10_Race(messageState.getRaceIndex()).getCe6_NameOfAlternateCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
            messageState.setRaceIndex(messageState.getRaceIndex() + 1);
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
            pid.getPid15_PrimaryLanguage().getIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValue());
            pid.getPid15_PrimaryLanguage().getText().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
            pid.getPid15_PrimaryLanguage().getNameOfCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem());
            pid.getPid15_PrimaryLanguage().getAlternateIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValue());
            pid.getPid15_PrimaryLanguage().getAlternateText().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
        }
        else if (pidField.startsWith("PID-16.0")) {
            //TODO - need to find an XML message with PID-16 in order to extract values from the correct data type field
            pid.getPid16_MaritalStatus().getIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValue());
            pid.getPid16_MaritalStatus().getText().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
            pid.getPid16_MaritalStatus().getNameOfCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem());
            pid.getPid16_MaritalStatus().getAlternateIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValue());
            pid.getPid16_MaritalStatus().getAlternateText().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
            pid.getPid16_MaritalStatus().getNameOfAlternateCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem());
        } else if (pidField.startsWith("PID-17.0")) {
            //TODO - need to find an XML message with PID-17 in order to extract values from the correct data type field
            pid.getPid17_Religion().getIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValue());
            pid.getPid17_Religion().getText().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
            pid.getPid17_Religion().getNameOfCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem());
            pid.getPid17_Religion().getAlternateIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValue());
            pid.getPid17_Religion().getAlternateText().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
            pid.getPid17_Religion().getNameOfAlternateCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem());
        } else if (pidField.startsWith("PID-22.0")) {
            pid.getPid22_EthnicGroup(0).getIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValue());
            pid.getPid22_EthnicGroup(0).getText().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
            pid.getPid22_EthnicGroup(0).getNameOfCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem());
            pid.getPid22_EthnicGroup(0).getAlternateIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValue());
            pid.getPid22_EthnicGroup(0).getAlternateText().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
            pid.getPid22_EthnicGroup(0).getNameOfAlternateCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem());
        } else if (pidField.startsWith("PID-23.0")) {
            pid.getPid23_BirthPlace().setValue(messageElement.getDataElement().getStDataType().getStringData());
        } else if (pidField.startsWith("PID-24.0")) {
            pid.getPid24_MultipleBirthIndicator().setValue(messageElement.getDataElement().getIdDataType().getIdCodedValue());
        } else if (pidField.startsWith("PID-25.0")) {
            pid.getPid25_BirthOrder().setValue(messageElement.getDataElement().getNmDataType().getNum());
        } else if (pidField.startsWith("PID-26.0")) {
            int citizenshipTypeIndex = messageState.getCitizenshipTypeIndex();
            pid.getPid26_Citizenship(citizenshipTypeIndex).getIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValue());
            pid.getPid26_Citizenship(citizenshipTypeIndex).getText().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
            pid.getPid26_Citizenship(citizenshipTypeIndex).getNameOfCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem());
            pid.getPid26_Citizenship(citizenshipTypeIndex).getAlternateIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValue());
            pid.getPid26_Citizenship(citizenshipTypeIndex).getAlternateText().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
            pid.getPid26_Citizenship(citizenshipTypeIndex).getNameOfAlternateCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem());
            messageState.setCitizenshipTypeIndex(citizenshipTypeIndex + 1);
        } else if (pidField.startsWith("PID-27.0")) {
            pid.getPid27_VeteransMilitaryStatus().getIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValue());
            pid.getPid27_VeteransMilitaryStatus().getText().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
            pid.getPid27_VeteransMilitaryStatus().getNameOfCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem());
            pid.getPid27_VeteransMilitaryStatus().getAlternateIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValue());
            pid.getPid27_VeteransMilitaryStatus().getAlternateText().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
            pid.getPid27_VeteransMilitaryStatus().getNameOfAlternateCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem());
        }else if (pidField.startsWith("PID-29.0")) {
            pidFieldValue = messageElement.getDataElement().getTsDataType().getTime().toString().trim();
            String dateFormat = dateFormatUtil.formatDate(pidFieldValue, questionDataTypeNND, questionIdentifierNND,"PID-29.0");
            pid.getPid29_PatientDeathDateAndTime().getTime().setValue(dateFormat);
        }else if (pidField.startsWith("PID-30.0")) {
            pid.getPid30_PatientDeathIndicator().setValue(messageElement.getDataElement().getIdDataType().getIdCodedValue());
        }else if (pidField.startsWith("PID-31.0")) {
            pid.getPid31_IdentityUnknownIndicator().setValue(messageElement.getDataElement().getIdDataType().getIdCodedValue());
        }else if (pidField.startsWith("PID-32.0")) {
            pid.getPid32_IdentityReliabilityCode(messageState.getIdentityReliabilityCodeIndex()).setValue(messageElement.getDataElement().getIsDataType().getIsCodedValue());
            messageState.setIdentityReliabilityCodeIndex(messageState.getIdentityReliabilityCodeIndex() + 1);
        }else if (pidField.startsWith("PID-33.0")) {
            String dateFormat = dateFormatUtil.formatDate(pidFieldValue, questionDataTypeNND, questionIdentifierNND, "PID-33.0");
            pid.getPid33_LastUpdateDateTime().getTime().setValue(dateFormat);
        }else if (pidField.startsWith("PID-34.1")) {
            pid.getPid34_LastUpdateFacility().getNamespaceID().setValue(messageElement.getDataElement().getIsDataType().getIsCodedValue());
        }else if (pidField.startsWith("PID-34.2")) {
            pid.getPid34_LastUpdateFacility().getUniversalID().setValue(messageElement.getDataElement().getStDataType().getStringData());
        }else if (pidField.startsWith("PID-34.3")) {
            pid.getPid34_LastUpdateFacility().getUniversalIDType().setValue(messageElement.getDataElement().getIdDataType().getIdCodedValue());
        }else if (pidField.startsWith("PID-35")) {
            pid.getPid35_SpeciesCode().getIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValue());
            pid.getPid35_SpeciesCode().getText().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
            pid.getPid35_SpeciesCode().getNameOfCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem());
            pid.getPid35_SpeciesCode().getAlternateIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValue());
            pid.getPid35_SpeciesCode().getAlternateText().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
            pid.getPid35_SpeciesCode().getNameOfAlternateCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem());
        }else if (pidField.startsWith("PID-36")) {
            pid.getPid36_BreedCode().getIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValue());
            pid.getPid36_BreedCode().getText().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
            pid.getPid36_BreedCode().getNameOfCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem());
            pid.getPid36_BreedCode().getAlternateIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValue());
            pid.getPid36_BreedCode().getAlternateText().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
            pid.getPid36_BreedCode().getNameOfAlternateCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem());
        }else if (pidField.startsWith("PID-37")) {
            pid.getPid37_Strain().setValue(messageElement.getDataElement().getStDataType().getStringData());
        }else if (pidField.startsWith("PID-38")) {
            pid.getPid38_ProductionClassCode().getIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValue());
            pid.getPid38_ProductionClassCode().getText().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
            pid.getPid38_ProductionClassCode().getNameOfCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem());
            pid.getPid38_ProductionClassCode().getAlternateIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValue());
            pid.getPid38_ProductionClassCode().getAlternateText().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
            pid.getPid38_ProductionClassCode().getNameOfAlternateCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem());
        }
    }
} 