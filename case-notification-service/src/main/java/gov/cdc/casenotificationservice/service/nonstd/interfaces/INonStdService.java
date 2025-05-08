package gov.cdc.casenotificationservice.service.nonstd.interfaces;

import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;

public interface INonStdService {
    void nonStdProcessor(MessageAfterStdChecker messageAfterStdChecker) throws Exception;
    void releaseHoldQueueAndProcessBatchNonStd();
}