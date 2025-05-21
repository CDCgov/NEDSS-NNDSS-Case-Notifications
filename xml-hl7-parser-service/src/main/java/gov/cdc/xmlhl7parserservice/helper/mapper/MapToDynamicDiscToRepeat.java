package gov.cdc.xmlhl7parserservice.helper.mapper;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import gov.cdc.xmlhl7parserservice.helper.MessageState;
import gov.cdc.xmlhl7parserservice.model.DynamicRepeatMulti;
import gov.cdc.xmlhl7parserservice.model.Obx.ObxRepeatingElement;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;

import java.util.ArrayList;
import java.util.List;

public class MapToDynamicDiscToRepeat {
    List<DynamicRepeatMulti> dynamicRepeatMultiArray = new ArrayList<>();
    MessageState messageState = new MessageState();
    MapToRemoveSpecialCharacters mapToRemoveSpecialCharacters = new MapToRemoveSpecialCharacters();

    void mapToDynamicDiscToRepeat(MessageElement messageElement, String mappedString, int splitPart, int obx2Inc, String questionIdentifier, int repeatCountNum, ORU_R01_ORDER_OBSERVATION orderObservation) throws DataTypeException {
        String separatorWOObx5 = "||||";
        int intIndicator = mappedString.indexOf(separatorWOObx5);

        String partIndicator1 = "";
        String partIndicator2 = "";
        String partIndicator3 = "";
        String partIndicator4 = "";
        String partIndicator5 = "";
        String partIndicator6 = "";
        String partIndicator7 = "";
        String partIndicator8 = "";
        String partIndicator9 = "";
        String partRemaining9 = "";

        String obx1 = "";
        String partobx11 = "";
        String obx2 = "";
        String partobx12 = "";
        String obx3 = "";
        String partobx13 = "";

        int counter = 0;

        if (mappedString.contains("^")) {
            if (intIndicator > 0) {
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

                int intStart7 = partRemaining6.indexOf("|");
                partIndicator7 = partRemaining6.substring(0, intStart7);
                String partRemaining7 = partRemaining6.substring(intStart7 + partIndicator7.length() + 3);

                int intStart8 = partRemaining7.indexOf("|");
                partIndicator8 = partRemaining7.substring(0, intStart8);
                String numVar = partIndicator8.substring(0, partRemaining7.indexOf("+"));
                int numCounter = Integer.parseInt(partIndicator8.substring(partRemaining7.indexOf("+") + 1));

                partRemaining9 = partRemaining7.substring(intStart8 + 1);
            } else {
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

                int intStart6 = partRemaining5.indexOf("|");
                partIndicator6 = partRemaining5.substring(0, intStart6);
                String partRemaining6 = partRemaining5.substring(intStart6 + 1);

                int intobx1 = partRemaining6.indexOf("^");
                obx1 = partRemaining6.substring(0, intobx1);
                String partRemainingintobx1 = partRemaining6.substring(intobx1 + 1);

                int intobx2 = partRemainingintobx1.indexOf("^");
                obx2 = partRemainingintobx1.substring(0, intobx2);
                String partRemainingintobx2 = partRemainingintobx1.substring(intobx2 + 1);

                int intobx3 = partRemainingintobx2.indexOf("|||");
                obx3 = partRemainingintobx2.substring(0, intobx3);
                String partRemainingintobx3 = partRemainingintobx2.substring(intobx3 + 3);

                int intStart8 = partRemainingintobx3.indexOf("|");
                partIndicator8 = partRemainingintobx3.substring(0, intStart8);
                String numVar = partIndicator8.substring(0, partRemainingintobx3.indexOf("+"));
                int numCounter = Integer.parseInt(partIndicator8.substring(partRemainingintobx3.indexOf("+") + 1));

                partRemaining9 = partRemainingintobx3.substring(intStart8 + 1);


            }

            questionIdentifier = partRemaining9;
            int maxObxCounter = 0;

            for (DynamicRepeatMulti item : dynamicRepeatMultiArray) {
                if (item.getParentCode().equals(partRemaining9) && item.getPartIndicator().equals(partIndicator8)) {
                    counter = item.getObx4counter();
                } else if (item.getParentCode().equals(partRemaining9) && counter == 0) {
                    if (maxObxCounter < item.getObx4counter()) {
                        maxObxCounter = item.getObx4counter();
                    }
                }
            }

            if (maxObxCounter == 0 && counter == 0) {
                DynamicRepeatMulti dynamicRepeat = new DynamicRepeatMulti();
                dynamicRepeat.setParentCode(partRemaining9);
                dynamicRepeat.setPartIndicator(partIndicator8);
                dynamicRepeat.setObx4counter(1);
                counter = 1;
                dynamicRepeatMultiArray.add(dynamicRepeat);
            } else if (maxObxCounter > 0) {
                DynamicRepeatMulti dynamicRepeat = new DynamicRepeatMulti();
                dynamicRepeat.setParentCode(partRemaining9);
                dynamicRepeat.setPartIndicator(partIndicator8);
                dynamicRepeat.setObx4counter(maxObxCounter + 1);
                counter = maxObxCounter + 1;
                dynamicRepeatMultiArray.add(dynamicRepeat);
            }

            repeatCountNum = counter;

            OBX obx = orderObservation.getOBSERVATION(1).getOBX();


            obx.getSetIDOBX().setValue(String.valueOf(obx2Inc + 1));

            CE observationIdentifier = obx.getObservationIdentifier();
            observationIdentifier.getIdentifier().setValue(partIndicator1);
            observationIdentifier.getText().setValue(partIndicator2);
            observationIdentifier.getNameOfCodingSystem().setValue(partIndicator3);
            observationIdentifier.getAlternateIdentifier().setValue(partIndicator4);
            observationIdentifier.getAlternateText().setValue(partIndicator5);
            observationIdentifier.getNameOfAlternateCodingSystem().setValue(partIndicator6);

            obx.getObservationSubID().setValue(String.valueOf(counter));
            obx.getObservationResultStatus().setValue("F");

            ObxRepeatingElement obxRepeatingElement = new ObxRepeatingElement();
            obxRepeatingElement.setElementUid(partIndicator1);
            obxRepeatingElement.setObxInc(1);
            messageState.getObxRepeatingElementArrayList().add(obxRepeatingElement);


            String questionDataType = messageElement.getDataElement().getQuestionDataTypeNND();
            if (questionDataType.equals("CWE") || splitPart == 2) {
                obx.getValueType().setValue("CWE");

                if(intIndicator > 0) {
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
                        String textData = "^^^" + messageElement.getDataElement().getCweDataType().getCweOriginalText();
                        textData = mapToRemoveSpecialCharacters.mapToRemoveSpecialCharacters(textData);
                        originalOtherText = "^^^" + textData;
                    }

                    String observationValue = String.join("^",
                            codedValue,
                            codedValueDescription,
                            codedValueCodingSystem,
                            localCodedValue,
                            localCodedValueDescription,
                            localCodedValueCodingSystem
                    ) + originalOtherText;


                    Type obxValue = obx.getObservationValue(1).getData();
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
            } else if (questionDataType.equals("TS")) {
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
            } else if (questionDataType.equals("TX")) {
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
            else if (questionDataType.equals("ST")) {
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
