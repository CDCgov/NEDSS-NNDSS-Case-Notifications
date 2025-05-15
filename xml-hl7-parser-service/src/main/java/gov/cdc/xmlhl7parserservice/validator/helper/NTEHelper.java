package gov.cdc.xmlhl7parserservice.validator.helper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.segment.NTE;
import gov.cdc.xmlhl7parserservice.validator.HL7Validator;

public class NTEHelper {
    HL7Validator validator = new HL7Validator();

    public void validateNTEFields(NTE nte) throws HL7Exception {
        validator.validateField("NTE-1", nte.getSetIDNTE().getValue(), false, 0, 4);
        validator.validateField("NTE-2", nte.getSourceOfComment().toString(), false, 0, 8);
        validator.validateField("NTE-3", nte.getComment(0).toString(), false, 0, Integer.MAX_VALUE);
        validator.validateField("NTE-4", nte.getCommentType().toString(), true, 0, 250);
    }
}
