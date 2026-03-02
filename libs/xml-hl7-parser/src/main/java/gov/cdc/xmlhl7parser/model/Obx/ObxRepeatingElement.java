package gov.cdc.xmlhl7parser.model.Obx;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ObxRepeatingElement {
  private String elementUid;
  private String questionGroupSeqNbr;
  private String observationSubId;
  private Integer obxInc;
  private Integer valueInc;
}
