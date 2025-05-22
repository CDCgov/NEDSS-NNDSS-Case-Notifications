package gov.cdc.xmlhl7parserservice.helper.mapper;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.segment.NK1;
import gov.cdc.xmlhl7parserservice.helper.MessageState;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;

public class MapToNK1Element {

    MapToXADType xadMapper = new MapToXADType();
    MapToTSDayMonthYearType tsMapper = new MapToTSDayMonthYearType();
    MessageState messageState = new MessageState();

    private void mapToNK1Element(MessageElement messageElement, NK1 nk1) throws DataTypeException {
        String hl7Field = messageElement.getHl7SegmentField().trim();
        if (hl7Field.startsWith("NK1-")) {
            String dataElement = hl7Field.substring(4); // Remove "NK1-"

            if (dataElement.startsWith("3.")) {
                // Map to CE type for Relationship
                String ceCodedValue = messageElement.getDataElement().getCeDataType().getCeCodedValue().trim();
                String ceCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeCodedValueDescription().trim();
                String ceCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem().trim();
                String ceLocalCodedValue = messageElement.getDataElement().getCeDataType().getCeLocalCodedValue().trim();
                String ceLocalCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription().trim();
                String ceLocalCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem().trim();

                nk1.getRelationship().getIdentifier().setValue(ceCodedValue);
                nk1.getRelationship().getText().setValue(ceCodedValueDescription);
                nk1.getRelationship().getNameOfCodingSystem().setValue(ceCodedValueCodingSystem);
                nk1.getRelationship().getAlternateIdentifier().setValue(ceLocalCodedValue);
                nk1.getRelationship().getAlternateText().setValue(ceLocalCodedValueDescription);
                nk1.getRelationship().getNameOfAlternateCodingSystem().setValue(ceLocalCodedValueCodingSystem);

            } else if (dataElement.startsWith("4.")) {
                // Map to XAD type for Address
                String dataLocator = dataElement.substring(2);
                xadMapper.mapToXADType(messageElement, dataLocator, nk1.getAddress());

            } else if (dataElement.startsWith("14.")) {
                // Map to CE type for MaritalStatus
                String ceCodedValue = messageElement.getDataElement().getCeDataType().getCeCodedValue().trim();
                String ceCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeCodedValueDescription().trim();
                String ceCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem().trim();
                String ceLocalCodedValue = messageElement.getDataElement().getCeDataType().getCeLocalCodedValue().trim();
                String ceLocalCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription().trim();
                String ceLocalCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem().trim();

                nk1.getMaritalStatus().getIdentifier().setValue(ceCodedValue);
                nk1.getMaritalStatus().getText().setValue(ceCodedValueDescription);
                nk1.getMaritalStatus().getNameOfCodingSystem().setValue(ceCodedValueCodingSystem);
                nk1.getMaritalStatus().getAlternateIdentifier().setValue(ceLocalCodedValue);
                nk1.getMaritalStatus().getAlternateText().setValue(ceLocalCodedValueDescription);
                nk1.getMaritalStatus().getNameOfAlternateCodingSystem().setValue(ceLocalCodedValueCodingSystem);

            } else if (dataElement.startsWith("16.")) {
                // Map to TS type for DateTimeOfBirth
                String dataLocator = dataElement.substring(2);
                tsMapper.mapToTSDayMonthYearType(messageElement, nk1.getDateTimeOfBirth());

            } else if (dataElement.startsWith("28.")) {
                // Map to CE type for EthnicGroup
                String dataLocator = dataElement.substring(2);
                String ceCodedValue = messageElement.getDataElement().getCeDataType().getCeCodedValue().trim();
                String ceCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeCodedValueDescription().trim();
                String ceCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem().trim();
                String ceLocalCodedValue = messageElement.getDataElement().getCeDataType().getCeLocalCodedValue().trim();
                String ceLocalCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription().trim();
                String ceLocalCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem().trim();

                nk1.getEthnicGroup(0).getIdentifier().setValue(ceCodedValue);
                nk1.getEthnicGroup(0).getText().setValue(ceCodedValueDescription);
                nk1.getEthnicGroup(0).getNameOfCodingSystem().setValue(ceCodedValueCodingSystem);
                nk1.getEthnicGroup(0).getAlternateIdentifier().setValue(ceLocalCodedValue);
                nk1.getEthnicGroup(0).getAlternateText().setValue(ceLocalCodedValueDescription);
                nk1.getEthnicGroup(0).getNameOfAlternateCodingSystem().setValue(ceLocalCodedValueCodingSystem);

            } else if (dataElement.startsWith("35.")) {
                // Map to CE type for Race
                String dataLocator = dataElement.substring(2);
                String ceCodedValue = messageElement.getDataElement().getCeDataType().getCeCodedValue().trim();
                String ceCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeCodedValueDescription().trim();
                String ceCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem().trim();
                String ceLocalCodedValue = messageElement.getDataElement().getCeDataType().getCeLocalCodedValue().trim();
                String ceLocalCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription().trim();
                String ceLocalCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem().trim();

                int nk1RaceInc = messageState.getNk1RaceIndex();

                nk1.getRace(nk1RaceInc).getIdentifier().setValue(ceCodedValue);
                nk1.getRace(nk1RaceInc).getText().setValue(ceCodedValueDescription);
                nk1.getRace(nk1RaceInc).getNameOfCodingSystem().setValue(ceCodedValueCodingSystem);
                nk1.getRace(nk1RaceInc).getAlternateIdentifier().setValue(ceLocalCodedValue);
                nk1.getRace(nk1RaceInc).getAlternateText().setValue(ceLocalCodedValueDescription);
                nk1.getRace(nk1RaceInc).getNameOfAlternateCodingSystem().setValue(ceLocalCodedValueCodingSystem);

                messageState.setNk1RaceIndex(nk1RaceInc++);
            }
        }
    }
}
