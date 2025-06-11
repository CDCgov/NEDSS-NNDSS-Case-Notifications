package gov.cdc.xmlhl7parserservice.validator.helper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.segment.PID;
import gov.cdc.xmlhl7parserservice.validator.HL7Validator;

public class PIDHelper {

    HL7Validator validator = new HL7Validator();

    public void validatePIDFields(PID pid) throws HL7Exception {
        validator.validateField("PID-3.IDNumber", pid.getPatientIdentifierList(0).getIDNumber().getValue(), true, 1, 15);
//        validator.validateField("PID-3.IdentifierTypeCode", pid.getPatientIdentifierList(0).getIdentifierTypeCode().getValue(), true, 1, 5);
//        validator.validateField("PID-3.AssigningAuthority.NamespaceID", pid.getPatientIdentifierList(0).getAssigningAuthority().getNamespaceID().getValue(), false, 0, 20);
        validator.validateField("PID-3.AssigningAuthority.UniversalID", pid.getPatientIdentifierList(0).getAssigningAuthority().getUniversalID().getValue(), true, 0, 199);
        validator.validateField("PID-3.AssigningAuthority.UniversalIDType", pid.getPatientIdentifierList(0).getAssigningAuthority().getUniversalIDType().getValue(), true, 0, 6);

//        validator.validateField("PID-5.FamilyName", pid.getPatientName(0).getFamilyName().getSurname().getValue(), true, 1, 50);
//        validator.validateField("PID-5.GivenName", pid.getPatientName(0).getGivenName().getValue(), true, 1, 50);
//        validator.validateField("PID-5.SecondName", pid.getPatientName(0).getSecondAndFurtherGivenNamesOrInitialsThereof().getValue(), false, 0, 50);
//        validator.validateField("PID-5.Suffix", pid.getPatientName(0).getSuffixEgJRorIII().getValue(), false, 0, 10);
//        validator.validateField("PID-5.Prefix", pid.getPatientName(0).getPrefixEgDR().getValue(), false, 0, 10);

        String dob = pid.getDateTimeOfBirth().getTime().getValue();
        validator.validateField("PID-7", dob, true, 0, 26);
//        if (dob != null && !dob.isEmpty()) {
//            if (!dob.matches("\\d{8}")) {
//                throw new HL7Exception("PID-7 must be in YYYYMMDD format");
//            }
//        }

//        validator.validateField("PID-8", pid.getAdministrativeSex().getValue(), true, 1, 1);
//        String sex = pid.getAdministrativeSex().getValue();
//        if (sex != null && !sex.matches("[MFUO]")) {
//            throw new HL7Exception("PID-8 must be one of M, F, U, or O");
//        }

        validator.validateField("PID-10", pid.getRace(0).toString(), true, 0, 250);
//        validator.validateField("PID-10.Identifier", pid.getRace(0).getIdentifier().getValue(), false, 0, 20);
//        validator.validateField("PID-10.Text", pid.getRace(0).getText().getValue(), false, 0, 80);
//        validator.validateField("PID-10.NameOfCodingSystem", pid.getRace(0).getNameOfCodingSystem().getValue(), false, 0, 20);

//        validator.validateField("PID-11.Street", pid.getPatientAddress(0).getStreetAddress().getStreetOrMailingAddress().getValue(), false, 0, 100);
//        validator.validateField("PID-11.City", pid.getPatientAddress(0).getCity().getValue(), false, 0, 50);
//        validator.validateField("PID-11.State", pid.getPatientAddress(0).getStateOrProvince().getValue(), false, 0, 50);
//        validator.validateField("PID-11.Zip", pid.getPatientAddress(0).getZipOrPostalCode().getValue(), false, 0, 15);
//        validator.validateField("PID-11.Country", pid.getPatientAddress(0).getCountry().getValue(), false, 0, 3);
//
//        validator.validateField("PID-13.PhoneNumber", pid.getPhoneNumberHome(0).getTelephoneNumber().getValue(), false, 0, 20);

        validator.validateField("PID-16", pid.getMaritalStatus().toString(), true, 0, 250);
        validator.validateField("PID-17", pid.getReligion().toString(), true, 0, 250);

//        validator.validateField("PID-18.IDNumber", pid.getPatientAccountNumber().getIDNumber().getValue(), true, 1, 20);
//        validator.validateField("PID-18.AssigningAuthority.NamespaceID", pid.getPatientAccountNumber().getAssigningAuthority().getNamespaceID().getValue(), false, 0, 20);
//        validator.validateField("PID-18.AssigningAuthority.UniversalID", pid.getPatientAccountNumber().getAssigningAuthority().getUniversalID().getValue(), true, 0, 199);
//        validator.validateField("PID-18.AssigningAuthority.UniversalIDType", pid.getPatientAccountNumber().getAssigningAuthority().getUniversalIDType().getValue(), true, 0, 6);

        validator.validateField("PID-22", pid.getEthnicGroup(0).toString(), true, 0, 250);
        validator.validateField("PID-26", pid.getCitizenship(0).toString(), true, 0, 840);
        validator.validateField("PID-27", pid.getVeteransMilitaryStatus().toString(), true, 0, 250);
        validator.validateField("PID-28", pid.getNationality().toString(), true, 0, 840);
        validator.validateField("PID-35", pid.getSpeciesCode().toString(), true, 0, 250);
        validator.validateField("PID-36", pid.getBreedCode().toString(), true, 0, 250);
        validator.validateField("PID-38", pid.getProductionClassCode().toString(), true, 0, 250);
        validator.validateField("PID-39", pid.getTribalCitizenship(0).toString(), true, 0, 250);



    }
}
