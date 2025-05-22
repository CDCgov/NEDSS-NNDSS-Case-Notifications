package gov.cdc.xmlhl7parserservice.helper.mapper;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.group.ORU_R01_ORDER_OBSERVATION;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.MessageElement;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class MapToDynamicRootlDiscToRepeat {

    MapToDynamicDiscToRepeat mapToDynamicDiscToRepeat = new MapToDynamicDiscToRepeat();
    MapToDynamicIndicatorToObx mapToDynamicIndicatorToObx = new MapToDynamicIndicatorToObx();

    public void mapToDynamicRootlDiscToRepeat(MessageElement messageElement, int obx2Inc, ORU_R01_ORDER_OBSERVATION orderObservation) throws DataTypeException {
        String questionMap = messageElement.getQuestionMap();
        String divider = "++";
        String separatorVal = "|";
        String separatorParent = "|:";
        String separatorSub = "^";
        String questionIdentifier = "";
        int countNum = 0;

        int intIndicator1 = questionMap.indexOf(divider);

        if (intIndicator1 > 0) {
            String part1 = questionMap.substring(0, intIndicator1);
            mapToDynamicDiscToRepeat.mapToDynamicDiscToRepeat(messageElement, part1, 1, obx2Inc, questionIdentifier, countNum, orderObservation);

            String part2 = questionMap.substring(intIndicator1 + divider.length());
            mapToDynamicDiscToRepeat.mapToDynamicDiscToRepeat(messageElement, part2, 2, obx2Inc, questionIdentifier, countNum, orderObservation);
        } else {
            mapToDynamicDiscToRepeat.mapToDynamicDiscToRepeat(messageElement, questionMap, 1, obx2Inc, questionIdentifier, countNum, orderObservation);
        }

        String indicatorCd = messageElement.getIndicatorCd();
        int intIndicator2 = indicatorCd.indexOf(separatorParent);

        if (intIndicator2 > 1) {
            String[] parts = indicatorCd.split(Pattern.quote(separatorVal));
            if (parts.length >= 3) {
                String part3 = parts[0];
                String part5 = parts[1];

                String dataType = messageElement.getDataElement().getQuestionDataTypeNND();
                if (dataType.equals("CWE")) {
                    String codedValue = messageElement.getDataElement().getCweDataType().getCweCodedValue();

                    boolean match = indicatorCd.startsWith(codedValue + separatorSub)
                            || indicatorCd.contains(separatorSub + codedValue + separatorSub)
                            || indicatorCd.contains(separatorSub + codedValue + separatorVal);

                    if (match) {
                        mapToDynamicIndicatorToObx.mapToDynamicIndicatorToObx(messageElement, questionIdentifier, part5, countNum, obx2Inc, orderObservation);
                    }
                }
            }
        }
    }

}
