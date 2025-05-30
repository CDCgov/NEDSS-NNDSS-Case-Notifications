package gov.cdc.xmlhl7parserservice.helper.mapper;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.datatype.CWE;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import gov.cdc.xmlhl7parserservice.helper.MessageState;
import gov.cdc.xmlhl7parserservice.model.DiscreteRepeat;
import gov.cdc.xmlhl7parserservice.model.Obx.ObxRepeatingElement;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MapToQuestionMap {
    MessageState messageState = new MessageState();
    List<DiscreteRepeat> discreteRepeatSNTypeArray = new ArrayList<>();
    List<DiscreteRepeat> discreteRepeatArray = new ArrayList<>();


    int obx2Inc = messageState.getObx2Inc();


    public void mapToQuestionMap(MessageElement messageElement, int counter, ORU_R01_ORDER_OBSERVATION orderObservation) throws HL7Exception {
        OBX obxUpdate = orderObservation.getOBSERVATION(orderObservation.getOBSERVATIONAll().size()).getOBX();

        String indPartMain = "";
        String indPart1 = "";
        String indPart2 = "";
        String indPart3 = "";
        String indPart4 = "";
        String indPart5 = "";
        String indPart6 = "";

        String questPart1 = "";
        String questPart2 = "";
        String questPart3 = "";
        String questPart4 = "";
        String questPart5 = "";
        String questPart6 = "";

        String quest2Part1 = "";
        String quest2Part2 = "";
        String quest2Part3 = "";
        String quest2Part4 = "";
        String quest2Part5 = "";
        String quest2Part6 = "";

        String otherText = "";
        int obx4Counter = 1;
        int mappedAsOtherInt = 0;
        String unkcode = "";
        String unkObx5 = "";
        String subStringRightInd = "";
        String mappedValue = "";
        String indicatorCdRaw = messageElement.getIndicatorCd();
        String indicatorCode = (indicatorCdRaw != null) ? indicatorCdRaw.trim() : "";
        String mappedIndicatorCode = (indicatorCdRaw != null) ? indicatorCdRaw.trim() : "";
        //we need to find index positions of the characters below

        int startInd = indicatorCode.indexOf("|");
        int checkPoint = indicatorCode.indexOf(":>");
        int splitCounter = indicatorCode.split("\\|").length; // Properly count splits
        int mapToQuestionId = indicatorCode.indexOf("|:");

        if (splitCounter == 3) {
            mappedIndicatorCode = indicatorCode.substring(mapToQuestionId + 2);
            indicatorCode = indicatorCode.substring(0, mapToQuestionId);
        }

        if (checkPoint > 0) {
            int gtIndex = indicatorCode.indexOf(">");
            if (gtIndex > 0) {
                mappedValue = indicatorCode.substring(0, gtIndex);
            }

            if (startInd != -1 && startInd + 1 < indicatorCode.length()) {
                indicatorCode = indicatorCode.substring(startInd + 1);
            }

            int nextPipeIndex = indicatorCode.indexOf("|");
            if (nextPipeIndex != -1) {
                unkObx5 = indicatorCode.substring(0, nextPipeIndex);
                if (nextPipeIndex + 1 < indicatorCode.length()) {
                    subStringRightInd = indicatorCode.substring(nextPipeIndex + 1);
                }
            }
        } else {
            if (startInd != -1 && startInd + 1 < indicatorCode.length()) {
                subStringRightInd = indicatorCode.substring(startInd + 1);
            }
        }

        // For parts related to "^" symbol
        //indPartMain = "^" + indicatorCode.substring(0, startInd) + "^";

        int endInd = indicatorCode.indexOf("|");
        String subStringLeftInd = (startInd != -1) ? indicatorCode.substring(0, startInd) : indicatorCode;
        indPartMain = "^" + subStringLeftInd + "^";

        if (subStringRightInd.contains("^")) {
            int startPartInt1 = subStringRightInd.indexOf("^");
            indPart1 = subStringRightInd.substring(0, startPartInt1);

            String RemainingPart1 = subStringRightInd.substring(startPartInt1 + 1);
            int startPartInt2 = RemainingPart1.indexOf("^");
            indPart2 = RemainingPart1.substring(0, startPartInt2);

            String RemainingPart2 = RemainingPart1.substring(startPartInt2 + 1);
            int startPartInt3 = RemainingPart2.indexOf("^");
            indPart3 = RemainingPart2.substring(0, startPartInt3);

            String RemainingPart3 = RemainingPart2.substring(startPartInt3 + 1);
            int startPartInt4 = RemainingPart3.indexOf("^");
            indPart4 = RemainingPart3.substring(0, startPartInt4);

            String RemainingPart4 = RemainingPart3.substring(startPartInt4 + 1);
            int startPartInt5 = RemainingPart4.indexOf("^");
            indPart5 = RemainingPart4.substring(0, startPartInt5);

            indPart6 = RemainingPart4.substring(startPartInt5 + 1);
        }

        String questionMap = (messageElement.getQuestionMap() != null) ? messageElement.getQuestionMap().trim() : "";
//        int start = questionMap.indexOf("|");
        String subStringRight = "";
//        int end = questionMap.indexOf("|");
        String subStringLeft = "";
        if (questionMap.contains("|")) {
            int index = questionMap.indexOf("|");
            subStringLeft = questionMap.substring(0, index);
            subStringRight = questionMap.substring(index + 1);
        }

        if (subStringLeft.contains("^")) {
            if (subStringLeft != null && !subStringLeft.isEmpty()) {
                String[] parts = subStringLeft.split("\\^", -1);

                questPart1 = parts.length > 0 ? parts[0] : "";
                questPart2 = parts.length > 1 ? parts[1] : "";
                questPart3 = parts.length > 2 ? parts[2] : "";
                questPart4 = parts.length > 3 ? parts[3] : "";
                questPart5 = parts.length > 4 ? parts[4] : "";
                questPart6 = parts.length > 5 ? parts[5] : "";
            }
        }

        if (subStringRight.contains("^")) {
            if (subStringRight != null && !subStringRight.isEmpty()) {
                String[] parts = subStringRight.split("\\^", -1);
                quest2Part1 = parts.length > 0 ? parts[0] : "";
                quest2Part2 = parts.length > 1 ? parts[1] : "";
                quest2Part3 = parts.length > 2 ? parts[2] : "";
                quest2Part4 = parts.length > 3 ? parts[3] : "";
                quest2Part5 = parts.length > 4 ? parts[4] : "";
                quest2Part6 = parts.length > 5 ? parts[5] : "";
            }
        }

        String dataElementCodedValue = "";
        String questionDataType = "";

        if(messageElement.getDataElement().getCweDataType() != null) {
            dataElementCodedValue = messageElement.getDataElement().getCweDataType().getCweCodedValue();
        }

        if(messageElement.getDataElement().getQuestionDataTypeNND() != null) {
            questionDataType = messageElement.getDataElement().getQuestionDataTypeNND();
        }

        if (!mappedValue.isEmpty() && !mappedValue.equals(dataElementCodedValue))
        {
        }
        else if ("SN_WITH_UNIT".equals(questionDataType))
        {
            int checkerNum = 0;

            for (DiscreteRepeat item : discreteRepeatSNTypeArray) {
                if (item.getMappedIndicatorCode().equals(mappedIndicatorCode)) {
                    obx4Counter = item.getObx4counter();
                    checkerNum = 1;
                    break;
                }
            }

            if (checkerNum == 0) {
                DiscreteRepeat newItem = new DiscreteRepeat();
                newItem.setObx4counter(discreteRepeatSNTypeArray.size());
                obx4Counter = newItem.getObx4counter();
                newItem.setCode(questPart1);
                newItem.setMappedIndicatorCode(mappedIndicatorCode);
                newItem.setCounter(obx2Inc);
                discreteRepeatSNTypeArray.add(newItem);
            }

            OBX obx = orderObservation.getOBSERVATION(1).getOBX();
            obx.getSetIDOBX().setValue(String.valueOf(obx2Inc + 1));
            obx.getValueType().setValue("SN");

            CE observationIdentifier = orderObservation.getOBSERVATION(1).getOBX().getObservationIdentifier();
            observationIdentifier.getIdentifier().setValue(questPart1);
            observationIdentifier.getText().setValue(questPart2);
            observationIdentifier.getNameOfCodingSystem().setValue(questPart3);
            observationIdentifier.getAlternateIdentifier().setValue(messageElement.getQuestionIdentifierNND());
            observationIdentifier.getAlternateText().setValue(messageElement.getQuestionLabelNND());
            observationIdentifier.getNameOfAlternateCodingSystem().setValue(questPart6);

            obx.getObservationSubID().setValue(String.valueOf(obx4Counter));

            String value = "^" + messageElement.getDataElement().getSnunitDataType().getNum1();
            ST observationValueStType = (obx.getObservationValue(0).getData() instanceof ST)
                    ? (ST) obx.getObservationValue(0).getData()
                    : new ST(obx.getMessage());

            observationValueStType.setValue(value);
            obx.getObservationValue(0).setData(observationValueStType);

            obx.getUnits().getIdentifier().setValue(messageElement.getDataElement().getSnunitDataType().getCeCodedValue());
            obx.getUnits().getText().setValue(messageElement.getDataElement().getSnunitDataType().getCeCodedValueDescription());
            obx.getUnits().getNameOfCodingSystem().setValue(messageElement.getDataElement().getSnunitDataType().getCeCodedValueCodingSystem());
            obx.getObservationResultStatus().setValue("F");

            obx2Inc++;
        }
        else if (messageElement.getQuestionIdentifierNND().contains("_OTH"))
        {
            int checkerNum = 0;
            String tester = "";

            int obxSize = orderObservation.getOBSERVATION(0).getOBX().numFields();
            // TODO - Check this length
            for (int j = 0; j < obxSize; j++) {
                String originalText = messageElement.getDataElement().getStDataType().getStringData();

                if (originalText != null && !originalText.isEmpty()) {
                    originalText = originalText.replace("\\", "\\E\\");
                    originalText = originalText.replace("|", "\\F\\");
                    originalText = originalText.replace("~", "\\R\\");
                    originalText = originalText.replace("^", "\\S\\");
                    originalText = originalText.replace("&", "\\T\\");
                }

                OBX obx = orderObservation.getOBSERVATION(0).getOBX();

                if (obx.getObservationIdentifier().getIdentifier().getValue().equals(questPart1) &&
                        obx.getObservationValue(0).getData().toString().startsWith("OTH^")) {
                    String updatedValue = obx.getObservationValue(0) + "^^^^^^" + originalText;
                    ST stTempType = (obx.getObservationValue(0).getData() instanceof ST)
                            ? (ST) obx.getObservationValue(0).getData()
                            : new ST(obx.getMessage());

                    stTempType.setValue(updatedValue);
                    obx.getObservationValue(0).setData(stTempType);
                    tester = "mapped";
                }
            }

            if (tester.isEmpty()) {
                otherText = messageElement.getDataElement().getStDataType().getStringData();

                if (otherText != null && !otherText.isEmpty()) {
                    otherText = otherText.replace("\\", "\\E\\");
                    otherText = otherText.replace("|", "\\F\\");
                    otherText = otherText.replace("~", "\\R\\");
                    otherText = otherText.replace("^", "\\S\\");
                    otherText = otherText.replace("&", "\\T\\");
                }

                boolean found = false;
                for (DiscreteRepeat dr : discreteRepeatArray) {
                    if (dr.getCode().equals(questPart1)) {
                        dr.setOtherText(otherText);
                        found = true;
                    }
                }

                if (discreteRepeatArray.isEmpty()) {
                    DiscreteRepeat discreteRepeat = new DiscreteRepeat();
                    // discreteRepeat.setCounter(1);
                    discreteRepeat.setCode(questPart1);
                    discreteRepeat.setOtherText(otherText);
                    discreteRepeatArray.add(discreteRepeat);
                }
            } else {
                for (DiscreteRepeat dr : discreteRepeatArray) {
                    if (dr.getCode().equals(questPart1)) {
                        dr.setOtherText(otherText);
                        checkerNum = 1;
                    }
                }

                if (checkerNum == 0) {
                    DiscreteRepeat discreteRepeat = new DiscreteRepeat();
                    discreteRepeat.setCounter(1);
                    discreteRepeat.setCode(questPart1);
                    discreteRepeat.setOtherText(otherText);
                    discreteRepeatArray.add(discreteRepeat);
                    mappedAsOtherInt = 1;
                }
            }
        }
        else if (indPartMain.contains("^" + dataElementCodedValue + "^")
                || (unkObx5 != null && !unkObx5.isEmpty()))
        {
            int checkerNum = 0;
            String id = messageElement.getQuestionIdentifier();

            if (splitCounter == 3) {
                int checkerNumDetail = 0;
                for (DiscreteRepeat dr : discreteRepeatSNTypeArray) {
                    if (dr.getMappedIndicatorCode().equals(mappedIndicatorCode)) {
                        obx4Counter = dr.getObx4counter();
                        checkerNumDetail = 1;
                    }
                }
                if (checkerNumDetail == 0) {
                    DiscreteRepeat discreteRepeat = new DiscreteRepeat();
                    discreteRepeat.setObx4counter(discreteRepeatSNTypeArray.size());
                    obx4Counter = discreteRepeat.getObx4counter();
                    discreteRepeat.setCode(questPart1);
                    discreteRepeat.setMappedIndicatorCode(mappedIndicatorCode);
                    discreteRepeat.setCounter(obx2Inc);
                    discreteRepeatSNTypeArray.add(discreteRepeat);
                }
            }

            for (DiscreteRepeat dr : discreteRepeatArray) {
                if (dr.getCode().equals(questPart1) && mappedAsOtherInt == 0) {
                    dr.setCounter(dr.getCounter() + 1);
                    checkerNum = 1;
                    otherText = dr.getOtherText();
                    obx4Counter = dr.getCounter();
                }
            }

            if (checkerNum == 0 && mappedAsOtherInt == 0) {
                DiscreteRepeat discreteRepeat = new DiscreteRepeat();
                discreteRepeat.setCounter(1);
                discreteRepeat.setCode(questPart1);
                discreteRepeatArray.add(discreteRepeat);
            }

            // Case 2
            orderObservation.getOBSERVATION(0).getOBX().getSetIDOBX().setValue(String.valueOf(counter + 1));
            orderObservation.getOBSERVATION(0).getOBX().getValueType().setValue("CWE");


            OBX obx1 = orderObservation.getOBSERVATION(0).getOBX();

            obx1.getObservationIdentifier().getIdentifier().setValue(questPart1);
            obx1.getObservationIdentifier().getText().setValue(questPart2);
            obx1.getObservationIdentifier().getNameOfCodingSystem().setValue(questPart3);
            obx1.getObservationIdentifier().getAlternateIdentifier().setValue(questPart4);
            obx1.getObservationIdentifier().getAlternateText().setValue(questPart5);
            obx1.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue(questPart6);

            ObxRepeatingElement obxRepeatingElement = new ObxRepeatingElement();
            obxRepeatingElement.setElementUid(questPart1);
            obxRepeatingElement.setObxInc(0);
            messageState.getObxRepeatingElementArrayList().add(obxRepeatingElement);

            if (checkPoint > 0) {
                CWE cweObservationValue = (obx1.getObservationValue(0).getData() instanceof CWE)
                        ? (CWE) obx1.getObservationValue(0).getData()
                        : new CWE(obx1.getMessage());



                cweObservationValue.getIdentifier().setValue(messageElement.getDataElement().getCweDataType().getCweCodedValue());
                cweObservationValue.getText().setValue(messageElement.getDataElement().getCweDataType().getCweCodedValueDescription());
                cweObservationValue.getNameOfCodingSystem().setValue(messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem());

                obx1.getObservationValue(0).setData(cweObservationValue);

            } else {
                CWE cweObservationValue = (obx1.getObservationValue(0).getData() instanceof CWE)
                        ? (CWE) obx1.getObservationValue(0).getData()
                        : new CWE(obx1.getMessage());


                cweObservationValue.getIdentifier().setValue(quest2Part1);
                cweObservationValue.getText().setValue(quest2Part2);
                cweObservationValue.getNameOfCodingSystem().setValue(quest2Part3);

                obx1.getObservationValue(0).setData(cweObservationValue);

            }

            if (messageElement.getQuestionIdentifierNND().equals("OTH") && otherText != null && !otherText.isEmpty()) {
                //TODO - Check value here
                String val = obx1.getObservationValue(0).toString() + "^^^^^^" + otherText;
                ST stObservationValue = (obx1.getObservationValue(0).getData() instanceof ST)
                        ? (ST) obx1.getObservationValue(0).getData()
                        : new ST(obx1.getMessage());

                stObservationValue.setValue(val);
                obx1.getObservationValue(0).setData(stObservationValue);
            }

            obx1.getObservationSubID().setValue(String.valueOf(obx4Counter));
            obx1.getObservationResultStatus().setValue("F");

            obx2Inc++;

            orderObservation.getOBSERVATION(1).getOBX().getSetIDOBX().setValue(String.valueOf(obx2Inc + 1));
            orderObservation.getOBSERVATION(1).getOBX().getValueType().setValue("CWE");

            OBX obx2 = orderObservation.getOBSERVATION(1).getOBX();

            obx2.getObservationIdentifier().getIdentifier().setValue(indPart1);
            obx2.getObservationIdentifier().getText().setValue(indPart2);
            obx2.getObservationIdentifier().getNameOfCodingSystem().setValue(indPart3);
            obx2.getObservationIdentifier().getAlternateIdentifier().setValue(indPart4);
            obx2.getObservationIdentifier().getAlternateText().setValue(indPart5);
            obx2.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue(indPart6);

            ObxRepeatingElement obxRepeatingElement1 = new ObxRepeatingElement();
            obxRepeatingElement1.setElementUid(indPart1);
            obxRepeatingElement1.setObxInc(1);
            messageState.getObxRepeatingElementArrayList().add(obxRepeatingElement1);


            if (unkObx5 != null && !unkObx5.isEmpty()) {
                ST stObservationValue = (obx2.getObservationValue(0).getData() instanceof ST)
                        ? (ST) obx2.getObservationValue(0).getData()
                        : new ST(obx2.getMessage());

                stObservationValue.setValue(unkObx5);
                obx2.getObservationValue(0).setData(stObservationValue);
            }
            else
            {
                CWE cweObservationValue = (obx2.getObservationValue(0).getData() instanceof CWE)
                        ? (CWE) obx2.getObservationValue(0).getData()
                        : new CWE(obx2.getMessage());

                cweObservationValue.getIdentifier().setValue(messageElement.getDataElement().getCweDataType().getCweCodedValue());
                cweObservationValue.getText().setValue(messageElement.getDataElement().getCweDataType().getCweCodedValueDescription());
                cweObservationValue.getNameOfCodingSystem().setValue(messageElement.getDataElement().getCweDataType().getCweCodedValueCodingSystem());

                obx2.getObservationValue(0).setData(cweObservationValue);

            }

            obx2.getObservationSubID().setValue(String.valueOf(obx4Counter));
            obx2.getObservationResultStatus().setValue("F");

            obx2Inc++;
        }
    }

}
