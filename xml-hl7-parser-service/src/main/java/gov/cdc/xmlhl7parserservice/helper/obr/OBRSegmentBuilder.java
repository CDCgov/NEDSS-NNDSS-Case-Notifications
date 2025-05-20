package gov.cdc.xmlhl7parserservice.helper.obr;

import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.DataTypeException;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import gov.cdc.xmlhl7parserservice.helper.MessageState;
import gov.cdc.xmlhl7parserservice.util.HL7DateFormatUtil;
import gov.cdc.xmlhl7parserservice.repository.msgout.IServiceActionPairRepository;
import gov.cdc.xmlhl7parserservice.repository.msgout.model.ServiceActionPairModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.Optional;

@Component
public class OBRSegmentBuilder {
    private static final Logger logger = LoggerFactory.getLogger(OBRSegmentBuilder.class);
    
    private final MessageState messageState;
    private final HL7DateFormatUtil dateFormatUtil;
    private final IServiceActionPairRepository iServiceActionPairRepository;

    public OBRSegmentBuilder(MessageState messageState, HL7DateFormatUtil dateFormatUtil, IServiceActionPairRepository iServiceActionPairRepository) {
        this.messageState = messageState;
        this.dateFormatUtil = dateFormatUtil;
        this.iServiceActionPairRepository = iServiceActionPairRepository;
    }

    public void processOBRFields(MessageElement messageElement, OBR obr) throws DataTypeException {
        String obrField = messageElement.getHl7SegmentField().trim();
        String orderGroupID = messageElement.getOrderGroupId();
        String questionDataTypeNND = messageElement.getDataElement().getQuestionDataTypeNND().trim();
        String questionIdentifierNND = messageElement.getQuestionIdentifierNND().trim();

        if (obrField.startsWith("OBR-3.1")){
            messageState.setEntityIdentifier2(messageElement.getDataElement().getStDataType().getStringData().trim());
            obr.getObr3_FillerOrderNumber().getEntityIdentifier().setValue(messageState.getEntityIdentifier2());
        }else if (obrField.startsWith("OBR-3.2") && Objects.equals(orderGroupID, "1")) {
            obr.getObr3_FillerOrderNumber().getNamespaceID().setValue(messageElement.getDataElement().getIsDataType().getIsCodedValue());
        }else if (obrField.startsWith("OBR-3.2") && Objects.equals(orderGroupID, "2")){
            messageState.setFillerOrderNumberNameSpaceIDGroup2(messageElement.getDataElement().getIsDataType().getIsCodedValue());
        }else if (obrField.startsWith("OBR-3.3") && Objects.equals(orderGroupID, "2")){
            obr.getObr3_FillerOrderNumber().getUniversalID().setValue(messageElement.getDataElement().getStDataType().getStringData());
            messageState.setFillerOrderNumberUniversalID2(messageElement.getDataElement().getStDataType().getStringData());
        }else if (obrField.startsWith("OBR-3.4") && Objects.equals(orderGroupID, "2")){
            obr.getObr3_FillerOrderNumber().getUniversalIDType().setValue(messageElement.getDataElement().getIdDataType().getIdCodedValue());
            messageState.setFillerOrderNumberUniversalIDType2(messageElement.getDataElement().getIdDataType().getIdCodedValue());
        }else if (obrField.startsWith("OBR-4.1") && Objects.equals(orderGroupID, "1")) {
            obr.getObr4_UniversalServiceIdentifier().getIdentifier().setValue("68991-9");
            messageState.setUniversalServiceIdentifierGroup1(messageState.getEntityIdentifier2());
        }else if (obrField.startsWith("OBR-4.1") && Objects.equals(orderGroupID, "2")){
            messageState.setUniversalServiceIdentifierGroup2(messageState.getEntityIdentifier2());
        }else if (obrField.startsWith("OBR-4.2") && Objects.equals(orderGroupID, "1")){
            obr.getObr4_UniversalServiceIdentifier().getText().setValue("Epidemiologic Information");
            messageState.setUniversalServiceIDTextGroup1(messageState.getEntityIdentifier2());
        }else if (obrField.startsWith("OBR-4.2") && Objects.equals(orderGroupID, "2")){
            messageState.setUniversalServiceIdentifierGroup2(messageState.getEntityIdentifier2());
        }else if (obrField.startsWith("OBR-4.3") && Objects.equals(orderGroupID, "1")){
            obr.getObr4_UniversalServiceIdentifier().getNameOfCodingSystem().setValue("LN");
            messageState.setUniversalServiceIDNameOfCodingSystemGroup1(messageElement.getDataElement().getIdDataType().getIdCodedValue());
        }else if (obrField.startsWith("OBR-4.3") && Objects.equals(orderGroupID, "2")){
            messageState.setUniversalServiceIDNameOfCodingSystemGroup2(messageElement.getDataElement().getIdDataType().getIdCodedValue());
        }else if (obrField.startsWith("OBR-7.0")){
            messageState.setObservationDateTime(messageElement.getDataElement().getTsDataType().getTime().toString().trim());
            messageState.setObr7(messageElement.getHl7SegmentField().trim());
            messageState.setObr7DataType(messageElement.getDataElement().getQuestionDataTypeNND().trim());
            messageState.setObr7QuestionDataTypeNND(messageElement.getQuestionIdentifierNND());
            String dateFormat = dateFormatUtil.formatDate(messageState.getObservationDateTime(), questionDataTypeNND, questionIdentifierNND, "OBR-7.0");
            obr.getObr7_ObservationDateTime().getTime().setValue(dateFormat);
        }else if (obrField.startsWith("OBR-22.0")){
            messageState.setResultStatusChgTime(messageElement.getDataElement().getTsDataType().getTime().toString().trim());
            String dateFormat = dateFormatUtil.formatDate(messageState.getResultStatusChgTime(), questionDataTypeNND, questionIdentifierNND, "OBR-22.0");
            obr.getObr22_ResultsRptStatusChngDateTime().getTime().setValue(dateFormat);
        }else if (obrField.startsWith("OBR-25.0")){
            obr.getObr25_ResultStatus().setValue(messageElement.getDataElement().getIdDataType().getIdCodedValue());
        }else if (obrField.startsWith("OBR-31.0")){
            String conditionCode = messageElement.getDataElement().getCeDataType().getCeCodedValue().trim();

            String service = "";
            String action = "";
            String serviceActionConditionCode = "";
            String serviceActionConceptCode = "";

            Optional<ServiceActionPairModel> serviceActionPair = iServiceActionPairRepository.findByMessageProfileId(messageState.getMessageType());
            if(serviceActionPair.isPresent()) {
                service = serviceActionPair.get().getService();
                action = serviceActionPair.get().getAction();
                serviceActionConditionCode = serviceActionPair.get().getConditionCode();
                serviceActionConceptCode = serviceActionPair.get().getConceptCode();
            }

            if (service == null || service.isEmpty() || action == null || action.isEmpty()) {
                logger.error("ERROR: There is no default SERVICE/ACTION pair defined in the SERVICE_ACTION_PAIR lookup for {} {}, which has a message profile ID of {} and condition Code of {}",
                        messageState.getEntityIdentifier2(), messageState.getNndMessageVersion(), messageState.getMessageType(), conditionCode);
                obr.getObr31_ReasonForStudy(0).getIdentifier().setValue(conditionCode);
            } else if (serviceActionConditionCode != null && !serviceActionConditionCode.isEmpty()
                    && (serviceActionConceptCode == null || serviceActionConceptCode.isEmpty())) {
                logger.error("ERROR: There is no default CONCEPT_CODE defined in the SERVICE_ACTION_PAIR lookup for {} {}, which has a message profile ID {}. Please populate CONCEPT_CODE column for the condition code",
                        messageState.getEntityIdentifier2(), messageState.getNndMessageVersion(), messageState.getMessageType());
                obr.getObr31_ReasonForStudy(0).getIdentifier().setValue(conditionCode);
            } else if (serviceActionConceptCode != null && !serviceActionConceptCode.isEmpty()) {
                obr.getObr31_ReasonForStudy(0).getIdentifier().setValue(serviceActionConceptCode);
            } else {
                obr.getObr31_ReasonForStudy(0).getIdentifier().setValue(conditionCode);
            }

            //update other fields
            obr.getObr31_ReasonForStudy(0).getText().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
            obr.getObr31_ReasonForStudy(0).getNameOfCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem());
            obr.getObr31_ReasonForStudy(0).getAlternateIdentifier().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValue());
            obr.getObr31_ReasonForStudy(0).getAlternateText().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
            obr.getObr31_ReasonForStudy(0).getNameOfAlternateCodingSystem().setValue(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem());

            messageState.setReasonForStudyIdentifier2(messageElement.getDataElement().getCeDataType().getCeCodedValue());
            messageState.setReasonForStudyText2(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
            messageState.setReasonForStudyNameOfCodingSystem2(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem());
            messageState.setReasonForStudyAlternateIdentifier2(messageElement.getDataElement().getCeDataType().getCeLocalCodedValue());
            messageState.setReasonForStudyAlternateText2(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueDescription());
            messageState.setReasonForStudyNameOfAlternateCodingSystem2(messageElement.getDataElement().getCeDataType().getCeLocalCodedValueCodingSystem());
        }
    }
} 