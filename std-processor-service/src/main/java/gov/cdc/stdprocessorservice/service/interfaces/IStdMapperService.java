package gov.cdc.stdprocessorservice.service.interfaces;

import gov.cdc.stdprocessorservice.model.generated.jaxb.NBSNNDIntermediaryMessage;

public interface IStdMapperService {
    void stdMapping(NBSNNDIntermediaryMessage in);
}
