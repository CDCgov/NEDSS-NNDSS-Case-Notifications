package gov.cdc.xmlhl7parserservice.helper.mapper;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.datatype.TS;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import gov.cdc.xmlhl7parserservice.helper.MessageState;
import gov.cdc.xmlhl7parserservice.model.DynamicRepeatMulti;
import gov.cdc.xmlhl7parserservice.model.Obx.ObxRepeatingElement;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MapToDynamicParentRptToRpt {
    private final List<ObxRepeatingElement> obxRepeatingElementArrayList;
    private final List<DynamicRepeatMulti> dynamicRepeatMultiArray;
    private final MapToRemoveSpecialCharacters mapToRemoveSpecialCharacters;

    public MapToDynamicParentRptToRpt(List<ObxRepeatingElement> obxRepeatingElementArrayList, List<DynamicRepeatMulti> dynamicRepeatMultiArray, MapToRemoveSpecialCharacters mapToRemoveSpecialCharacters) {
        this.obxRepeatingElementArrayList = obxRepeatingElementArrayList;
        this.dynamicRepeatMultiArray = dynamicRepeatMultiArray;
        this.mapToRemoveSpecialCharacters = mapToRemoveSpecialCharacters;
    }

    public void mapToDynamicParentRptToRpt(MessageElement messageElement, int obx2Inc, String messageType, ORU_R01_ORDER_OBSERVATION orderObservation) throws DataTypeException {
        String parentCode = messageElement.getIndicatorCd();
        int intStart = parentCode.indexOf("|ParentRepeatBlock");

        String partIndicator1 = parentCode.substring(0, intStart);
        String parentQuestionIdentifier = partIndicator1.replace("||:", "");

        String questionId = messageElement.getQuestionIdentifierNND();
        String obsSubIdCounter = messageElement.getObservationSubID();

        int maxObxCounter = 0;
        int counter = 0;


        for (int x = 0; x < dynamicRepeatMultiArray.size(); x++) {
            DynamicRepeatMulti entry = dynamicRepeatMultiArray.get(x);

            if (entry.getParentCode().equals(parentQuestionIdentifier) &&
                    entry.getPartIndicator().equals(obsSubIdCounter)) {
                counter = entry.getObx4counter();
            }
            else if (entry.getParentCode().equals(parentQuestionIdentifier) && counter == 0) {
                if (maxObxCounter < entry.getObx4counter()) {
                    maxObxCounter = entry.getObx4counter();
                }
            }
        }
        if (maxObxCounter == 0 && counter == 0) {
            DynamicRepeatMulti dynamicRepeat = new DynamicRepeatMulti();
            dynamicRepeat.setParentCode(parentQuestionIdentifier);
            dynamicRepeat.setPartIndicator(obsSubIdCounter);
            dynamicRepeat.setObx4counter(1);
            dynamicRepeatMultiArray.add(dynamicRepeat);
            counter = dynamicRepeat.getObx4counter();
        } else if (maxObxCounter > 0) {
            DynamicRepeatMulti dynamicRepeat = new DynamicRepeatMulti();
            dynamicRepeat.setParentCode(parentQuestionIdentifier);
            dynamicRepeat.setPartIndicator(obsSubIdCounter);
            dynamicRepeat.setObx4counter(maxObxCounter + 1);
            dynamicRepeatMultiArray.add(dynamicRepeat);
            counter = dynamicRepeat.getObx4counter();
        }

        OBX obx = orderObservation.getOBSERVATION(1).getOBX();

        obx.getValueType().setValue(messageElement.getDataElement().getQuestionDataTypeNND());
        obx.getSetIDOBX().setValue(String.valueOf(obx2Inc + 1));

        obx.getObservationIdentifier().getIdentifier().setValue(messageElement.getQuestionIdentifierNND());
        obx.getObservationIdentifier().getText().setValue(messageElement.getQuestionLabelNND());
        obx.getObservationIdentifier().getNameOfCodingSystem().setValue(messageElement.getQuestionOID());

        obx.getObservationIdentifier().getAlternateIdentifier().setValue(messageElement.getQuestionIdentifier());
        obx.getObservationIdentifier().getAlternateText().setValue(messageElement.getQuestionLabel());
        obx.getObservationIdentifier().getNameOfAlternateCodingSystem().setValue("L");

        obx.getObservationResultStatus().setValue("F");
        obx.getObservationSubID().setValue(String.valueOf(counter));

        ObxRepeatingElement obxRepeatingElement = new ObxRepeatingElement();
        obxRepeatingElement.setElementUid(messageElement.getQuestionIdentifierNND());
        obxRepeatingElement.setObxInc(1);
        obxRepeatingElementArrayList.add(obxRepeatingElement);

        if (messageElement.getDataElement().getQuestionDataTypeNND().equals("CWE")) {
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
                String textData = messageElement.getDataElement().getCweDataType().getCweOriginalText();
                textData = mapToRemoveSpecialCharacters.mapToRemoveSpecialCharacters(textData);
                originalOtherText = "^^^" + textData;
            }

            String finalValue = String.join("^",
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


            stData.setValue(finalValue);
            obx.getObservationValue(0).setData(stData);
        }
        if ((messageElement.getDataElement().getQuestionDataTypeNND()).equals("TS")) {
            String time = messageElement.getDataElement().getTsDataType().getTime().toString();

            String year = time.length() >= 4 ? time.substring(0, 4) : "";
            String month = time.length() >= 6 ? time.substring(4, 6) : "00";
            String day = time.length() >= 8 ? time.substring(6, 8) : "00";
            String hour = time.length() >= 10 ? time.substring(8, 10) : "00";
            String minute = time.length() >= 12 ? time.substring(10, 12) : "00";
            String second = time.length() >= 14 ? time.substring(12, 14) : "00";
            String milli = time.length() >= 17 ? time.substring(14, 17) : "000";
            String separator = ".";

            String timeOut = year + month + day + hour + minute + second + separator + milli;

            //TODO - Check how to set value
            Type obxValue = obx.getObservationValue(0).getData();
            TS tsType;

            if (obxValue instanceof TS) {
                tsType = (TS) obxValue;
            } else {
                tsType = new TS(obx.getMessage());
            }


            tsType.getTime().setValue(timeOut);
            obx.getObservationValue(0).setData(tsType);
        }
        String dataType = messageElement.getDataElement().getQuestionDataTypeNND();

        if (dataType.equals("TX")) {
            obx.getValueType().setValue("ST");
            String textData = messageElement.getDataElement().getTxDataType().getTextData();
            textData = textData.replace("\n", " ");

            Type obxValue = obx.getObservationValue(0).getData();
            ST stDataType;

            if (obxValue instanceof ST) {
                stDataType = (ST) obxValue;
            } else {
                stDataType = new ST(obx.getMessage());
            }


            stDataType.setValue(textData);
            obx.getObservationValue(0).setData(stDataType);
        }

        if (dataType.equals("ST")) {
            obx.getValueType().setValue(dataType);
            String textData = messageElement.getDataElement().getStDataType().getStringData();

            Type obxValue = obx.getObservationValue(0).getData();
            ST stDataType;

            if (obxValue instanceof ST) {
                stDataType = (ST) obxValue;
            } else {
                stDataType = new ST(obx.getMessage());
            }


            stDataType.setValue(textData);
            obx.getObservationValue(0).setData(stDataType);
        }
        obx2Inc = obx2Inc + 1;
    }

}
