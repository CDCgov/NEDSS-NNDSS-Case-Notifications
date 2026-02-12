package gov.cdc.casenotificationservice.service.common;

import gov.cdc.casenotificationservice.exception.APIException;
import gov.cdc.casenotificationservice.repository.odse.model.CNTransportqOut;
import gov.cdc.casenotificationservice.service.common.interfaces.IXmlHl7Service;
import gov.cdc.xmlhl7parserlib.helper.HL7MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class XmlHl7LibService implements IXmlHl7Service {
  private final HL7MessageBuilder hl7MessageBuilder;

  public XmlHl7LibService(HL7MessageBuilder hl7MessageBuilder) {
    this.hl7MessageBuilder = hl7MessageBuilder;
  }

  @Override
  public String buildHl7Message(CNTransportqOut record, boolean hl7ValidationEnabled) throws APIException {
    return hl7MessageBuilder.buildHl7Message(record.getMessagePayload(), hl7ValidationEnabled);
  }
}
