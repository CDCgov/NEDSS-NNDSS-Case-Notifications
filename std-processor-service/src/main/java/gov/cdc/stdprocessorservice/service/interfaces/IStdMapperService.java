package gov.cdc.stdprocessorservice.service.interfaces;

import gov.cdc.stdprocessorservice.model.Netss;
import gov.cdc.stdprocessorservice.model.generated.jaxb.NBSNNDIntermediaryMessage;

public interface IStdMapperService {
    Netss stdMapping(NBSNNDIntermediaryMessage in);
}
