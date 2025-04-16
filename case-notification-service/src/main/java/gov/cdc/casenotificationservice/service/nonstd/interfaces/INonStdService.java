package gov.cdc.casenotificationservice.service.nonstd.interfaces;

import gov.cdc.casenotificationservice.model.PHINMSProperties;

public interface INonStdService {
    void nonStdProcessor(String payload) throws Exception;
    void releaseHoldQueueAndProcessBatchNonStd() throws Exception;
}