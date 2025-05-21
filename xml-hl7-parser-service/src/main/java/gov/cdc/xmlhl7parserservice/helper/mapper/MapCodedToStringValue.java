package gov.cdc.xmlhl7parserservice.helper.mapper;

import gov.cdc.xmlhl7parserservice.model.ParentLink;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import org.springframework.stereotype.Component;

@Component
public class MapCodedToStringValue {

    // TODO - Check where this return value is being used
    String mapCodedToStringValue(MessageElement messageElement, String obxResult, ParentLink parentLink) {
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

}
