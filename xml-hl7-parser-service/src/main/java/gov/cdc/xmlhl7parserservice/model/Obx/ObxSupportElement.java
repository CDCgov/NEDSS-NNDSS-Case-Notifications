package gov.cdc.xmlhl7parserservice.model.Obx;

import ca.uhn.hl7v2.model.v25.segment.OBX;
import gov.cdc.xmlhl7parserservice.model.MessageElement;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ObxSupportElement {
    List<ObxRepeatingElement> obxRepeatingElementArrayList;
    MessageElement messageElement;
    OBX obx;
    String messageType;
    Integer dupRepeatCongenitalCounter;
    Integer inv290Inv291Counter;
    Integer obx1Inc;
    Integer obx2Inc;
    Boolean inv177Found;
    String newDate;
    String inv177Date;
    Boolean isDefaultNull;
    Boolean genericMMGv20;
    String hcw;
    Integer hcwObxInc;
    Integer hcwObxOrderGroupId;
    Integer hcwObx5ValueInc;
    Boolean hcwTextBeforeCodedInd;
    Boolean INV162RepeatIndicator;
    Integer drugCounter;

}
