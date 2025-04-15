package gov.cdc.casenotificationservice.service.interfaces;

import gov.cdc.casenotificationservice.model.Netss;
import gov.cdc.casenotificationservice.model.generated.jaxb.NBSNNDIntermediaryMessage;

public interface IStdMapperService {
    Netss stdMapping(NBSNNDIntermediaryMessage in);
}
