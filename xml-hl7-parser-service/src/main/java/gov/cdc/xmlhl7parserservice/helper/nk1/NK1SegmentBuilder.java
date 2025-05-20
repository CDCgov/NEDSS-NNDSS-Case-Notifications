package gov.cdc.xmlhl7parserservice.helper.nk1;

import ca.uhn.hl7v2.model.v25.segment.NK1;
import ca.uhn.hl7v2.model.DataTypeException;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import gov.cdc.xmlhl7parserservice.helper.MessageState;
import org.springframework.stereotype.Component;

@Component
public class NK1SegmentBuilder {
    private final MessageState messageState;

    public NK1SegmentBuilder(MessageState messageState) {
        this.messageState = messageState;
    }

    public void processNK1Fields(MessageElement messageElement, NK1 nk1) throws DataTypeException {
        String nk1Field = messageElement.getHl7SegmentField().trim();
        String ceCodedValue = messageElement.getDataElement().getCeDataType().getCeCodedValue().trim();
        String ceCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeCodedValueDescription().trim();
        String ceCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem().trim();
        String ceLocalCodedValue = messageElement.getDataElement().getCeDataType().getCeLocalCodedValue().trim();
        String ceLocalCodedValueDescription = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription().trim();
        String ceLocalCodedValueCodingSystem = messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem().trim();
        nk1.getSetIDNK1().setValue("1");

        if (nk1Field.equals("NK1-28.0")){
            nk1.getNk128_EthnicGroup(0).getIdentifier().setValue(ceCodedValue);
            nk1.getNk128_EthnicGroup(0).getText().setValue(ceCodedValueDescription);
            nk1.getNk128_EthnicGroup(0).getNameOfCodingSystem().setValue(ceCodedValueCodingSystem);
            nk1.getNk128_EthnicGroup(0).getAlternateIdentifier().setValue(ceLocalCodedValue);
            nk1.getNk128_EthnicGroup(0).getAlternateText().setValue(ceLocalCodedValueDescription);
            nk1.getNk128_EthnicGroup(0).getNameOfAlternateCodingSystem().setValue(ceLocalCodedValueCodingSystem);
            nk1.getNk128_EthnicGroup(0).getNameOfAlternateCodingSystem().setValue(ceLocalCodedValueCodingSystem);
        }else if (nk1Field.equals("NK1-35.0")){
            nk1.getNk135_Race(messageState.getNk1RaceIndex()).getIdentifier().setValue(ceCodedValue);
            nk1.getNk135_Race(messageState.getNk1RaceIndex()).getText().setValue(ceCodedValueDescription);
            nk1.getNk135_Race(messageState.getNk1RaceIndex()).getNameOfCodingSystem().setValue(ceCodedValueCodingSystem);
            nk1.getNk135_Race(messageState.getNk1RaceIndex()).getAlternateIdentifier().setValue(ceLocalCodedValue);
            nk1.getNk135_Race(messageState.getNk1RaceIndex()).getAlternateText().setValue(ceLocalCodedValueDescription);
            nk1.getNk135_Race(messageState.getNk1RaceIndex()).getNameOfAlternateCodingSystem().setValue(ceLocalCodedValueCodingSystem);
            messageState.setNk1RaceIndex(messageState.getNk1RaceIndex() + 1);
        }
    }
} 