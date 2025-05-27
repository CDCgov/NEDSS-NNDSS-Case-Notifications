package gov.cdc.xmlhl7parserservice.helper.mapper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.datatype.EI;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import gov.cdc.xmlhl7parserservice.helper.MessageState;
import gov.cdc.xmlhl7parserservice.model.EIElement;
import gov.cdc.xmlhl7parserservice.model.ParentLink;
import gov.cdc.xmlhl7parserservice.model.ResultMethod;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.LabReportEvent;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.NBSNNDIntermediaryMessage;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class MapLabReportEventToOBR {
    private final MapToEIType mapToEIType;
    private final MapToCEType mapToCEType;
    private final MapToCWEType mapToCWEType;
    private final MapToTS18DataType mapToTS18DataType;
    private final MapResultedTestToOBX mapResultedTestToOBX;
    private final MapToDTM18 mapToDTM18;
    MessageState messageState;

    public MapLabReportEventToOBR(MapToEIType mapToEIType, MapToCEType mapToCEType, MapToCWEType mapToCWEType, MapToTS18DataType mapToTS18DataType, MapResultedTestToOBX mapResultedTestToOBX, MapToDTM18 mapToDTM18) {
        this.mapToEIType = mapToEIType;
        this.mapToCEType = mapToCEType;
        this.mapToCWEType = mapToCWEType;
        this.mapToTS18DataType = mapToTS18DataType;
        this.mapResultedTestToOBX = mapResultedTestToOBX;
        this.mapToDTM18 = mapToDTM18;
    }

    public void mapLabReportEventToOBR(NBSNNDIntermediaryMessage nbsnndIntermediaryMessage, LabReportEvent labReportEvent, int obrCounter, int labObrCounter, String messageType, int i, EIElement eiElement, ParentLink parentLink, int obxSubidCounter, String cachedOBX3data, ORU_R01 oruMessage) throws HL7Exception {
        int labSubCounter = 0;
        int specimenCounter= 1;
        int isValidSPM=0;
        String resultStatus="";
        int reasonForStudyCounter=0;
        EIElement eiType = new EIElement();
        cachedOBX3data="";

        for (MessageElement messageElement: nbsnndIntermediaryMessage.getMessageElement()) {
            String questionDataTypeNND = messageElement.getDataElement().getQuestionDataTypeNND().trim();
            String questionIdentifierNND = messageElement.getQuestionIdentifierNND();
            String hl7Field = messageElement.getHl7SegmentField().trim();
            if (hl7Field.startsWith("OBR-")) {
                String dataElement = hl7Field.replace("OBR-", "");

                if (dataElement.startsWith("1.")) {
                } else if (dataElement.startsWith("2.")) {
                    mapToEIType.mapToEIType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getPlacerOrderNumber());
                } else if (dataElement.startsWith("3.")) {
                    EI fillerOrder = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getFillerOrderNumber();
                    mapToEIType.mapToEIType(messageElement, fillerOrder);

                    // TODO - Verify if it is okay to wrap it as string
                    eiType.setEntityIdentifier(String.valueOf(fillerOrder.getEntityIdentifier()));
                    eiType.setNamespaceID(String.valueOf(fillerOrder.getNamespaceID()));
                    eiType.setUniversalID(String.valueOf(fillerOrder.getUniversalID()));
                    eiType.setUniversalIDType(String.valueOf(fillerOrder.getUniversalIDType()));
                } else if (dataElement.startsWith("4.")) {
                    mapToCEType.mapToCEType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getUniversalServiceIdentifier());
                } else if (dataElement.startsWith("5.")) {
                } else if (dataElement.startsWith("6.")) {
                } else if (dataElement.startsWith("7.")) {
                    mapToTS18DataType.mapToTS18DataType(messageElement.getDataElement().getTsDataType().getTime().toString(), oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getObservationDateTime());
                } else if (dataElement.startsWith("14.")) {
                } else if (dataElement.startsWith("16.")) {
                } else if (dataElement.startsWith("22.")) {
                    mapToTS18DataType.mapToTS18DataType(messageElement.getDataElement().getTsDataType().getTime().toString(), oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getResultsRptStatusChngDateTime());
                } else if (dataElement.startsWith("25.")) {
                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getResultStatus().setValue(messageElement.getDataElement().getIdDataType().getIdCodedValue());
                } else if (dataElement.startsWith("31.")) {
                    mapToCEType.mapToCEType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getReasonForStudy(reasonForStudyCounter));
                    reasonForStudyCounter++;
                } else if (dataElement.startsWith("35.")) {
                    // mapToCEType(messageElement, out.getRace().get(raceCounterNK1));
                    // raceCounterNK1++;
                }
            } else if (hl7Field.startsWith("SPM-")) {
                String dataElement = hl7Field.replace("SPM-", "");

                if (dataElement.startsWith("1.")) {
                } else if (dataElement.startsWith("2.1")) {
                    mapToEIType.mapToEIType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenID().getPlacerAssignedIdentifier());
                } else if (dataElement.startsWith("2.2")) {
                    mapToEIType. mapToEIType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenID().getFillerAssignedIdentifier());
                } else if (dataElement.startsWith("4.")) {
                    mapToCWEType.mapToCWEType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenType());
                    isValidSPM = 1;
                } else if (dataElement.startsWith("8")) {
                    mapToCWEType.mapToCWEType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenSourceSite());
                } else if (dataElement.startsWith("11")) {
                } else if (dataElement.startsWith("12.0")) {
                    String quantity = messageElement.getDataElement().getNmDataType().getNum();
                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenCollectionAmount().getQuantity().setValue(quantity);
                } else if (dataElement.startsWith("12.1")) {
                } else if (dataElement.startsWith("12.2")) {
                    mapToCEType.mapToCEType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenCollectionAmount().getUnits());
                } else if (dataElement.startsWith("14")) {
                    String desc = messageElement.getDataElement().getStDataType().getStringData();
                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenDescription(0).setValue(desc);
                } else if (dataElement.startsWith("17")) {
                    mapToTS18DataType.mapToTS18DataType(messageElement.getDataElement().getTsDataType().getTime().toString(), oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSpecimenCollectionDateTime().getRangeStartDateTime());
                } else if (dataElement.startsWith("18")) {
                }
            }
            if (isValidSPM == 0) {
                // TODO - this needs to be validated
                oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).removeOBX(0);
            } else {
                oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getSPECIMEN(specimenCounter).getSPM().getSetIDSPM().setValue("1");
            }
        }

        if (eiElement.getEntityIdentifier() != null && !eiElement.getEntityIdentifier().isEmpty()) {
            EI fillerAssignedIdentifier = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getParentNumber().getFillerAssignedIdentifier();
            fillerAssignedIdentifier.getEntityIdentifier().setValue(eiElement.getEntityIdentifier());
            fillerAssignedIdentifier.getNamespaceID().setValue(eiElement.getNamespaceID());
            fillerAssignedIdentifier.getUniversalID().setValue(eiElement.getUniversalID());
            fillerAssignedIdentifier.getUniversalIDType().setValue(eiElement.getUniversalIDType());
        }

        if (parentLink.getObservationValue() != null && !parentLink.getObservationValue().isEmpty()) {
            CE parentObservationIdentifier = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getParentResult().getParentObservationIdentifier();
            parentObservationIdentifier.getText().setValue(parentLink.getText());
            parentObservationIdentifier.getNameOfCodingSystem().setValue(parentLink.getNameOfCodingSystem());
            parentObservationIdentifier.getIdentifier().setValue(parentLink.getIdentifier());
            parentObservationIdentifier.getAlternateText().setValue(parentLink.getAlternateText());
            parentObservationIdentifier.getNameOfAlternateCodingSystem().setValue(parentLink.getNameOfAlternateCodingSystem());
            parentObservationIdentifier.getAlternateIdentifier().setValue(parentLink.getAlternateIdentifier());

            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getParentResult().getParentObservationSubIdentifier().setValue(parentLink.getObservationSubID());
            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBR().getParentResult().getParentObservationValueDescriptor().setValue(parentLink.getObservationValue());
        }

        int resultedTestCounter = 0;
        int previousCounter = 0;
        int repeatCounter =0;

        List<LabReportEvent.ResultedTest> resultedTestList = labReportEvent.getResultedTest();
        for (int j = 0; j < resultedTestList.size(); j++) {
            String specimenCollectionDate = "";
            String dateTimeOfAnalysis = "";
            String OBXResult = "";
            String referenceRangeFrom = "";
            String referenceRangeTo = "";
            String interpretationFlag = "";
            int notesCounter = 0;
            OBXResult = "";
            referenceRangeTo = "";
            referenceRangeFrom = "";

            ResultMethod resultMethod = new ResultMethod();
            repeatCounter += 1;

            ParentLink parentLinkOBX = new ParentLink();

            mapResultedTestToOBX.mapResultedTestToOBX(
                    resultedTestList.get(j),
                    obrCounter,
                    labSubCounter,
                    OBXResult,
                    resultedTestCounter,
                    notesCounter,
                    referenceRangeFrom,
                    referenceRangeTo,
                    resultStatus,
                    messageState.getMessageType(),
                    specimenCollectionDate,
                    obrCounter + 1,
                    eiType,
                    dateTimeOfAnalysis,
                    interpretationFlag,
                    obxSubidCounter,
                    cachedOBX3data,
                    resultMethod,
                    parentLinkOBX,
                    oruMessage
            );

            String identifier = "";
            String description = "";
            String descriptionValue = "";

            if (OBXResult.contains("^")) {
                int part1 = OBXResult.indexOf("^");
                identifier = OBXResult.substring(0, part1);

                String rest = OBXResult.substring(part1 + 1);

                int part2 = rest.indexOf("^");
                if (part2 >= 0) {
                    description = rest.substring(0, part2);
                    descriptionValue = rest.substring(part2 + 1);
                } else {
                    description = rest;
                }
            }
            for(int k = previousCounter; k < oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().numFields(); k++) {
                if(oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labObrCounter).getOBX().getObservationIdentifier().getIdentifier().isEmpty()) {
                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationIdentifier().getIdentifier().setValue(identifier);
                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationIdentifier().getNameOfCodingSystem().setValue(description);
                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationIdentifier().getText().setValue(descriptionValue);
                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationResultStatus().setValue(resultStatus);
                }

                String output = "";
                String outputIndex = "";
                String subString = "";

                if (cachedOBX3data != null && !cachedOBX3data.isEmpty() && cachedOBX3data.contains(identifier + ":")) {
                    int start = cachedOBX3data.indexOf(identifier + ":");

                    if (start == 0) {
                        int contTest = cachedOBX3data.indexOf("||");
                        subString = cachedOBX3data.substring(0, contTest);
                        int counterFinder = subString.indexOf(":");
                        String obxSubidCounterTest = subString.substring(counterFinder + 1);
                        int obxSubidCounterTestInt = Integer.parseInt(obxSubidCounterTest);

                        cachedOBX3data = cachedOBX3data.replaceFirst(
                                Pattern.quote(identifier + ":" + obxSubidCounterTest),
                                identifier + ":" + (obxSubidCounterTestInt + 1)
                        );

                        obxSubidCounter = obxSubidCounterTestInt + 1;
                    } else {
                        int contTest = cachedOBX3data.indexOf(identifier + ":");
                        int lookaheadLength = Math.min(cachedOBX3data.length() - contTest, identifier.length() + 6); // safe bounds
                        String cachedString = cachedOBX3data.substring(contTest, contTest + lookaheadLength);

                        String cachedStringWithoutMarker = cachedString.split("\\|\\|")[0];

                        int colonIndex = cachedStringWithoutMarker.indexOf(":");
                        String counterNumber = cachedStringWithoutMarker.substring(colonIndex + 1);
                        int obxSubidCounterTestInt = Integer.parseInt(counterNumber);

                        cachedOBX3data = cachedOBX3data.replaceFirst(
                                Pattern.quote(identifier + ":" + counterNumber),
                                identifier + ":" + (obxSubidCounterTestInt + 1)
                        );

                        obxSubidCounter = obxSubidCounterTestInt + 1;
                    }
                } else {
                    cachedOBX3data = cachedOBX3data + identifier + ":1||";
                    obxSubidCounter = 1;
                }
                parentLinkOBX.setObservationSubID(Integer.toString(obxSubidCounter));

                if(referenceRangeFrom.isEmpty() && !referenceRangeTo.isEmpty()) {
                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getReferencesRange().setValue(referenceRangeFrom + "-" + referenceRangeTo);
                } else if (!referenceRangeFrom.isEmpty() || !referenceRangeTo.isEmpty()) {
                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getReferencesRange().setValue(referenceRangeFrom + referenceRangeTo);
                }

                if (!specimenCollectionDate.isEmpty()) {
                    mapToDTM18.mapToDTM18(specimenCollectionDate, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getDateTimeOfTheObservation().getTime());
                }
                if (!dateTimeOfAnalysis.isEmpty()) {
                    mapToDTM18.mapToDTM18(dateTimeOfAnalysis, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getDateTimeOfTheAnalysis().getTime());
                }
                if (!interpretationFlag.isEmpty()) {
                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getAbnormalFlags(0).setValue(interpretationFlag);
                }
                if (resultMethod != null && !resultMethod.getText().isEmpty()) {
                    CE observationMethod = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationMethod(0);
                    observationMethod.getText().setValue(resultMethod.getText());
                    observationMethod.getNameOfCodingSystem().setValue(resultMethod.getNameOfCodingSystem());
                    observationMethod.getIdentifier().setValue(resultMethod.getCode());
                }

                oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationSubID().setValue(Integer.toString(obxSubidCounter));
                String cweOBX = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getValueType().toString();
                String ceOBX = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getValueType().toString();
                CE observationIdentifier = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationIdentifier();
                if("CWE".equals(cweOBX) || "CE".equals(ceOBX)) {
                    parentLinkOBX.setIdentifier(observationIdentifier.getIdentifier().getValue());
                    parentLinkOBX.setNameOfCodingSystem(observationIdentifier.getNameOfCodingSystem().getValue());
                    parentLinkOBX.setText(observationIdentifier.getText().getValue());
                    parentLinkOBX.setAlternateIdentifier(observationIdentifier.getAlternateText().getValue());
                    parentLinkOBX.setNameOfAlternateCodingSystem(observationIdentifier.getNameOfAlternateCodingSystem().getValue());
                    parentLinkOBX.setAlternateText(observationIdentifier.getAlternateText().getValue());
                    parentLinkOBX.setObservationSubID(oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationSubID().getValue());
                    // TODO - Check the toString() method
                    parentLinkOBX.setObservationValue(oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().getObservationValue(0).toString());
                    mapToSusceptibilityOBX(
                            nbsnndIntermediaryMessage,
                            resultedTestList.get(j),
                            obrCounter,
                            labSubCounter,
                            resultedTestCounter,
                            obrCounter + 1,
                            messageState.getMessageType(),
                            parentLinkOBX,
                            eiType,
                            oruMessage
                    );
                }
            }
            previousCounter = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrCounter).getOBSERVATION(labSubCounter).getOBX().numFields();
        }
    }

    void mapToSusceptibilityOBX(NBSNNDIntermediaryMessage nbsnndIntermediaryMessage, LabReportEvent.ResultedTest resultedTest, int obrCounter, int labSubCounter, int resultedTestCounter, int i, String messageType, ParentLink parentLink, EIElement eiElement, ORU_R01 oruMessage) throws HL7Exception {
        int labObrCounter;
        for(LabReportEvent labReportEvent : resultedTest.getLabReportEvent()) {
            obrCounter = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATIONReps();
            int obxSUSSubidCounter = 1;
            String cachedSUSOBX3data = "";
            if(!labReportEvent.getMessageElement().isEmpty()) {
                labObrCounter = obrCounter + 1;
                mapLabReportEventToOBR(nbsnndIntermediaryMessage,
                        labReportEvent,
                        obrCounter,
                        labObrCounter,
                        messageState.getMessageType(),
                        1,
                        eiElement,
                        parentLink,
                        obxSUSSubidCounter,
                        cachedSUSOBX3data,
                        oruMessage);
            }
        }
    }

}
