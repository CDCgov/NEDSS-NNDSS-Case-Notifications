package gov.cdc.casenotificationservice.exception;

public class StdProcessorServiceException extends Exception {
    public StdProcessorServiceException(String message) {
        super(message);
    }

    public StdProcessorServiceException(String message, Exception e) {
        super(message, e);
    }

    public StdProcessorServiceException(String message, Throwable e) {
        super(message, e);
    }
}