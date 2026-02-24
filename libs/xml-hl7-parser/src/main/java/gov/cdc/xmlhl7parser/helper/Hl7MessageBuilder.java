package gov.cdc.xmlhl7parser.helper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;

import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v25.datatype.*;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import ca.uhn.hl7v2.model.v25.segment.*;

import gov.cdc.xmlhl7parser.exception.XmlHl7ParserException;
import gov.cdc.xmlhl7parser.helper.mapper.*;
import gov.cdc.xmlhl7parser.helper.obx.OBXSegmentBuilder;
import gov.cdc.xmlhl7parser.model.*;
import gov.cdc.xmlhl7parser.model.Obx.ObxRepeatingElement;
import gov.cdc.xmlhl7parser.model.generated.jaxb.*;

import gov.cdc.xmlhl7parser.validator.Hl7Validator;
import jakarta.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import java.io.StringReader;
import java.util.*;

import gov.cdc.xmlhl7parser.helper.msh.MSHSegmentBuilder;
import gov.cdc.xmlhl7parser.helper.pid.PIDSegmentBuilder;
import gov.cdc.xmlhl7parser.util.Hl7DateFormatUtil;
import gov.cdc.xmlhl7parser.helper.nk1.NK1SegmentBuilder;
import gov.cdc.xmlhl7parser.helper.obr.OBRSegmentBuilder;

@Component
public class Hl7MessageBuilder {

    private final MSHSegmentBuilder mshSegmentBuilder;
    private final PIDSegmentBuilder pidSegmentBuilder;
    private final NK1SegmentBuilder nk1SegmentBuilder;
    private final OBRSegmentBuilder obrSegmentBuilder;
    private final OBXSegmentBuilder obxSegmentBuilder;
    private final Hl7DateFormatUtil dateFormatUtil;
    private final MapToDynamicParentRptToRpt mapToDynamicParentRptToRpt;
    private final MapToDynamicRootlDiscToRepeat mapToDynamicRootlDiscToRepeat;
    private final MapToDisRepeat mapToDisRepeat;
    private final MapToRepeatToMultiNND mapToRepeatToMultiNND;
    private final MapToQuestionMap mapToQuestionMap;
    private final MapLabReportEventToOBR mapLabReportEventToOBR;


    public static final String FIELD_SEPARATOR = "|";
    public static final String ENCODING_CHARACTERS = "^~\\&";

    @Autowired
    public Hl7MessageBuilder(
            MSHSegmentBuilder mshSegmentBuilder,
            PIDSegmentBuilder pidSegmentBuilder,
            NK1SegmentBuilder nk1SegmentBuilder,
            OBRSegmentBuilder obrSegmentBuilder, OBXSegmentBuilder obxSegmentBuilder,
            Hl7DateFormatUtil dateFormatUtil,
            MapToDynamicParentRptToRpt mapToDynamicParentRptToRpt,
            MapToDynamicRootlDiscToRepeat mapToDynamicRootlDiscToRepeat,
            MapToDisRepeat mapToDisRepeat,
            MapToRepeatToMultiNND mapToRepeatToMultiNND,
            MapToQuestionMap mapToQuestionMap,
            MapLabReportEventToOBR mapLabReportEventToOBR
    ) {
        this.mshSegmentBuilder = mshSegmentBuilder;
        this.pidSegmentBuilder = pidSegmentBuilder;
        this.nk1SegmentBuilder = nk1SegmentBuilder;
        this.obrSegmentBuilder = obrSegmentBuilder;
        this.obxSegmentBuilder = obxSegmentBuilder;
        this.dateFormatUtil = dateFormatUtil;
        this.mapToDynamicParentRptToRpt = mapToDynamicParentRptToRpt;
        this.mapToDynamicRootlDiscToRepeat = mapToDynamicRootlDiscToRepeat;
        this.mapToDisRepeat = mapToDisRepeat;
        this.mapToRepeatToMultiNND = mapToRepeatToMultiNND;
        this.mapToQuestionMap = mapToQuestionMap;
        this.mapLabReportEventToOBR = mapLabReportEventToOBR;
    }

    private static final Logger logger = LoggerFactory.getLogger(Hl7MessageBuilder.class);

    /**
     * Builds an HL7 message from an NBS NND intermediary XML payload.
     *
     * <p>Unmarshalls the XML into an NBSNNDIntermediaryMessage, then
     * calls parseXml to construct and populate the HL7 message segments
     * and any associated lab report events.
     *
     * @param xmlPayload         the NBS NND intermediary XML string to parse
     * @param validationEnabled  whether to run HL7 validation on the constructed message
     * @return the fully constructed HL7 message as a string
     * @throws XmlHL7ParserException if XML unmarshalling or HL7 message construction fails
     */
    public String buildHl7Message(String xmlPayload, boolean validationEnabled) throws XmlHl7ParserException {
        try {
            JAXBContext context = JAXBContext.newInstance(NBSNNDIntermediaryMessage.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            StringReader reader = new StringReader(xmlPayload);
            NBSNNDIntermediaryMessage nbsnndIntermediaryMessage =
                    (NBSNNDIntermediaryMessage) unmarshaller.unmarshal(reader);

            return parseXml(nbsnndIntermediaryMessage, validationEnabled);
        } catch (JAXBException e) {
            throw new XmlHl7ParserException("Failed to construct NBSNNDIntermediaryMessage. ", e);
        } catch (HL7Exception e) {
            throw new XmlHl7ParserException("Failed to parse NBSNNDIntermediaryMessage to HL7", e);
        }
    }

    public String parseXml(NBSNNDIntermediaryMessage nbsnndIntermediaryMessage, boolean validationEnabled) throws HL7Exception, XmlHl7ParserException {
        ORU_R01 oruMessage = new ORU_R01();

        MSH msh = oruMessage.getMSH();
        PID pid = oruMessage.getPATIENT_RESULT().getPATIENT().getPID();
        OBR obr = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR();
        NK1 nk1 = oruMessage.getPATIENT_RESULT().getPATIENT().getNK1();
        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0);
        ORU_R01_ORDER_OBSERVATION obx = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION();

        // Fresh state for each invocation
        MessageState messageState = new MessageState();

        //initialize local variables
        String stateLocalID = "";
        List<ObxRepeatingElement> obxRepeatingElementArrayList = messageState.getObxRepeatingElementArrayList();
        int inv290Inv291Counter1 = 0;
        int inv290Inv291Counter2 = 0;
        int std121ObxInc = -1;
        int std121obxOrderGroupId = 0;
        int std121ObsValue = -1;
        String NBS246observationSubID = "";
        String std300 = "";
        boolean hcwTextBeforeCodedInd = false;
        String hcw = "";
        int obx2Inc = 0;
        int obx1Inc = 0;
        String OTH_COMP_TEXT = "\"\"";
        String OTH_COMP_REPLACE = "\"\"";
        String OTH_SANDS_TEXT = "\"\"";
        String OTH_SANDS_REPLACE = "\"\"";

        //set static fields
        msh.getFieldSeparator().setValue(FIELD_SEPARATOR);
        msh.getEncodingCharacters().setValue(ENCODING_CHARACTERS);
        //msh.getMessageType().getMessageCode().setValue(MESSAGE_CODE);
        //msh.getMessageType().getTriggerEvent().setValue(MESSAGE_TRIGGER_EVENT);
        pid.getSetIDPID().setValue("1");
        pid.getPatientName(0).getNameTypeCode().setValue("S");
        obr.getObr1_SetIDOBR().setValue("1");

        for (int z = 0; z < nbsnndIntermediaryMessage.getMessageElement().size(); z++){
            if (nbsnndIntermediaryMessage.getMessageElement().get(z).getQuestionIdentifierNND().equalsIgnoreCase("INV826") || nbsnndIntermediaryMessage.getMessageElement().get(z).getQuestionIdentifierNND().equalsIgnoreCase("INV827") ) {
                var test = "";
            }

            String segmentField = nbsnndIntermediaryMessage.getMessageElement().get(z).getHl7SegmentField().trim();
            if (segmentField.startsWith("MSH")){
                processMSHFields(nbsnndIntermediaryMessage.getMessageElement().get(z), msh, messageState);
            }
            else if (segmentField.startsWith("PID"))
            {
                processPIDFields(nbsnndIntermediaryMessage.getMessageElement().get(z), pid, messageState);
            }else if (segmentField.startsWith("NK1"))
            {
                processNK1Fields(nbsnndIntermediaryMessage.getMessageElement().get(z), nk1, messageState);
            }else if (segmentField.startsWith("OBR"))
            {
                processOBRFields(nbsnndIntermediaryMessage.getMessageElement().get(z), obr, messageState);
            }

            if (nbsnndIntermediaryMessage.getMessageElement().get(z).getQuestionIdentifierNND().trim().equals("STD300"))
            {
                std300 = nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getStDataType().getStringData().trim();
                if (!std300.isEmpty()){
                    std300 = std300.replace("\\","\\E\\");
                    std300 = std300.replace("|","\\F\\");
                    std300 = std300.replace("~","\\R\\");
                    std300 = std300.replace("^","\\S\\");
                    std300 = std300.replace("&","\\T\\");
                }

            }
            else if (nbsnndIntermediaryMessage.getMessageElement().get(z).getQuestionIdentifierNND().trim().equals("NBS246")){
                if (!nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweCodedValue().trim().equals("C")){
                    NBS246observationSubID = nbsnndIntermediaryMessage.getMessageElement().get(z).getObservationSubID().trim();
                }
                else {
                    NBS246observationSubID = "";
                }
            }
            else if (nbsnndIntermediaryMessage.getMessageElement().get(z).getQuestionIdentifierNND().trim().equals("223366009") && messageState.getGenericMMGv20())
            {
                String cweCodedValue = nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweCodedValue().trim();
                switch (cweCodedValue) {
                    case "Y" -> hcw = "; HCWYes";
                    case "N" -> hcw = "; HCWNo";
                    case "UNK" -> hcw = "; HCWUnknown";
                }

                if (hcwTextBeforeCodedInd) {

                    Type obxValue = obx.getOBSERVATION(messageState.getHcwObxOrderGroupId()).getOBX().getObservationValue(messageState.getHcwObx5ValueInc()).getData();
                    TX textDataType;

                    if (obxValue instanceof TX) {
                        textDataType = (TX) obxValue;
                    } else {
                        textDataType = new TX(obx.getMessage());
                    }


                    textDataType.setValue(hcw);
                    obx.getOBSERVATION(messageState.getHcwObxOrderGroupId()).getOBX().getObx5_ObservationValue(messageState.getHcwObx5ValueInc()).setData(textDataType);
                }else{
                    int obxOrderGroupId;
                    if (nbsnndIntermediaryMessage.getMessageElement().get(z).getOrderGroupId().trim().equals("1")){
                        obxOrderGroupId = 0;
                    }else{
                        obxOrderGroupId = 1;
                    }
                    obx.getOBSERVATION(1).getOBX().getSetIDOBX().setValue(String.valueOf(obx2Inc+1));
                    obx.getOBSERVATION(1).getOBX().getObservationResultStatus().setValue("F");
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getValueType().setValue("TX");
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationIdentifier().getIdentifier().setValue("77999-1");
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationIdentifier().getNameOfCodingSystem().setValue("2.16.840.1.113883.6.1");
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationIdentifier().getText().setValue("Comment");
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationIdentifier().getAlternateIdentifier().setValue("INV886");
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationIdentifier().getAlternateText().setValue("Notification Comments to CDC");
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");

                    Type obxValue = obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationValue(0).getData();
                    TX textData;

                    if (obxValue instanceof TX) {
                        textData = (TX) obxValue;
                    } else {
                        textData = new TX(obx.getMessage());
                    }

                    textData.setValue(hcw);
                    obx.getOBSERVATION(obxOrderGroupId).getOBX().getObservationValue(0).setData(textData);

                    obx2Inc +=1;
                }
            }
            //STD specific code for combining STD121
            else if (nbsnndIntermediaryMessage.getMessageElement().get(z).getQuestionIdentifierNND().trim().equals("STD121"))
            {
                if (std121ObxInc==-1){
                    if (nbsnndIntermediaryMessage.getMessageElement().get(z).getOrderGroupId().trim().equals("1")){
                        std121ObxInc = obx1Inc;
                        std121obxOrderGroupId = 0;
                        messageState.setObx1Inc(obx1Inc++);
                    }else{
                        std121ObxInc = obx2Inc;
                        std121obxOrderGroupId = 1;
                        messageState.setObx2Inc(obx2Inc++);
                    }
                }
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getSetIDOBX().setValue(String.valueOf(std121ObxInc+1));
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getValueType().setValue("CWE");
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationIdentifier().getIdentifier().setValue(nbsnndIntermediaryMessage.getMessageElement().get(z).getQuestionIdentifierNND().trim());
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationIdentifier().getNameOfCodingSystem().setValue(nbsnndIntermediaryMessage.getMessageElement().get(z).getQuestionOID().trim());
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationIdentifier().getText().setValue(nbsnndIntermediaryMessage.getMessageElement().get(z).getQuestionLabelNND().trim());
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationIdentifier().getAlternateIdentifier().setValue(nbsnndIntermediaryMessage.getMessageElement().get(z).getQuestionIdentifier().trim());
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationIdentifier().getAlternateText().setValue(nbsnndIntermediaryMessage.getMessageElement().get(z).getQuestionLabelNND().trim());
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");
                if (!nbsnndIntermediaryMessage.getMessageElement().get(z).getObservationSubID().trim().isEmpty()){
                    obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationSubID().setValue(nbsnndIntermediaryMessage.getMessageElement().get(z).getObservationSubID().trim());
                }else if (!nbsnndIntermediaryMessage.getMessageElement().get(z).getQuestionGroupSeqNbr().trim().isEmpty()){
                    obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationSubID().setValue(nbsnndIntermediaryMessage.getMessageElement().get(z).getQuestionGroupSeqNbr().trim());
                }

                std121ObsValue += 1;
                String codedValue = "";
                String codedValueDescription = "";
                String codedValueCodingSystem = "";
                String localCodedValue = "";
                String localCodedValueDescription = "";
                String localCodedValueCodingSystem = "";
                String originalOtherText = "";
                if (!nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweCodedValue().trim().isEmpty()){
                    codedValue = nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweCodedValue().trim();
                }
                if (!nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweCodedValueDescription().trim().isEmpty()){
                    codedValueDescription = nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweCodedValue().trim();
                }
                if (!nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweCodedValueCodingSystem().trim().isEmpty()){
                    codedValueCodingSystem = nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweCodedValueCodingSystem().trim();
                }
                if (!nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweLocalCodedValue().trim().isEmpty()){
                    localCodedValue = nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweLocalCodedValue().trim();
                }
                if (!nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweLocalCodedValueDescription().trim().isEmpty()){
                    localCodedValueDescription = nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweLocalCodedValueDescription().trim();
                }
                if (!nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem().trim().isEmpty()){
                    localCodedValueCodingSystem = nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem().trim();
                }
                if (!nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweOriginalText().trim().isEmpty()){
                    originalOtherText = nbsnndIntermediaryMessage.getMessageElement().get(z).getDataElement().getCweDataType().getCweOriginalText().trim();
                }
                Type obxValue = obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationValue(std121ObsValue).getData();
                TX textData;

                if (obxValue instanceof TX) {
                    textData = (TX) obxValue;
                } else {
                    textData = new TX(obx.getMessage());
                }



                String value = codedValue + "^"+codedValueDescription+"^"+codedValueCodingSystem+"^"+localCodedValue+"^"+
                        localCodedValueDescription+"^" +localCodedValueCodingSystem+"^"+originalOtherText;
                textData.setValue(value);
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationValue(std121ObsValue).setData(textData);
                obx.getOBSERVATION(std121obxOrderGroupId).getOBX().getObservationResultStatus().setValue("F");

            }
            else if(nbsnndIntermediaryMessage.getMessageElement().get(z).getIndicatorCd() != null && nbsnndIntermediaryMessage.getMessageElement().get(z).getIndicatorCd().contains("ParentRepeatBlock"))
            {
                mapToDynamicParentRptToRpt.mapToDynamicParentRptToRpt(nbsnndIntermediaryMessage.getMessageElement().get(z),  obx2Inc, messageState.getMessageType(), oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0));
            }
            else if(nbsnndIntermediaryMessage.getMessageElement().get(z).getIndicatorCd() != null && nbsnndIntermediaryMessage.getMessageElement().get(z).getIndicatorCd().contains("DiscAsRepeat"))
            {
                mapToDynamicRootlDiscToRepeat.mapToDynamicRootlDiscToRepeat(nbsnndIntermediaryMessage.getMessageElement().get(z), obx2Inc, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0));
            }
            else if(nbsnndIntermediaryMessage.getMessageElement().get(z).getIndicatorCd() != null && nbsnndIntermediaryMessage.getMessageElement().get(z).getIndicatorCd().contains("DiscCdToMultiOBS"))
            {
                mapToDisRepeat.mapToDisRepeat(nbsnndIntermediaryMessage.getMessageElement().get(z), obx2Inc, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0));
            }
            else if(nbsnndIntermediaryMessage.getMessageElement().get(z).getIndicatorCd() != null && nbsnndIntermediaryMessage.getMessageElement().get(z).getIndicatorCd().contains("RepeatToMultiNND"))
            {
                mapToRepeatToMultiNND.mapToRepeatToMultiNND(nbsnndIntermediaryMessage.getMessageElement().get(z), obx2Inc, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0));
            }
            else if (
                    (nbsnndIntermediaryMessage.getMessageElement().get(z).getHl7SegmentField().equals("OBX-3.0")
                            || nbsnndIntermediaryMessage.getMessageElement().get(z).getHl7SegmentField().equals("OBX-5.9"))
                            && nbsnndIntermediaryMessage.getMessageElement().get(z).getQuestionMap() != null
                            && nbsnndIntermediaryMessage.getMessageElement().get(z).getQuestionMap().contains("|")
            )
            {
                mapToQuestionMap.mapToQuestionMap(nbsnndIntermediaryMessage.getMessageElement().get(z), obx2Inc, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0));
            }
            else if (segmentField.startsWith("OBX"))
            {
                processOBXFields(nbsnndIntermediaryMessage.getMessageElement().get(z),oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0), messageState);
            }

            if(messageState.getMessageType().contains("Arbo_Case_Map_v1.0") && messageState.isDefaultNull() && !stateLocalID.isEmpty())
            {
                // Pushing this down to the last index
                if (z == nbsnndIntermediaryMessage.getMessageElement().size() - 1) {
                    OBX obxForArboCaseMapV1 = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBSERVATION(obx.getOBSERVATIONAll().size()).getOBX();
                    var obxElement = new ObxRepeatingElement();
                    obxElement.setElementUid("INV173");
                    obxRepeatingElementArrayList.add(obxElement);
                    obxForArboCaseMapV1.getObx1_SetIDOBX().setValue(String.valueOf(obxRepeatingElementArrayList.size()));
                    obxForArboCaseMapV1.getValueType().setValue("ST");
                    obxForArboCaseMapV1.getObservationIdentifier().getIdentifier().setValue("INV173");
                    obxForArboCaseMapV1.getObservationIdentifier().getText().setValue("State Case ID");
                    obxForArboCaseMapV1.getObservationIdentifier().getNameOfCodingSystem().setValue("2.16.840.1.114222.4.5.232");

                    obxForArboCaseMapV1.getObservationIdentifier().getAlternateIdentifier().setValue("INV173");
                    obxForArboCaseMapV1.getObservationIdentifier().getAlternateText().setValue("State Case ID");
                    obxForArboCaseMapV1.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");
                    // obxForArboCaseMapV1.getObservationIdentifier().getIdentifier().setValue(stateLocalID);
                    Varies value = obxForArboCaseMapV1.getObservationValue(0);
                    ST st = new ST(oruMessage);
                    st.setValue(stateLocalID);
                    value.setData(st);

                    obxForArboCaseMapV1.getObservationResultStatus().setValue("F");
                }
            }

            /* This code will cover the situation outside TB investigation (In TB question_identifer='INV121'
           and question_identifier_nnd='INV177' and question is populated from frontend)
           where INV177 is not in the intermediate message, this is applicable to only V2 Pages. Excludes Arbo, Gen V1, varicella*/
            if (!messageState.isInv177Found() && messageState.getInv177Date() != null && !messageState.getInv177Date().isEmpty()
                    && !messageState.getMessageType().contains("TB_Case_Map_v2.0")
                    && !messageState.getMessageType().contains("Arbo_Case_Map_v1.0")
                    && !messageState.getMessageType().contains("Gen_Case_Map_v1.0")
                    && !messageState.getMessageType().contains("Var_Case_Map_v2.0"))
            {
                // Pushing this down to the last index
                if (z == nbsnndIntermediaryMessage.getMessageElement().size() - 1) {
                    OBX obxForGenV2 = obx.getOBSERVATION(obx.getOBSERVATIONAll().size()).getOBX();

                    var obxElement = new ObxRepeatingElement();
                    obxElement.setElementUid("77970-2");
                    obxRepeatingElementArrayList.add(obxElement);
                    messageState.setObx2Inc(messageState.getObxInc() + 1);
                    obxForGenV2.getObx1_SetIDOBX().setValue(String.valueOf(messageState.getObxInc()));

                    obxForGenV2.getValueType().setValue("DT");
                    obxForGenV2.getObservationResultStatus().setValue("F");
                    obxForGenV2.getObservationIdentifier().getIdentifier().setValue("77970-2");
                    obxForGenV2.getObservationIdentifier().getText().setValue("Date First Reported PHD");
                    obxForGenV2.getObservationIdentifier().getNameOfCodingSystem().setValue("2.16.840.1.113883.6.1");
                    obxForGenV2.getObservationIdentifier().getAlternateIdentifier().setValue("INV177");
                    obxForGenV2.getObservationIdentifier().getAlternateText().setValue("Date First Reported PHD");
                    obxForGenV2.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");

                    // out.PATIENT_RESULT.ORDER_OBSERVATION[0].OBSERVATION[1].OBX[obx2Inc].ObservationValue[0] = inv177Date;
                    DT dt = new DT(obxForGenV2.getMessage());
                    dt.setValue(messageState.getInv177Date());
                    obxForGenV2.getObservationValue(0).setData(dt);
//                    inv177Date = "";
                }
            }

            /*This code should execute for Gen V1 guides, excludes varicella and Arbo*/
            if (!messageState.isInv177Found() && messageState.getInv177Date() != null && !messageState.getInv177Date().isEmpty() && messageState.getMessageType().startsWith("Gen_Case_Map_v1.0"))
            {
                // Pushing this down to the last index
                if (z == nbsnndIntermediaryMessage.getMessageElement().size() - 1) {
                    OBX obxForGenV1 = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBSERVATION(1).getOBX();

                    messageState.setObx2Inc(messageState.getObxInc() + 1);
                    obxForGenV1.getObx1_SetIDOBX().setValue(String.valueOf(messageState.getObxInc()));
                    obxForGenV1.getValueType().setValue("TS");
                    obxForGenV1.getObservationResultStatus().setValue("F");

                    obxForGenV1.getObservationIdentifier().getIdentifier().setValue("INV177");
                    obxForGenV1.getObservationIdentifier().getText().setValue("Date First Reported PHD");
                    obxForGenV1.getObservationIdentifier().getNameOfCodingSystem().setValue("2.16.840.1.114222.4.5.232");

                    obxForGenV1.getObservationIdentifier().getAlternateIdentifier().setValue("INV177");
                    obxForGenV1.getObservationIdentifier().getAlternateText().setValue("Date First Reported PHD");
                    obxForGenV1.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");

                    Varies value = obxForGenV1.getObservationValue(0);
                    TS ts = new TS(oruMessage);
                    ts.getTime().setValue(messageState.getInv177Date() + "000000.000"); //for TS18 format
                    value.setData(ts);
                }
            }

            if ("NND_ORU_v2.0".equals(messageState.getNndMessageVersion())) {
                OBR obrForNND_ORU_v2 = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(0).getOBR();
                obrForNND_ORU_v2.getSetIDOBR().setValue("2");

                obrForNND_ORU_v2.getFillerOrderNumber().getEntityIdentifier().setValue(messageState.getEntityIdentifier2());
                obrForNND_ORU_v2.getFillerOrderNumber().getNamespaceID().setValue(messageState.getFillerOrderNumberNameSpaceIDGroup2());
                obrForNND_ORU_v2.getFillerOrderNumber().getUniversalID().setValue(messageState.getFillerOrderNumberUniversalID2());
                obrForNND_ORU_v2.getFillerOrderNumber().getUniversalIDType().setValue(messageState.getFillerOrderNumberUniversalIDType2());

                obrForNND_ORU_v2.getUniversalServiceIdentifier().getIdentifier().setValue(messageState.getUniversalServiceIdentifierGroup1());
                obrForNND_ORU_v2.getUniversalServiceIdentifier().getText().setValue(messageState.getUniversalServiceIDTextGroup1());
                obrForNND_ORU_v2.getUniversalServiceIdentifier().getNameOfCodingSystem().setValue(messageState.getUniversalServiceIDNameOfCodingSystemGroup1());

                obrForNND_ORU_v2.getUniversalServiceIdentifier().getIdentifier().setValue(messageState.getUniversalServiceIdentifierGroup2());
                obrForNND_ORU_v2.getUniversalServiceIdentifier().getText().setValue(messageState.getUniversalServiceIDTextGroup1());
                obrForNND_ORU_v2.getUniversalServiceIdentifier().getNameOfCodingSystem().setValue(messageState.getUniversalServiceIDNameOfCodingSystemGroup2());

                // TODO - Validate these two date values
                String dateFormatForObr7 = dateFormatUtil.formatDate(
                        messageState.getObservationDateTime(),
                        messageState.getObr7QuestionDataTypeNND(),
                        messageState.getObr7DataType(),
                        "OBR-7.0"
                );
                obr.getObr7_ObservationDateTime().getTime().setValue(dateFormatForObr7);

                String dateFormatForObr22 = dateFormatUtil.formatDate(
                        messageState.getResultStatusChgTime(),
                        messageState.getObr7QuestionDataTypeNND(),
                        messageState.getObr7DataType(),
                        "OBR22.0"
                );
                obr.getObr22_ResultsRptStatusChngDateTime().getTime().setValue(dateFormatForObr22);

                obrForNND_ORU_v2.getReasonForStudy(0).getIdentifier().setValue(messageState.getReasonForStudyIdentifier2());
                obrForNND_ORU_v2.getReasonForStudy(0).getText().setValue(messageState.getReasonForStudyText2());
                obrForNND_ORU_v2.getReasonForStudy(0).getNameOfCodingSystem().setValue(messageState.getReasonForStudyNameOfCodingSystem2());
            }
        }

        // Process MSH21 field
        if (messageState.getIsSingleProfile()) {
            mshSegmentBuilder.setSingleProfileMSH21(msh, messageState);
        } else {
            mshSegmentBuilder.setMultiProfileMSH21(msh, messageState);
        }

        int	labObrCounter = 1;
        EIElement eiType = new EIElement();

        ParentLink parentLink = new ParentLink();
        String cachedOBX3data = "";

        for (LabReportEvent labReportEvent: nbsnndIntermediaryMessage.getLabReportEvent()){
            int obrCounter = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATIONReps();
            labObrCounter = obrCounter + 1;
            int obxSubIdCounter = 1;

            mapLabReportEventToOBR.mapLabReportEventToOBR(
                    nbsnndIntermediaryMessage,
                    labReportEvent,
                    obrCounter,
                    labObrCounter,
                    messageState.getMessageType(),
                    0,
                    eiType,
                    parentLink,
                    obxSubIdCounter,
                    cachedOBX3data,
                    oruMessage
            );
        }

        for(int i = 0; i < oruMessage.getPATIENT_RESULT().getORDER_OBSERVATIONReps(); i++) {
            for(int j = 0; j < oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATIONReps(); j++) {
                String alternateIdentifier = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getAlternateIdentifier().getValue();

                if(messageState.getMessageType().contains("CongenitalSyphilis_MMG_V1.0")) {
                    Set<String> validAlternateIds = Set.of("LAB588", "INV290", "INV291", "STD123", "LAB167", "STD123_1");

                    if (validAlternateIds.contains(alternateIdentifier)) {
                        int subIdCounter;
                        try {
                            subIdCounter = Integer.parseInt(oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationSubID().getValue());
                        } catch (NumberFormatException e) {
                            // TODO check this value
                            subIdCounter = -1;
                        }
                        if (subIdCounter < 0) {
                            int newSubIdCounter = -subIdCounter;
                            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationSubID().setValue(Integer.toString(messageState.getDupRepeatCongenitalCounter() + newSubIdCounter));
                        }
                    }
                    else {
                        if("STD122".equals(alternateIdentifier) || "STD123".equals(alternateIdentifier) || "STD126".equals(alternateIdentifier)) {
                            if (inv290Inv291Counter1 == 0) {
                                int incCounter = messageState.getInv290Inv291Counter() + 1;
                                messageState.setInv290Inv291Counter(incCounter);
//                                inv290Inv291Counter++;
                                inv290Inv291Counter1 = incCounter;
                            }
                            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationSubID().setValue(Integer.toString(inv290Inv291Counter1));
                        } else if ("STD124".equals(alternateIdentifier) || "STD125".equals(alternateIdentifier)) {
                            if (inv290Inv291Counter2 == 0) {
                                int incCounter = messageState.getInv290Inv291Counter() + 1;
                                messageState.setInv290Inv291Counter(incCounter);
//                                inv290Inv291Counter++;
                                inv290Inv291Counter2 = incCounter;
                            }
                            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationSubID().setValue(Integer.toString(inv290Inv291Counter2));
                        } else if ("STD121".equals(alternateIdentifier)) {
                            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationSubID().setValue("");
                        }
                    }

                    String obxValue = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(0).toString();
                    String obxIdIdentifier = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationIdentifier().getIdentifier().getValue();
                    Type stobxValue = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(0).getData();
                    ST stType;

                    if (stobxValue instanceof ST) {
                        stType = (ST) stobxValue;
                    } else {
                        stType = new ST(oruMessage);
                    }

                    Type stobxValue2 = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(1).getData();
                    ST stType1;

                    if (stobxValue2 instanceof ST) {
                        stType1 = (ST) stobxValue2;
                    } else {
                        stType1 = new ST(oruMessage);
                    }

                    Type stobxValue3 = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(1).getData();
                    ST stType2;

                    if (stobxValue3 instanceof ST) {
                        stType2 = (ST) stobxValue3;
                    } else {
                        stType2 = new ST(oruMessage);
                    }

                    int observationValue1Size = stType2.getValue() != null ? stType2.getValue().length() : 0;


                    if(obxValue != null && obxValue.contains("Other Drugs Used^2.16.840.1.114222.4.5.274") && std300 != null && !std300.isEmpty()) {
                        stType.setValue(obxValue + "^^^^^^" + std300);
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(0).setData(stType);
                    } else if ("67187-5".equals(obxIdIdentifier) && obxValue != null && obxValue.contains(OTH_COMP_REPLACE)) {
                        if (OTH_COMP_TEXT != null && !OTH_COMP_TEXT.isEmpty()) {
                            stType.setValue(OTH_COMP_REPLACE + "^^^^^^" + OTH_COMP_TEXT);
                            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(0).setData(stType);
                        } else {
                            stType.setValue("OTH^other^2.16.840.1.113883.5.1008");
                            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(0).setData(stType);
                        }
                        stType1.setValue("");
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(1).setData(stType1);
                    } else if ("56831-1".equals(obxIdIdentifier) && obxValue != null && obxValue.contains(OTH_SANDS_REPLACE)) {
                        stType.setValue(OTH_SANDS_REPLACE + "^^^^^^" + OTH_SANDS_TEXT);
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(0).setData(stType);

                        stType1.setValue("");
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(1).setData(stType1);
                    } else if ("56831-1".equals(obxIdIdentifier) && observationValue1Size > 0) {
                        stType1.setValue("");
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(i).getOBSERVATION(j).getOBX().getObservationValue(1).setData(stType1);
                    }
                }
            }
        }

        if (validationEnabled) {
            Hl7Validator validator = new Hl7Validator();
            boolean isHL7Valid = validator.nndOruR01Validator(oruMessage);
        }

        logger.info("Final message: {} ", oruMessage);

        // based on message type
//        String base64EncodedString = encodeToBase64(oruMessage.getMessage().toString());
        //based on the message type and

        //TODO - connector.persistNotification(base64EncodedString,Constants.NETSS_TRANSPORT_Q_OUT_TABLE);

        //logger.info("SB: {}", completedObxString.toString());
        return oruMessage.toString();
    }

    /**
     * Processes each field of the MSH segment found in the  XML file.
     * This method extracts the relevant data from the MessageElement and
     * updates the MSH object.
     *
     * @param messageElement The XML element representing a specific MSH field,
     *                       including its attributes and values.
     * @param msh The MSH object that is being built, which will be updated with
     *            data from the provided messageElement.
     * @param messageState The per-invocation message state.
     */

    private void processMSHFields(MessageElement messageElement, MSH msh, MessageState messageState) throws DataTypeException {
        mshSegmentBuilder.processMSHFields(messageElement, msh, messageState);
    }
    /**
     * Processes each field of the PID segment found in the  XML file.
     * This method extracts the relevant data from the MessageElement and
     * updates the PID object.
     *
     * @param messageElement The XML element representing a specific PID field,
     *                       including its attributes and values.
     * @param pid The PID object that is being built, which will be updated with
     *            data from the provided messageElement.
     * @param messageState The per-invocation message state.
     */
    private void processPIDFields(MessageElement messageElement, PID pid, MessageState messageState) throws DataTypeException {
        pidSegmentBuilder.processPIDFields(messageElement, pid, messageState);
    }
    /**
     * Processes each field of the NK1 segment found in the  XML file.
     * This method extracts the relevant data from the MessageElement and
     * updates the NK1 object.
     *
     * @param messageElement The XML element representing a specific NK1 field,
     *                       including its attributes and values.
     * @param nk1 The NK1 object that is being built, which will be updated with
     *            data from the provided messageElement.
     * @param messageState The per-invocation message state.
     */
    private void processNK1Fields(MessageElement messageElement, NK1 nk1, MessageState messageState) throws DataTypeException {
        nk1SegmentBuilder.processNK1Fields(messageElement, nk1, messageState);
    }
    /**
     * Processes each field of the OBR segment found in the  XML file.
     * This method extracts the relevant data from the MessageElement and
     * updates the OBR object.
     *
     * @param messageElement The XML element representing a specific OBR field,
     *                       including its attributes and values.
     * @param obr The OBR object that is being built, which will be updated with
     *            data from the provided messageElement.
     * @param messageState The per-invocation message state.
     */
    private void processOBRFields(MessageElement messageElement, OBR obr, MessageState messageState) throws DataTypeException {
        obrSegmentBuilder.processOBRFields(messageElement, obr, messageState);
    }

    /**
     * Processes each field of the OBX segment found in the  XML file.
     * This method extracts the relevant data from the MessageElement and
     * updates the OBX object.
     *
     * @param messageElement   The XML element representing a specific OBX field,
     *                         including its attributes and values.
     * @param orderObservation The OBX object that is being built, which will be updated with
     *                         data from the provided messageElement.
     * @param messageState The per-invocation message state.
     */
    private void processOBXFields(MessageElement messageElement, ORU_R01_ORDER_OBSERVATION orderObservation, MessageState messageState) throws HL7Exception {
        obxSegmentBuilder.processOBXFields(messageElement, orderObservation, messageState);
    }
}
