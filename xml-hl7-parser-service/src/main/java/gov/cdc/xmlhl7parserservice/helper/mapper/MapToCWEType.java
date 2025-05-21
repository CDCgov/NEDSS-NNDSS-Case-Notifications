package gov.cdc.xmlhl7parserservice.helper.mapper;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.datatype.CWE;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import org.springframework.stereotype.Component;

@Component
public class MapToCWEType {

    void mapToCWEType(MessageElement input, CWE output) throws DataTypeException {
        MessageElement.DataElement.CweDataType cweDataType = input.getDataElement().getCweDataType();
        if(cweDataType.getCweCodedValue() != null)
        {
            output.getIdentifier().setValue(cweDataType.getCweCodedValue());
        } else {
            output.getIdentifier().setValue("MISSING");
        }
        if(cweDataType.getCweCodedValueCodingSystem() != null)
        {
            output.getNameOfCodingSystem().setValue(cweDataType.getCweCodedValueCodingSystem());
        } else {
            output.getNameOfCodingSystem().setValue("MISSING");
        }
        if(cweDataType.getCweCodedValueDescription() != null)
        {
            output.getText().setValue(cweDataType.getCweCodedValueDescription());
        } else {
            output.getText().setValue("MISSING");
        }

    }

}
