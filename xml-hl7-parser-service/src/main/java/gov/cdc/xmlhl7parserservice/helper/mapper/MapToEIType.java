package gov.cdc.xmlhl7parserservice.helper.mapper;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.datatype.EI;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.EiDataType;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import org.springframework.stereotype.Component;

@Component
public class MapToEIType {

    void mapToEIType(MessageElement input, EI output) throws DataTypeException {
        EiDataType eiDataType = input.getDataElement().getEiDataType();
        if(eiDataType.getEntityIdentifier() != null) {
            output.getEntityIdentifier().setValue(eiDataType.getEntityIdentifier());
        }
        if(eiDataType.getNamespaceId() != null) {
            output.getNamespaceID().setValue(eiDataType.getNamespaceId());
        }
        if(eiDataType.getUniversalId() != null) {
            output.getUniversalID().setValue(eiDataType.getUniversalId());
        }
        if(eiDataType.getUniversalIdType() != null) {
            output.getUniversalIDType().setValue(eiDataType.getUniversalIdType());
        }
    }

}
