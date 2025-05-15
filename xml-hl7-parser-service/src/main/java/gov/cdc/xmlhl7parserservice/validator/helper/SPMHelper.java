package gov.cdc.xmlhl7parserservice.validator.helper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.segment.SPM;
import gov.cdc.xmlhl7parserservice.validator.HL7Validator;

public class SPMHelper {
    HL7Validator validator = new HL7Validator();

    public void validateSPMFields(SPM spm) throws HL7Exception {
        validator.validateField("SPM-1", spm.getSetIDSPM().getValue(), false, 0, 4);
        validator.validateField("SPM-2", spm.getSpecimenID().toString(), false, 0, 80);
        validator.validateField("SPM-3", spm.getSpecimenParentIDs(0).toString(), false, 0, 80);
        validator.validateField("SPM-4", spm.getSpecimenType().toString(), true, 0, 250);
        validator.validateField("SPM-5", spm.getSpecimenTypeModifier(0).toString(), false, 0, 250);
        validator.validateField("SPM-6", spm.getSpecimenAdditives(0).toString(), false, 0, 250);
        validator.validateField("SPM-7", spm.getSpecimenCollectionMethod().toString(), false, 0, 250);
        validator.validateField("SPM-8", spm.getSpecimenSourceSite().toString(), false, 0, 250);
        validator.validateField("SPM-9", spm.getSpecimenSourceSiteModifier(0).toString(), false, 0, 250);
        validator.validateField("SPM-10", spm.getSpecimenCollectionSite().toString(), false, 0, 250);
        validator.validateField("SPM-11", spm.getSpecimenRole(0).toString(), false, 0, 250);
        validator.validateField("SPM-12", spm.getSpecimenCollectionAmount().toString(), false, 0, 20);
        validator.validateField("SPM-13", spm.getGroupedSpecimenCount().getValue(), false, 0, 6);
        validator.validateField("SPM-14", spm.getSpecimenDescription(0).getValue(), false, 0, 250);
        validator.validateField("SPM-15", spm.getSpecimenHandlingCode(0).toString(), false, 0, 250);
        validator.validateField("SPM-16", spm.getSpecimenRiskCode(0).toString(), false, 0, 250);
        validator.validateField("SPM-17", spm.getSpecimenCollectionDateTime().toString(), false, 0, 26);
        validator.validateField("SPM-18", spm.getSpecimenReceivedDateTime().getTime().getValue(), false, 0, 26);
        validator.validateField("SPM-19", spm.getSpecimenExpirationDateTime().getTime().getValue(), false, 0, 26);
        validator.validateField("SPM-20", spm.getSpecimenAvailability().getValue(), false, 0, 1);
        validator.validateField("SPM-21", spm.getSpecimenRejectReason(0).toString(), false, 0, 250);
        validator.validateField("SPM-22", spm.getSpecimenQuality().toString(), false, 0, 250);
        validator.validateField("SPM-23", spm.getSpecimenAppropriateness().toString(), false, 0, 250);
        validator.validateField("SPM-24", spm.getSpecimenCondition(0).toString(), false, 0, 250);
        validator.validateField("SPM-25", spm.getSpecimenCurrentQuantity().toString(), false, 0, 20);
        validator.validateField("SPM-26", spm.getNumberOfSpecimenContainers().getValue(), false, 0, 4);
        validator.validateField("SPM-27", spm.getContainerType().toString(), false, 0, 250);
        validator.validateField("SPM-28", spm.getContainerCondition().toString(), false, 0, 250);
        validator.validateField("SPM-29", spm.getSpecimenChildRole().toString(), false, 0, 250);
    }
}
