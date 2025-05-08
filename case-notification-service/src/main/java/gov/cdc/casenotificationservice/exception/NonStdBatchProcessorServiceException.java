package gov.cdc.casenotificationservice.exception;

public class NonStdBatchProcessorServiceException extends Exception {
    public NonStdBatchProcessorServiceException(String message) {
        super(message);
    }

    public NonStdBatchProcessorServiceException(String message, Exception e) {
        super(message, e);
    }

    public NonStdBatchProcessorServiceException(String message, Throwable e) {
        super(message, e);
    }
}