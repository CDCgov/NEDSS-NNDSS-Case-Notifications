package gov.cdc.casenotificationservice.service.nonstd.interfaces;

import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.model.PHINMSProperties;

public interface INonStdService {
    void nonStdProcessor(MessageAfterStdChecker messageAfterStdChecker) throws Exception;
    void releaseHoldQueueAndProcessBatchNonStd() throws Exception;
}