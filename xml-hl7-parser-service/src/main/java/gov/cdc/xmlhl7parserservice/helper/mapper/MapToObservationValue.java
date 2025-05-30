package gov.cdc.xmlhl7parserservice.helper.mapper;

import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import org.springframework.stereotype.Component;


@Component
public class MapToObservationValue {

    public String mapToObservationValue(MessageElement messageElement, String observationValue) {
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

}
