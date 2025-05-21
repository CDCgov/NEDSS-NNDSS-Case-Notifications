package gov.cdc.xmlhl7parserservice.helper.mapper;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import gov.cdc.xmlhl7parserservice.model.DiscreteMulti;
import gov.cdc.xmlhl7parserservice.model.Obx.ObxRepeatingElement;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;

public class MapToRepeatToMultiNND {
    public void mapToRepeatToMultiNND(MessageElement messageElement, int obx2Inc, ORU_R01_ORDER_OBSERVATION orderObservation) throws DataTypeException {
        ObxRepeatingElement obxRepeatingElement = null;
        String indPartMain = "";
        String indPart1 = "";
        String questPart1 = "";
        String questPart2 = "";
        String questPart3 = "";
        String questPart4 = "";
        String questPart5 = "";
        String questPart6 = "";

        String otherText = "";
        int obx4Counter = 1;
        String unkcode = "";
        String mappedValue = "";
        String indicatorCode = messageElement.getIndicatorCd();
        int obsCounter = 0;
        int counter = 0;
        String output = "";
        String originalString = "";

        int startInd = indicatorCode.indexOf("|");
        int discCdIndex = indicatorCode.indexOf("DiscCdToMultiOBS");
        mappedValue = indicatorCode.substring(0, discCdIndex - 2);
        indicatorCode = indicatorCode.substring(startInd + 2);

        String questionMap = messageElement.getQuestionMap();
        int start = questionMap.indexOf("|");
        String subStringRight = questionMap.substring(start + 1);
        int end = questionMap.indexOf("|");
        String subStringLeft = questionMap.substring(0, start);

        if (subStringLeft.contains("^")) {
            int startPartInt1 = subStringLeft.indexOf("^");
            questPart1 = subStringLeft.substring(0, startPartInt1);
            String remainingPart1 = subStringLeft.substring(startPartInt1 + 1);

            int startPartInt2 = remainingPart1.indexOf("^");
            questPart2 = remainingPart1.substring(0, startPartInt2);
            String remainingPart2 = remainingPart1.substring(startPartInt2 + 1);

            int startPartInt3 = remainingPart2.indexOf("^");
            questPart3 = remainingPart2.substring(0, startPartInt3);
            String remainingPart3 = remainingPart2.substring(startPartInt3 + 1);

            int startPartInt4 = remainingPart3.indexOf("^");
            questPart4 = remainingPart3.substring(0, startPartInt4);
            String remainingPart4 = remainingPart3.substring(startPartInt4 + 1);

            int startPartInt5 = remainingPart4.indexOf("^");
            questPart5 = remainingPart4.substring(0, startPartInt5);
            questPart6 = remainingPart4.substring(startPartInt5 + 1);
        }

        int checkerNum = 0;

        for (DiscreteMulti repeatMulti : repeatMultiArray) {
            if (repeatMulti.getCode().equals(questPart1)) {
                counter = repeatMulti.getCounter();
                repeatMulti.setObsValueCounter(repeatMulti.getObsValueCounter() + 1);
                obsCounter = repeatMulti.getObsValueCounter();
                checkerNum = 1;
                break;
            }
        }

        if (checkerNum == 0) {
            DiscreteMulti repeatMulti = new DiscreteMulti();
            repeatMulti.setObsValueCounter(repeatMultiArray.size());
            counter = obx2Inc;
            repeatMulti.setCounter(obx2Inc);
            repeatMulti.setCode(questPart1);
            repeatMulti.setObsValueCounter(0);
            repeatMulti.setCweQuestionIdentifier(messageElement.getQuestionIdentifier());
            repeatMultiArray.add(repeatMulti);

            OBX obx = orderObservation.getOBSERVATION(1).getOBX();
            obx.getObservationResultStatus().setValue("F");
            obx.getSetIDOBX().setValue(String.valueOf(obx2Inc + 1));
            obx.getValueType().setValue(messageElement.getDataElement().getQuestionDataTypeNND());

            CE observationIdentifier = obx.getObservationIdentifier();
            observationIdentifier.getIdentifier().setValue(questPart1);
            observationIdentifier.getText().setValue(questPart2);
            observationIdentifier.getNameOfCodingSystem().setValue(questPart3);
            observationIdentifier.getAlternateIdentifier().setValue(questPart4);
            observationIdentifier.getAlternateText().setValue(questPart5);
            observationIdentifier.getNameOfAlternateCodingSystem().setValue(questPart6);

            obx.getObservationResultStatus().setValue("F");

            obxRepeatingElement = new ObxRepeatingElement();
            obxRepeatingElement.setElementUid(questPart1);
            obxRepeatingElement.setObxInc(1);
            obxRepeatingElementArrayList.add(obxRepeatingElement);
        }

        var localComplex = messageElement.getDataElement();

        if (localComplex.getCeDataType() != null) {
            var ce = localComplex.getCeDataType();
            if (ce.getCeCodedValue() != null) {
                output = ce.getCeCodedValue();
            }
            if (ce.getCeCodedValueCodingSystem() != null) {
                output += "^" + ce.getCeCodedValueCodingSystem();
            }
            if (ce.getCeCodedValueDescription() != null) {
                output += "^" + ce.getCeCodedValueDescription();
            }
        } else if (localComplex.getCweDataType() != null) {
            var cwe = localComplex.getCweDataType();
            if (cwe.getCweCodedValue() != null) {
                output = cwe.getCweCodedValue();
            }
            if (cwe.getCweCodedValueCodingSystem() != null) {
                output += "^" + cwe.getCweCodedValueCodingSystem();
            }
            if (cwe.getCweCodedValueDescription() != null) {
                output += "^" + cwe.getCweCodedValueDescription();
            }
        } else if (localComplex.getDtDataType() != null) {
            var dt = localComplex.getDtDataType();
            if (dt.getYear() != null) {
                originalString = dt.getYear();
            }
            if (dt.getDate() != null) {
                originalString = dt.getDate().toString();
            }

            int stringSize = originalString.length();
            output = originalString.substring(0, Math.min(4, stringSize));
            if (stringSize > 7) {
                output += originalString.substring(4, 6);
            }
            if (stringSize > 10) {
                output += originalString.substring(6, 8);
            }
        } else if (localComplex.getStDataType() != null) {
            output = localComplex.getStDataType().getStringData();
        }

        if (localComplex.getNmDataType() != null) {
            output = localComplex.getNmDataType().getNum();
        }


        Type obxValue = orderObservation.getOBSERVATION(1).getOBX().getObservationValue(obsCounter).getData();
        ST stType;

        if (obxValue instanceof ST) {
            stType = (ST) obxValue;
        } else {
            stType = new ST(orderObservation.getOBSERVATION(1).getOBX().getMessage());
        }

        stType.setValue(output);
        orderObservation.getOBSERVATION(1).getOBX().getObservationValue(obsCounter).setData(stType);

        if (obxRepeatingElement == null) {
            obxRepeatingElement = new ObxRepeatingElement();
            obxRepeatingElement.setElementUid("mapToRepeatToMultiNND");
            obxRepeatingElement.setObxInc(1);
            obxRepeatingElementArrayList.add(obxRepeatingElement);
        }


//        out.OBSERVATION[1].OBX[counter].ObservationValue[obsCounter] = output;


        if (checkerNum == 0) {
            obx2Inc++;
        }

    }

}
