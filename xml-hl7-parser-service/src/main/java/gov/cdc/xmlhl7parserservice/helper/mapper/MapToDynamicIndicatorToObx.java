package gov.cdc.xmlhl7parserservice.helper.mapper;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import gov.cdc.xmlhl7parserservice.helper.MessageState;
import gov.cdc.xmlhl7parserservice.model.Obx.ObxRepeatingElement;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;

public class MapToDynamicIndicatorToObx {
    MessageState messageState = new MessageState();
    MapToRemoveSpecialCharacters mapToRemoveSpecialCharacters = new MapToRemoveSpecialCharacters();

     void mapToDynamicIndicatorToObx(MessageElement messageElement, String questionIdentifier, String mappedString, int countNum, int obx2Inc, ORU_R01_ORDER_OBSERVATION orderObservation) throws DataTypeException {
        String separatorWOObx5 = "|:";
        int intIndicator = mappedString.indexOf(separatorWOObx5);
        ObxRepeatingElement obxRepeatingElement = null;
        String partIndicator1 = "";
        String partIndicator2 = "";
        String partIndicator3 = "";
        String partIndicator4 = "";
        String partIndicator5 = "";
        String partIndicator6 = "";

        String obx1 = "";
        String partobx11 = "";
        String obx2 = "";
        String partobx12 = "";
        String obx3 = "";
        String partobx13 = "";

        if(mappedString.contains("^")) {
            int intStart1 = mappedString.indexOf("^");
            partIndicator1 = mappedString.substring(0, intStart1);
            String partRemaining1 = mappedString.substring(intStart1 + 1);

            int intStart2 = partRemaining1.indexOf("^");
            partIndicator2 = partRemaining1.substring(0, intStart2);
            String partRemaining2 = partRemaining1.substring(intStart2 + 1);

            int intStart3 = partRemaining2.indexOf("^");
            partIndicator3 = partRemaining2.substring(0, intStart3);
            String partRemaining3 = partRemaining2.substring(intStart3 + 1);

            int intStart4 = partRemaining3.indexOf("^");
            partIndicator4 = partRemaining3.substring(0, intStart4);
            String partRemaining4 = partRemaining3.substring(intStart4 + 1);

            int intStart5 = partRemaining4.indexOf("^");
            partIndicator5 = partRemaining4.substring(0, intStart5);
            String partRemaining5 = partRemaining4.substring(intStart5 + 1);

            int intStart6 = partRemaining5.indexOf("|||");
            partIndicator6 = partRemaining5.substring(0, intStart6);
            String partRemaining6 = partRemaining5.substring(intStart6 + 3);

            OBX obx = orderObservation.getOBSERVATION(1).getOBX();
            obx.getSetIDOBX().setValue(String.valueOf(obx2Inc + 1));

            CE observationIdentifier = obx.getObservationIdentifier();
            observationIdentifier.getIdentifier().setValue(partIndicator1);
            observationIdentifier.getText().setValue(partIndicator2);
            observationIdentifier.getNameOfCodingSystem().setValue(partIndicator3);
            observationIdentifier.getAlternateIdentifier().setValue(partIndicator4);
            observationIdentifier.getAlternateText().setValue(partIndicator5);
            observationIdentifier.getNameOfAlternateCodingSystem().setValue(partIndicator6);

            obx.getObservationSubID().setValue(String.valueOf(countNum));
            obx.getObservationResultStatus().setValue("F");

            orderObservation.getOBSERVATION(1).getOBX().getValueType().setValue("CWE");
            String questionDataType = messageElement.getDataElement().getQuestionDataTypeNND();

            obxRepeatingElement = new ObxRepeatingElement();
            obxRepeatingElement.setElementUid("mapToDynamicIndicatorToObx");
            obxRepeatingElement.setObxInc(1);
            messageState.getObxRepeatingElementArrayList().add(obxRepeatingElement);

            if(questionDataType.equals("CWE")) {
                if (intIndicator > 2) {
                    String codedValue = "";
                    if (messageElement.getDataElement().getCweDataType().getCweCodedValue() != null) {
                        codedValue = messageElement.getDataElement().getCweDataType().getCweCodedValue();
                    }

                    String codedValueDescription = "";
                    if (messageElement.getDataElement().getCweDataType().getCweCodedValueDescription() != null) {
                        codedValueDescription = messageElement.getDataElement().getCweDataType().getCweCodedValueDescription();
                    }

                    String codedValueCodingSystem = "";
                    if (messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem() != null) {
                        codedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem();
                    }

                    String localCodedValue = "";
                    if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValue() != null) {
                        localCodedValue = messageElement.getDataElement().getCweDataType().getCweLocalCodedValue();
                    }

                    String localCodedValueDescription = "";
                    if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription() != null) {
                        localCodedValueDescription = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueDescription();
                    }

                    String localCodedValueCodingSystem = "";
                    if (messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem() != null) {
                        localCodedValueCodingSystem = messageElement.getDataElement().getCweDataType().getCweLocalCodedValueCodingSystem();
                    }

                    String originalOtherText = "";
                    if (messageElement.getDataElement().getCweDataType().getCweOriginalText() != null) {
                        originalOtherText = "^^^" + messageElement.getDataElement().getCweDataType().getCweOriginalText();
                    }

                    String observationValue = String.join("^",
                            codedValue,
                            codedValueDescription,
                            codedValueCodingSystem,
                            localCodedValue,
                            localCodedValueDescription,
                            localCodedValueCodingSystem
                    ) + originalOtherText;

                    Type obxValue = obx.getObservationValue(0).getData();
                    ST stData;

                    if (obxValue instanceof ST) {
                        stData = (ST) obxValue;
                    } else {
                        stData = new ST(obx.getMessage());
                    }

                    stData.setValue(observationValue);
                    obx.getObservationValue(0).setData(stData);

                } else {
                    String observationValue = String.join("^", obx1, obx2, obx3);
                    Type obxValue = obx.getObservationValue(0).getData();
                    ST stData;

                    if (obxValue instanceof ST) {
                        stData = (ST) obxValue;
                    } else {
                        stData = new ST(obx.getMessage());
                    }


                    stData.setValue(observationValue);
                    obx.getObservationValue(0).setData(stData);
                }
            }

            if (questionDataType.equals("TS")) {
                obx.getValueType().setValue("TS");
                String time = messageElement.getDataElement().getTsDataType().getTime().toString();
                String year = "";
                String month = "00";
                String day = "00";
                String hour = "00";
                String minute = "00";
                String second = "00";
                String milli = "000";
                String separator = ".";

                int stringSize = time.length();

                if (stringSize >= 4) year = time.substring(0, 4);
                if (stringSize >= 7) month = time.substring(4, 6);
                if (stringSize >= 10) day = time.substring(6, 8);
                if (stringSize >= 13) hour = time.substring(8, 10);
                if (stringSize >= 16) minute = time.substring(10, 12);
                if (stringSize >= 19) second = time.substring(12, 14);
                if (stringSize >= 23) milli = time.substring(14, 17);

                String formattedTime = year + month + day + hour + minute + second + separator + milli;
                Type obxValue = obx.getObservationValue(0).getData();
                ST stData;

                if (obxValue instanceof ST) {
                    stData = (ST) obxValue;
                } else {
                    stData = new ST(obx.getMessage());
                }


                stData.setValue(formattedTime);
                obx.getObservationValue(0).setData(stData);
            }

            if (questionDataType.equals("TX")) {
                obx.getValueType().setValue("ST");

                String textData = "";
                if (messageElement.getDataElement().getTxDataType() != null &&
                        messageElement.getDataElement().getTxDataType().getTextData() != null) {

                    textData = messageElement.getDataElement().getTxDataType().getTextData();
                    textData = textData.replace("\n", " ");
                    textData = mapToRemoveSpecialCharacters.mapToRemoveSpecialCharacters(textData);
                }

                Type obxValue = obx.getObservationValue(0).getData();
                ST stData;

                if (obxValue instanceof ST) {
                    stData = (ST) obxValue;
                } else {
                    stData = new ST(obx.getMessage());
                }



                stData.setValue(textData);
                obx.getObservationValue(0).setData(stData);
            }

            if (questionDataType.equals("ST")) {
                obx.getValueType().setValue("ST");

                String textData = "";
                if (messageElement.getDataElement().getStDataType() != null &&
                        messageElement.getDataElement().getStDataType().getStringData() != null) {

                    textData = messageElement.getDataElement().getStDataType().getStringData();
                    textData = mapToRemoveSpecialCharacters.mapToRemoveSpecialCharacters(textData);
                }


                Type obxValue = obx.getObservationValue(0).getData();
                ST stData;

                if (obxValue instanceof ST) {
                    stData = (ST) obxValue;
                } else {
                    stData = new ST(obx.getMessage());
                }


                stData.setValue(textData);
                obx.getObservationValue(0).setData(stData);
            }
            obx2Inc = obx2Inc + 1;
        }

    }
}
