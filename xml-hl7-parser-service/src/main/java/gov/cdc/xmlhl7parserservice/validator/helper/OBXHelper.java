package gov.cdc.xmlhl7parserservice.validator.helper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import gov.cdc.xmlhl7parserservice.validator.HL7Validator;

public class OBXHelper {

    HL7Validator validator = new HL7Validator();

    public void validateOBXFields(OBX obx) throws HL7Exception {
        validator.validateField("OBX-1", obx.getSetIDOBX().getValue(), false, 0, 4);
        validator.validateField("OBX-2", obx.getValueType().getValue(), false, 0, 3);
        validator.validateField("OBX-3", obx.getObservationIdentifier().toString(), true, 0, 840);
        validator.validateField("OBX-4", obx.getObservationSubID().getValue(), false, 0, 20);
        validator.validateField("OBX-5", obx.getObservationValue(0).toString(), false, 0, 99999);
        validator.validateField("OBX-6", obx.getUnits().toString(), false, 0, 840);
        validator.validateField("OBX-7", obx.getReferencesRange().getValue(), false, 0, 60);
        validator.validateField("OBX-8", obx.getAbnormalFlags(0).getValue(), false, 0, 5);
        validator.validateField("OBX-9", obx.getProbability().getValue(), false, 0, 5);
        validator.validateField("OBX-10", obx.getNatureOfAbnormalTest(0).getValue(), false, 0, 2);
        validator.validateField("OBX-11", obx.getObservationResultStatus().getValue(), false, 0, 1);
//        validator.validateField("OBX-12", obx.getnormal().toString(), false, 0, 26);
        validator.validateField("OBX-13", obx.getUserDefinedAccessChecks().getValue(), false, 0, 20);
        validator.validateField("OBX-14", obx.getDateTimeOfTheObservation().getTime().getValue(), false, 0, 26);
        validator.validateField("OBX-15", obx.getProducerSID().toString(), false, 0, 250);
        validator.validateField("OBX-16", obx.getResponsibleObserver(0).toString(), false, 0, 250);
        validator.validateField("OBX-17", obx.getObservationMethod(0).toString(), false, 0, 250);
        validator.validateField("OBX-18", obx.getEquipmentInstanceIdentifier(0).toString(), false, 0, 22);
        validator.validateField("OBX-19", obx.getDateTimeOfTheAnalysis().getTime().getValue(), false, 0, 26);
    }
}
