package gov.cdc.casenotificationservice.service.common.interfaces;

import gov.cdc.casenotificationservice.exception.APIException;
import gov.cdc.casenotificationservice.repository.odse.model.CNTransportqOut;

public interface IXmlHl7Service {
    String buildHl7Message(CNTransportqOut record, boolean hl7ValidationEnabled) throws APIException;
}
