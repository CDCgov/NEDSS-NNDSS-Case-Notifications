package gov.cdc.casenotificationservice.service.common.interfaces;

import gov.cdc.casenotificationservice.exception.XmlHl7ParsingException;

public interface IXmlHl7Service {
    String buildHl7Message(String xmlPayload, boolean hl7ValidationEnabled) throws XmlHl7ParsingException;
}
