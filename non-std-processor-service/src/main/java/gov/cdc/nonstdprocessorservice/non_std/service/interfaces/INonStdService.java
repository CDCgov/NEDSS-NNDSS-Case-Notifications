package gov.cdc.nonstdprocessorservice.non_std.service.interfaces;

import gov.cdc.nonstdprocessorservice.non_std.model.PHINMSProperties;

public interface INonStdService {
    void nonStdProcessor(String payload, PHINMSProperties PHINMSProperties) throws Exception;
    void releaseHoldQueueAndProcessBatchNonStd() throws Exception;
}
