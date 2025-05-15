package gov.cdc.xmlhl7parserservice.validator;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_OBSERVATION;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_PATIENT_RESULT;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_SPECIMEN;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.*;
import ca.uhn.hl7v2.parser.PipeParser;
import gov.cdc.xmlhl7parserservice.exception.XmlHL7ParserException;
import gov.cdc.xmlhl7parserservice.validator.helper.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class HL7Validator {

    public boolean nndOruR01Validator(String hl7Message) throws XmlHL7ParserException, HL7Exception {
        PipeParser parser = new PipeParser();
        Message message = parser.parse(hl7Message);

        if (!(message instanceof ORU_R01 oruR01)) {
            throw new HL7Exception("Message is not ORU_R01");
        }

        MSH msh = oruR01.getMSH();
        if (msh == null) {
            throw new HL7Exception("Missing MSH segment");
        }
        MSHHelper mshHelper = new MSHHelper();
        mshHelper.validateMSHFields(msh);

        ORU_R01_PATIENT_RESULT patientResult = oruR01.getPATIENT_RESULT();
        if (patientResult == null) {
            throw new HL7Exception("Missing PATIENT_RESULT group");
        }
        if (patientResult.getPATIENT() == null) {
            log.info("Patient Result getPATIENT() is null.");
            throw new XmlHL7ParserException("Patient Result getPATIENT() is null.");
        }
        else {
            PID pid = patientResult.getPATIENT().getPID();
            if (pid == null) {
                throw new HL7Exception("Missing PID segment in PATIENT group");
            } else {
                PIDHelper pidHelper = new PIDHelper();
                pidHelper.validatePIDFields(pid);
            }

            int nk1Size = patientResult.getPATIENT().getNK1Reps();
            for (int i = 0; i < nk1Size; i++) {
                NK1 nk1 = patientResult.getPATIENT().getNK1(i);
                if (nk1 == null) {
                    log.info("NK1 segment not available.");
                }
                else {
                    NK1Helper nk1Helper = new NK1Helper();
                    nk1Helper.validateNK1Fields(nk1);
                }
            }
        }

        List<ORU_R01_ORDER_OBSERVATION> orderObservations = patientResult.getORDER_OBSERVATIONAll();

        if (orderObservations == null || orderObservations.isEmpty()) {
            throw new HL7Exception("Missing ORDER_OBSERVATION group");
        }
        for (ORU_R01_ORDER_OBSERVATION orderObx : orderObservations) {
            // TODO - OBR should be for loop?
            OBR obr = orderObx.getOBR();
            if (obr == null) {
                throw new HL7Exception("Missing OBR Segment");
            }
            else {
                OBRHelper obrHelper = new OBRHelper();
                obrHelper.validateOBRFields(obr);
            }

            NTE nte = orderObx.getNTE();
            if (nte == null) {
                log.info("Missing NTE Segment");
            }
            else {
                NTEHelper nteHelper = new NTEHelper();
                nteHelper.validateNTEFields(nte);
            }

            List<ORU_R01_OBSERVATION> observations = orderObx.getOBSERVATIONAll();
            for (ORU_R01_OBSERVATION observation : observations) {
                OBX obx = observation.getOBX();
                if (obx == null) {
                    throw new HL7Exception("Missing OBX Segment.");
                }
                else {
                    OBXHelper obxHelper = new OBXHelper();
                    obxHelper.validateOBXFields(obx);
                }
                NTE nteObx = observation.getNTE();
                if (nteObx == null) {
                   log.info("Missing NTE Segment in OBSERVATION.");
                }
                else {
                    NTEHelper nteHelper = new NTEHelper();
                    nteHelper.validateNTEFields(nteObx);
                }
            }

            List<ORU_R01_SPECIMEN> specimens = orderObx.getSPECIMENAll();
            for (ORU_R01_SPECIMEN specimen : specimens) {
                SPM spm = specimen.getSPM();
                if (spm == null) {
                    throw new HL7Exception("Missing OBX Segment");
                }
                else {
                    SPMHelper spmHelper = new SPMHelper();
                    spmHelper.validateSPMFields(spm);
                }

                OBX spmObx = specimen.getOBX();
                if (spmObx == null) {
                    log.info("Missing NTE Segment in SPECIMEN.");
                }
                else {
                    OBXHelper obxHelper = new OBXHelper();
                    obxHelper.validateOBXFields(spmObx);
                }
            }
        }
        return true;
    }

    public void validateField(String fieldName, String value, boolean required, int minLen, int maxLen) throws HL7Exception {
        if (required && (value == null || value.trim().isEmpty())) {
            throw new HL7Exception(fieldName + " is required but missing");
        }
        if (value != null) {
            int len = value.length();
            if (len < minLen || len > maxLen) {
                throw new HL7Exception(fieldName + " length must be between " + minLen + " and " + maxLen + " but was " + len);
            }
        }
    }
}
