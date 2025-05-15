package gov.cdc.xmlhl7parserservice.validator.helper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import gov.cdc.xmlhl7parserservice.validator.HL7Validator;

public class OBRHelper {

    HL7Validator validator = new HL7Validator();

    public void validateOBRFields(OBR obr) throws HL7Exception {
        validator.validateField("OBR-1", obr.getSetIDOBR().getValue(), false, 0, 4);
        validator.validateField("OBR-2", obr.getPlacerOrderNumber().toString(), true, 0, 22);
        validator.validateField("OBR-3", obr.getFillerOrderNumber().toString(), true, 0, 427);
        validator.validateField("OBR-4", obr.getUniversalServiceIdentifier().toString(), true, 0, 840);
        validator.validateField("OBR-5", obr.getPriorityOBR().getValue(), false, 0, 1);
        validator.validateField("OBR-6", obr.getRequestedDateTime().getTime().getValue(), false, 0, 26);
        validator.validateField("OBR-7", obr.getObservationDateTime().getTime().getValue(), false, 0, 26);
        validator.validateField("OBR-8", obr.getObservationEndDateTime().getTime().getValue(), false, 0, 26);
        validator.validateField("OBR-9", obr.getCollectionVolume().toString(), false, 0, 20);
        validator.validateField("OBR-10", obr.getCollectorIdentifier(0).toString(), false, 0, 250);
        validator.validateField("OBR-11", obr.getSpecimenActionCode().getValue(), false, 0, 1);
        validator.validateField("OBR-12", obr.getDangerCode().toString(), false, 0, 250);
        validator.validateField("OBR-13", obr.getRelevantClinicalInformation().getValue(), false, 0, 300);
        validator.validateField("OBR-14", obr.getSpecimenReceivedDateTime().getTime().getValue(), false, 0, 26);
        validator.validateField("OBR-15", obr.getSpecimenSource().toString(), false, 0, 300);
        validator.validateField("OBR-16", obr.getOrderingProvider(0).toString(), false, 0, 250);
        validator.validateField("OBR-17", obr.getOrderCallbackPhoneNumber(0).toString(), false, 0, 250);
        validator.validateField("OBR-18", obr.getPlacerField1().getValue(), false, 0, 60);
        validator.validateField("OBR-19", obr.getPlacerField2().getValue(), false, 0, 60);
        validator.validateField("OBR-20", obr.getFillerField1().getValue(), false, 0, 60);
        validator.validateField("OBR-21", obr.getFillerField2().getValue(), false, 0, 60);
        validator.validateField("OBR-22", obr.getResultsRptStatusChngDateTime().getTime().getValue(), false, 0, 26);
        validator.validateField("OBR-23", obr.getChargeToPractice().toString(), false, 0, 40);
        validator.validateField("OBR-24", obr.getDiagnosticServSectID().getValue(), false, 0, 10);
        validator.validateField("OBR-25", obr.getResultStatus().getValue(), false, 0, 1);
        validator.validateField("OBR-26", obr.getParentResult().toString(), false, 0, 400);
        validator.validateField("OBR-27", obr.getQuantityTiming(0).toString(), false, 0, 200);
        validator.validateField("OBR-28", obr.getResultCopiesTo(0).toString(), false, 0, 250);
        validator.validateField("OBR-29", obr.getParent().toString(), false, 0, 200);
        validator.validateField("OBR-30", obr.getTransportationMode().getValue(), false, 0, 20);
        validator.validateField("OBR-31", obr.getReasonForStudy(0).toString(), true, 0, 840);
        validator.validateField("OBR-32", obr.getPrincipalResultInterpreter().toString(), false, 0, 250);
        validator.validateField("OBR-33", obr.getAssistantResultInterpreter(0).toString(), false, 0, 250);
        validator.validateField("OBR-34", obr.getTechnician(0).toString(), false, 0, 250);
        validator.validateField("OBR-35", obr.getTranscriptionist(0).toString(), false, 0, 250);
        validator.validateField("OBR-36", obr.getScheduledDateTime().getTime().getValue(), false, 0, 26);
        validator.validateField("OBR-37", obr.getNumberOfSampleContainers().getValue(), false, 0, 4);
        validator.validateField("OBR-38", obr.getTransportLogisticsOfCollectedSample(0).toString(), false, 0, 250);
        validator.validateField("OBR-39", obr.getCollectorSComment(0).toString(), false, 0, 250);
        validator.validateField("OBR-40", obr.getTransportArrangementResponsibility().toString(), false, 0, 250);
        validator.validateField("OBR-41", obr.getTransportArranged().getValue(), false, 0, 30);
        validator.validateField("OBR-42", obr.getEscortRequired().getValue(), false, 0, 30);
        validator.validateField("OBR-43", obr.getPlannedPatientTransportComment(0).toString(), false, 0, 250);
        validator.validateField("OBR-44", obr.getProcedureCode().toString(), true, 0, 250);
        validator.validateField("OBR-45", obr.getProcedureCodeModifier(0).toString(), true, 0, 250);
        validator.validateField("OBR-46", obr.getPlacerSupplementalServiceInformation(0).toString(), false, 0, 250);
        validator.validateField("OBR-47", obr.getFillerSupplementalServiceInformation(0).toString(), false, 0, 250);
        validator.validateField("OBR-48", obr.getMedicallyNecessaryDuplicateProcedureReason().toString(), false, 0, 250);
        validator.validateField("OBR-49", obr.getResultHandling().toString(), false, 0, 2);

    }
}
