package gov.cdc.casenotificationservice.service.nonstd.interfaces;

import gov.cdc.casenotificationservice.exception.NonRetryableException;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;

public interface INonStdService {
    void nonStdProcessor(MessageAfterStdChecker messageAfterStdChecker, boolean hl7ValidationApplied) throws Exception;
    void releaseHoldQueueAndProcessBatchNonStd() throws NonRetryableException;
}