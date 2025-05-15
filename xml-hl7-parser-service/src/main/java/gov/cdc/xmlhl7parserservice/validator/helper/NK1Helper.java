package gov.cdc.xmlhl7parserservice.validator.helper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.segment.NK1;
import gov.cdc.xmlhl7parserservice.validator.HL7Validator;

public class NK1Helper {

    HL7Validator validator = new HL7Validator();

    public void validateNK1Fields(NK1 nk1) throws HL7Exception {
        validator.validateField("NK1-1", nk1.getSetIDNK1().getValue(), false, 0, 4);
        //validator.validateField("NK1-2", nk1.getName(), false, 0, 250);
        validator.validateField("NK1-3", nk1.getRelationship().toString(), true, 0, 250);
        validator.validateField("NK1-4", nk1.getAddress(0).toString(), false, 0, 250);
        validator.validateField("NK1-5", nk1.getPhoneNumber(0).toString(), false, 0, 250);
        validator.validateField("NK1-6", nk1.getBusinessPhoneNumber(0).toString(), false, 0, 250);
        validator.validateField("NK1-7", nk1.getContactRole().toString(), false, 0, 250);
        validator.validateField("NK1-8", nk1.getStartDate().getValue(), false, 0, 26);
        validator.validateField("NK1-9", nk1.getEndDate().getValue(), false, 0, 26);
        validator.validateField("NK1-10", nk1.getNextOfKinAssociatedPartiesJobTitle().getValue(), false, 0, 250);
        validator.validateField("NK1-11", nk1.getNextOfKinAssociatedPartiesJobCodeClass().toString(), false, 0, 250);
        validator.validateField("NK1-12", nk1.getNextOfKinAssociatedPartiesEmployeeNumber().toString(), false, 0, 250);
        validator.validateField("NK1-13", nk1.getOrganizationNameNK1(0).toString(), false, 0, 250);
        validator.validateField("NK1-14", nk1.getMaritalStatus().toString(), true, 0, 250);
        validator.validateField("NK1-15", nk1.getAdministrativeSex().getValue(), false, 0, 1);
        validator.validateField("NK1-16", nk1.getDateTimeOfBirth().getTime().getValue(), false, 0, 26);
        validator.validateField("NK1-17", nk1.getLivingDependency(0).getValue(), false, 0, 2);
        validator.validateField("NK1-18", nk1.getAmbulatoryStatus(0).toString(), false, 0, 2);
        validator.validateField("NK1-19", nk1.getCitizenship(0).toString(), true, 0, 250);
        validator.validateField("NK1-20", nk1.getPrimaryLanguage().toString(), false, 0, 250);
        validator.validateField("NK1-21", nk1.getLivingArrangement().getValue(), false, 0, 2);
        validator.validateField("NK1-22", nk1.getPublicityCode().toString(), true, 0, 250);
        validator.validateField("NK1-23", nk1.getProtectionIndicator().getValue(), false, 0, 1);
        validator.validateField("NK1-24", nk1.getStudentIndicator().getValue(), false, 0, 1);
        validator.validateField("NK1-25", nk1.getReligion().toString(), true, 0, 250);
        validator.validateField("NK1-26", nk1.getMotherSMaidenName(0).toString(), false, 0, 250);
        validator.validateField("NK1-27", nk1.getNationality().toString(), true, 0, 250);
        validator.validateField("NK1-28", nk1.getEthnicGroup(0).toString(), true, 0, 250);
        validator.validateField("NK1-29", nk1.getContactReason(0).toString(), false, 0, 250);
        validator.validateField("NK1-30", nk1.getContactPersonSName(0).toString(), false, 0, 250);
        validator.validateField("NK1-31", nk1.getContactPersonSTelephoneNumber(0).toString(), false, 0, 250);
        validator.validateField("NK1-32", nk1.getContactPersonSAddress(0).toString(), false, 0, 250);
//        validator.validateField("NK1-33", nk1.getnextofkinassociatedpartiesI(0).toString(), false, 0, 250);
        validator.validateField("NK1-34", nk1.getJobStatus().getValue(), false, 0, 250);
        validator.validateField("NK1-35", nk1.getRace(0).toString(), true, 0, 250);
        validator.validateField("NK1-36", nk1.getHandicap().getValue(), false, 0, 250);
        validator.validateField("NK1-37", nk1.getContactPersonSocialSecurityNumber().getValue(), false, 0, 250);
        validator.validateField("NK1-38", nk1.getNextOfKinBirthPlace().getValue(), false, 0, 250);
        validator.validateField("NK1-39", nk1.getVIPIndicator().getValue(), false, 0, 2);

    }
}
