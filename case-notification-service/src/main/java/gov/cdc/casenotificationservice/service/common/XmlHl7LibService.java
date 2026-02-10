package gov.cdc.casenotificationservice.service.common;

import gov.cdc.casenotificationservice.exception.APIException;
import gov.cdc.casenotificationservice.repository.odse.CNTraportqOutRepository;
import gov.cdc.casenotificationservice.repository.odse.model.CNTransportqOut;
import gov.cdc.casenotificationservice.service.common.interfaces.IXmlHl7Service;
import gov.cdc.xmlhl7parserlib.helper.HL7MessageBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
@ConditionalOnProperty(name = "xmlhl7.parser.mode", havingValue = "library", matchIfMissing = true)
public class XmlHl7LibService implements IXmlHl7Service {
  private final CNTraportqOutRepository cnTraportqOutRepository;
  private final HL7MessageBuilder hl7MessageBuilder;

  public XmlHl7LibService(CNTraportqOutRepository cnTraportqOutRepository, HL7MessageBuilder hl7MessageBuilder) {
    this.cnTraportqOutRepository = cnTraportqOutRepository;
    this.hl7MessageBuilder = hl7MessageBuilder;
  }

  @Override
  public String buildHl7Message(String recordId, boolean hl7ValidationEnabled) throws APIException {
    var xmlPayload = cnTraportqOutRepository.findTopByRecordUid(Long.valueOf(recordId));
    return hl7MessageBuilder.buildHl7Message(xmlPayload.getMessagePayload(), hl7ValidationEnabled);
  }
}
