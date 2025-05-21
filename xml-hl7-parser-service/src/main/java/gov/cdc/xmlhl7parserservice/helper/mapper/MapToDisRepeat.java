package gov.cdc.xmlhl7parserservice.helper.mapper;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Type;
import ca.uhn.hl7v2.model.v25.datatype.CE;
import ca.uhn.hl7v2.model.v25.datatype.ST;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import gov.cdc.xmlhl7parserservice.helper.MessageState;
import gov.cdc.xmlhl7parserservice.model.DiscreteMulti;
import gov.cdc.xmlhl7parserservice.model.Obx.ObxRepeatingElement;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import org.springframework.stereotype.Component;

@Component
public class MapToDisRepeat {
    MessageState messageState = new MessageState();
    DiscreteMulti discreteMulti = new DiscreteMulti();

    public void mapToDisRepeat(MessageElement messageElement, int obx2Inc, ORU_R01_ORDER_OBSERVATION orderObservation) throws DataTypeException {
        ObxRepeatingElement obxRepeatingElement = null;

        String indicatorCode = messageElement.getIndicatorCd();
        int startInd = indicatorCode.indexOf("|");

        String mappedValue = indicatorCode.substring(0, indicatorCode.indexOf("DiscCdToMultiOBS") - 2);

        if (mappedValue.equals("Y") && (messageElement.getDataElement().getCweDataType().getCweCodedValue()).equals("Y")) {
            indicatorCode = indicatorCode.substring(startInd + 2);

            String questionMap = messageElement.getQuestionMap();
            int start = questionMap.indexOf("|");
            String subStringRight = questionMap.substring(start + 1);
            String subStringLeft = questionMap.substring(0, start);

            String questPart1 = "", questPart2 = "", questPart3 = "", questPart4 = "", questPart5 = "", questPart6 = "";
            if (subStringLeft.contains("^")) {
                int partStart1 = subStringLeft.indexOf("^");
                questPart1 = subStringLeft.substring(0, partStart1);
                String remaining1 = subStringLeft.substring(partStart1 + 1);

                int partStart2 = remaining1.indexOf("^");
                questPart2 = remaining1.substring(0, partStart2);
                String remaining2 = remaining1.substring(partStart2 + 1);

                int partStart3 = remaining2.indexOf("^");
                questPart3 = remaining2.substring(0, partStart3);
                String remaining3 = remaining2.substring(partStart3 + 1);

                int partStart4 = remaining3.indexOf("^");
                questPart4 = remaining3.substring(0, partStart4);
                String remaining4 = remaining3.substring(partStart4 + 1);

                int partStart5 = remaining4.indexOf("^");
                questPart5 = remaining4.substring(0, partStart5);
                questPart6 = remaining4.substring(partStart5 + 1);
            }

            if (discreteMulti.getCounter() == 0) {
                discreteMulti.setCounter(obx2Inc);
                discreteMulti.setObsValueCounter(0);
                discreteMulti.setCode(questPart1);
                discreteMulti.setIndicatorCode(messageElement.getIndicatorCd());
                discreteMulti.setCweQuestionIdentifier(messageElement.getQuestionIdentifier());

                OBX obx = orderObservation.getOBSERVATION(1).getOBX();
                obx.getObservationResultStatus().setValue("F");
                obx.getSetIDOBX().setValue(String.valueOf(obx2Inc + 1));
                obx.getValueType().setValue("CWE");

                CE obsId = obx.getObservationIdentifier();
                obsId.getIdentifier().setValue(questPart1);
                obsId.getText().setValue(questPart2);
                obsId.getNameOfCodingSystem().setValue(questPart3);
                obsId.getAlternateIdentifier().setValue(questPart4);
                obsId.getAlternateText().setValue(questPart5);
                obsId.getNameOfAlternateCodingSystem().setValue(questPart6);

                obxRepeatingElement = new ObxRepeatingElement();
                obxRepeatingElement.setElementUid(questPart1);
                obxRepeatingElement.setObxInc(1);
                messageState.getObxRepeatingElementArrayList().add(obxRepeatingElement);

                obx2Inc += 1;
            } else {
                discreteMulti.setObsValueCounter(discreteMulti.getObsValueCounter() + 1);
            }

            //TODO - Verify this implementation everywhere in the code
            Type obxValue = orderObservation.getOBSERVATION(1).getOBX().getObservationValue(discreteMulti.getObsValueCounter()).getData();
            ST stType;

            if (obxValue instanceof ST) {
                stType = (ST) obxValue;
            } else {
                stType = new ST(orderObservation.getOBSERVATION(1).getOBX().getMessage());
            }


            stType.setValue(subStringRight);
            orderObservation.getOBSERVATION(1).getOBX().getObservationValue(discreteMulti.getObsValueCounter()).setData(stType);
            if (obxRepeatingElement == null) {
                obxRepeatingElement = new ObxRepeatingElement();
                obxRepeatingElement.setElementUid("mapToDisRepeat");
                obxRepeatingElement.setObxInc(1);
                messageState.getObxRepeatingElementArrayList().add(obxRepeatingElement);
            }

        }
    }
}
