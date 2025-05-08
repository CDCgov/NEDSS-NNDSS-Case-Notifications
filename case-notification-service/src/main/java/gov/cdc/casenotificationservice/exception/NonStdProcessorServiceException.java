package gov.cdc.casenotificationservice.exception;

public class NonStdProcessorServiceException extends Exception {
    public NonStdProcessorServiceException(String message) {
        super(message);
    }

    public NonStdProcessorServiceException(String message, Exception e) {
        super(message, e);
    }

    public NonStdProcessorServiceException(String message, Throwable e) {
        super(message, e);
    }
}