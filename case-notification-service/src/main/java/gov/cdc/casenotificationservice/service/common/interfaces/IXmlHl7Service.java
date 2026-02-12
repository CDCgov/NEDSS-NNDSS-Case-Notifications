package gov.cdc.casenotificationservice.service.common.interfaces;

import gov.cdc.casenotificationservice.exception.APIException;

public interface IXmlHl7Service {
    String buildHl7Message(String xmlPayload, boolean hl7ValidationEnabled) throws APIException;
}
