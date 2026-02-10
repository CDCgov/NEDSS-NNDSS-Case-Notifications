package gov.cdc.casenotificationservice.service.common;

import gov.cdc.casenotificationservice.exception.APIException;
import gov.cdc.casenotificationservice.repository.odse.CNTraportqOutRepository;
import gov.cdc.casenotificationservice.service.common.interfaces.IXmlHl7Service;

public class XmlHl7LibService implements IXmlHl7Service {
  private final CNTraportqOutRepository cnTraportqOutRepository;

  public XmlHl7LibService(CNTraportqOutRepository cnTraportqOutRepository) {
    this.cnTraportqOutRepository = cnTraportqOutRepository;
  }

  @Override
  public String buildHl7Message(String recordId, boolean hl7ValidationEnabled) throws APIException {
    return "";
  }
}
