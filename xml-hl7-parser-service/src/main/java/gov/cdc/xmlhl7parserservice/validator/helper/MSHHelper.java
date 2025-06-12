package gov.cdc.xmlhl7parserservice.validator.helper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import gov.cdc.xmlhl7parserservice.validator.HL7Validator;

public class MSHHelper {

    HL7Validator validator = new HL7Validator();

    public void validateMSHFields(MSH msh) throws HL7Exception {

        validator.validateField("MSH-1", msh.getFieldSeparator().getValue(), true, 1, 1);
        validator.validateField("MSH-2", msh.getEncodingCharacters().getValue(), true, 4, 4);

        validator.validateField("MSH-3.NamespaceID", msh.getSendingApplication().getNamespaceID().getValue(), false, 0, 20);
        validator.validateField("MSH-3.UniversalID", msh.getSendingApplication().getUniversalID().getValue(), true, 0, 199);
        validator.validateField("MSH-3.UniversalIDType", msh.getSendingApplication().getUniversalIDType().getValue(), true, 0, 6);

        validator.validateField("MSH-4.NamespaceID", msh.getSendingFacility().getNamespaceID().getValue(), false, 0, 20);
        validator.validateField("MSH-4.UniversalID", msh.getSendingFacility().getUniversalID().getValue(), true, 0, 199);
        validator.validateField("MSH-4.UniversalIDType", msh.getSendingFacility().getUniversalIDType().getValue(), true, 0, 6);

        validator.validateField("MSH-5.NamespaceID", msh.getReceivingApplication().getNamespaceID().getValue(), false, 0, 20);
        validator.validateField("MSH-5.UniversalID", msh.getReceivingApplication().getUniversalID().getValue(), true, 0, 199);
        validator.validateField("MSH-5.UniversalIDType", msh.getReceivingApplication().getUniversalIDType().getValue(), true, 0, 6);

        validator.validateField("MSH-6.NamespaceID", msh.getReceivingFacility().getNamespaceID().getValue(), false, 0, 20);
        validator.validateField("MSH-6.UniversalID", msh.getReceivingFacility().getUniversalID().getValue(), true, 0, 199);
        validator.validateField("MSH-6.UniversalIDType", msh.getReceivingFacility().getUniversalIDType().getValue(), true, 0, 6);


        String dateTime = msh.getDateTimeOfMessage().getTime().getValue();
        validator.validateField("MSH-7", dateTime, false, 8, 26);
        if (dateTime != null && !dateTime.isEmpty()) {
            if (dateTime.length() < 4 || !dateTime.substring(0, 4).matches("\\d{4}")) {
                throw new HL7Exception("MSH-7.Year must be 4 digits");
            }
            if (dateTime.length() >= 6 && !dateTime.substring(4, 6).matches("0[1-9]|1[0-2]")) {
                throw new HL7Exception("MSH-7.Month must be 01-12");
            }
            if (dateTime.length() >= 8 && !dateTime.substring(6, 8).matches("0[1-9]|[12][0-9]|3[01]")) {
                throw new HL7Exception("MSH-7.Day must be 01-31");
            }
            if (dateTime.length() >= 10 && !dateTime.substring(8, 10).matches("[01][0-9]|2[0-3]")) {
                throw new HL7Exception("MSH-7.Hour must be 00-23");
            }
            if (dateTime.length() >= 12 && !dateTime.substring(10, 12).matches("[0-5][0-9]")) {
                throw new HL7Exception("MSH-7.Minute must be 00-59");
            }
            if (dateTime.length() >= 14 && !dateTime.substring(12, 14).matches("[0-5][0-9]")) {
                throw new HL7Exception("MSH-7.Second must be 00-59");
            }
        }

        validator.validateField("MSH-8", msh.getSecurity().getValue(), false, 0, 40);
        validator.validateField("MSH-9", msh.getMessageType().encode(), true, 1, 15);
        validator.validateField("MSH-10", msh.getMessageControlID().getValue(), true, 1, 199);
        validator.validateField("MSH-11", msh.getProcessingID().encode(), true, 1, 3);
        validator.validateField("MSH-12", msh.getVersionID().encode(), true, 1, 60);
        validator.validateField("MSH-13", msh.getSequenceNumber().getValue(), false, 0, 15);
        validator.validateField("MSH-14", msh.getContinuationPointer().getValue(), false, 0, 180);
        validator.validateField("MSH-15", msh.getAcceptAcknowledgmentType().getValue(), false, 0, 2);
        validator.validateField("MSH-16", msh.getApplicationAcknowledgmentType().getValue(), false, 0, 2);
        validator.validateField("MSH-17", msh.getCountryCode().getValue(), false, 0, 3);
        validator.validateField("MSH-18", msh.getCharacterSet(0).getValue(), false, 0, 16);
        validator.validateField("MSH-19", msh.getPrincipalLanguageOfMessage().encode(), false, 0, 250);
        validator.validateField("MSH-20", msh.getAlternateCharacterSetHandlingScheme().getValue(), false, 0, 20);


        int msh21Reps = msh.getMessageProfileIdentifierReps();
        if (msh21Reps < 2 || msh21Reps > 3) {
            throw new HL7Exception("MSH-21 must have between 2 and 3 repetitions");
        }
        for (int i = 0; i < msh21Reps; i++) {
            String value = msh.getMessageProfileIdentifier(i).toString();
            validator.validateField("MSH-21[" + (i + 1) + "]", value, true, 1, 427);
        }
    }
    
}
