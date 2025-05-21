package gov.cdc.xmlhl7parserservice.helper.mapper;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import org.springframework.stereotype.Component;

@Component
public class MapToCEType {

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

}
