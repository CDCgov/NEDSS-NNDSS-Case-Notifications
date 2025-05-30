package gov.cdc.xmlhl7parserservice.helper.mapper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.message.ORU_R01;
import gov.cdc.xmlhl7parserservice.model.EIElement;
import gov.cdc.xmlhl7parserservice.model.ParentLink;
import gov.cdc.xmlhl7parserservice.model.ResultMethod;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.LabReportEvent;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import org.springframework.stereotype.Component;

@Component
public class MapResultedTestToOBX {
    private final MapToObservationValue mapToObservationValue;
    private final MapToCEType mapToCEType;
    private final MapCodedToStringValue mapCodedToStringValue;

    public MapResultedTestToOBX(MapToObservationValue mapToObservationValue, MapToCEType mapToCEType, MapCodedToStringValue mapCodedToStringValue) {
        this.mapToObservationValue = mapToObservationValue;
        this.mapToCEType = mapToCEType;
        this.mapCodedToStringValue = mapCodedToStringValue;
    }

    void mapResultedTestToOBX(LabReportEvent.ResultedTest resultedTest, int obrSubCounter, int observationCounter, String OBXResult, int resultedTestCounter, int notesCounter,
                              String referenceRangeFrom, String referenceRangeTo, String resultStatus, String messageType, String specimenCollectionDate,
                              int obrCounter, EIElement eiElement, String dateTimeOfAnalysis, String interpretationFlag, int obxSubidCounter, String cachedOBX3data,
                              ResultMethod resultMethod, ParentLink parentLink, ORU_R01 oruMessage) throws HL7Exception {
        int valueTypeChecker = 0;

        for(MessageElement messageElement : resultedTest.getMessageElement()) {
            String hl7Field = messageElement.getHl7SegmentField().trim();

            if(hl7Field.startsWith("OBX-")) {
                String dataElement = hl7Field.replace("OBX-", "");

                if (dataElement.contains("5.")) {
                    int counter = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getObservationValue().length;
                    if(counter > 0){
                        resultedTestCounter = resultedTestCounter + 1;
                    }
                    oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getSetIDOBX().setValue(String.valueOf(resultedTestCounter + 1));
                }

                if (dataElement.contains("1.")) {
                }
                else if (dataElement.contains("2.")) {
                }
                //TODO - Check if this value is being used
                else if (dataElement.contains("3.")) {
                    String codedValue = mapCodedToStringValue.mapCodedToStringValue(messageElement, OBXResult, parentLink);
                }
                else if (dataElement.contains("5.")) {
                    if (messageElement.getDataElement().getQuestionDataTypeNND().equals("SN_WITH_UNIT")) {
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getValueType().setValue("SN");
                        valueTypeChecker = 1;

                        String codedValue = "";
                        String codedValueDescription = "";
                        String codedValueCodingSystem = "";
                        String localCodedValue = "";
                        String localCodedValueDescription = "";
                        String localCodedValueCodingSystem = "";

                        if (messageElement.getDataElement().getSnunitDataType().getCeCodedValue() != null) {
                            codedValue = messageElement.getDataElement().getSnunitDataType().getCeCodedValue();
                        }

                        if (messageElement.getDataElement().getSnunitDataType().getCeCodedValueDescription() != null) {
                            codedValueDescription = messageElement.getDataElement().getSnunitDataType().getCeCodedValueDescription();
                        }

                        if (messageElement.getDataElement().getSnunitDataType().getCeCodedValueCodingSystem() != null) {
                            codedValueCodingSystem = messageElement.getDataElement().getSnunitDataType().getCeCodedValueCodingSystem();
                        }

                        if (messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValue() != null) {
                            localCodedValue = messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValue();
                        }

                        if (messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueDescription() != null) {
                            localCodedValueDescription = messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueDescription();
                        }

                        if (messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueCodingSystem() != null) {
                            localCodedValueCodingSystem = messageElement.getDataElement().getSnunitDataType().getCeLocalCodedValueCodingSystem();
                        }

                        CE currentObxUnits = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getUnits();

                        currentObxUnits.getIdentifier().setValue(codedValue);
                        currentObxUnits.getNameOfCodingSystem().setValue(codedValueCodingSystem);
                        currentObxUnits.getText().setValue(codedValueDescription);
                        currentObxUnits.getAlternateIdentifier().setValue(localCodedValue);
                        currentObxUnits.getNameOfAlternateCodingSystem().setValue(localCodedValueCodingSystem);
                        currentObxUnits.getAlternateText().setValue(localCodedValueDescription);
                    }
                    else {
                        oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getValueType().setValue(messageElement.getDataElement().getQuestionDataTypeNND());
                        valueTypeChecker = 1;
                    }

                    String observationValue = mapToObservationValue.mapToObservationValue(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getObservationValue(0).toString());
                    Type obxValue = oruMessage.getPATIENT_RESULT()
                            .getORDER_OBSERVATION(obrSubCounter)
                            .getOBSERVATION(observationCounter)
                            .getOBX()
                            .getObservationValue(0)
                            .getData();

                    ST stDataType;
                    if (obxValue instanceof ST) {
                        stDataType = (ST) obxValue;
                    } else {
                        stDataType = new ST(
                                oruMessage.getPATIENT_RESULT()
                                        .getORDER_OBSERVATION(obrSubCounter)
                                        .getOBSERVATION(observationCounter)
                                        .getOBX()
                                        .getMessage()
                        );
                    }


                    stDataType.setValue(observationValue);
                    // mapToObservationValue(messageElement,out.PATIENT_RESULT.ORDER_OBSERVATION[obrSubCounter].OBSERVATION[observationCounter].OBX[resultedTestCounter].ObservationValue[0]);
                }
                else if (dataElement.contains("6.")) {
                    mapToCEType.mapToCEType(messageElement, oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getUnits());
                }
                else if (dataElement.contains("7.")) {
                    String dataLocator = dataElement.replace("7.", "");

                    if(messageElement.getQuestionIdentifier().equals("LAB119") || messageElement.getQuestionIdentifier().equals("NBS373")) {
                        referenceRangeFrom = messageElement.getDataElement().getStDataType().getStringData();
                    }
                    if(messageElement.getQuestionIdentifier().equals("LAB120") || messageElement.getQuestionIdentifier().equals("NBS374")) {
                        referenceRangeTo = messageElement.getDataElement().getStDataType().getStringData();
                    }
                }
                else if (dataElement.contains("8.")) {
                    interpretationFlag = messageElement.getDataElement().getIsDataType().getIsCodedValue();

                }
                else if (dataElement.contains("11.")) {
                    resultStatus =messageElement.getDataElement().getIdDataType().getIdCodedValue();

                }
                else if (dataElement.contains("14.")) {
                    specimenCollectionDate = messageElement.getDataElement().getTsDataType().getTime().toString();

                }
                else if (dataElement.contains("17.")) {
                    resultMethod.setCode(messageElement.getDataElement().getCeDataType().getCeCodedValue());
                    resultMethod.setNameOfCodingSystem(messageElement.getDataElement().getCeDataType().getCeCodedValueCodingSystem());
                    resultMethod.setText(messageElement.getDataElement().getCeDataType().getCeCodedValueDescription());
                }
                else if (dataElement.contains("19.")) {
                    dateTimeOfAnalysis = messageElement.getDataElement().getTsDataType().getTime().toString();
                }
                else if (dataElement.contains("23.1.")) {
                }
            }
        }

        String obxValueType = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getValueType().getValue();

        if(obxValueType == null || obxValueType.equals("")) {
//            setEmpty(oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getSPECIMEN(observationCounter));
            int length = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getSPECIMEN(observationCounter).getOBXReps();
            for (int counter = 0; counter < length; counter++) {
                oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getSPECIMEN(observationCounter).removeOBX(counter);
            }
        }
        if(valueTypeChecker == 0){
            int counter = oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getObservationValue().length;
            if(counter > 0){
                resultedTestCounter =resultedTestCounter + 1;
            }
            Type obxValue = oruMessage.getPATIENT_RESULT()
                    .getORDER_OBSERVATION(obrSubCounter)
                    .getOBSERVATION(observationCounter)
                    .getOBX()
                    .getObservationValue(0)
                    .getData();

            ST stDataType;
            if (obxValue instanceof ST) {
                stDataType = (ST) obxValue;
            } else {
                stDataType = new ST(
                        oruMessage.getPATIENT_RESULT()
                                .getORDER_OBSERVATION(obrSubCounter)
                                .getOBSERVATION(observationCounter)
                                .getOBX()
                                .getMessage()
                );
            }


            stDataType.setValue("\"\"");
            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getObservationValue(0).setData(stDataType);
            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getSetIDOBX().setValue(String.valueOf(resultedTestCounter + 1));
            oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION(obrSubCounter).getOBSERVATION(observationCounter).getOBX().getValueType().setValue("TX");
        }
    }


}
