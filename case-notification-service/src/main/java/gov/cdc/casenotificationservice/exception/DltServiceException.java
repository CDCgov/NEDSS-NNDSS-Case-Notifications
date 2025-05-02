package gov.cdc.casenotificationservice.exception;

public class DltServiceException extends Exception {
    public DltServiceException(String message) {
        super(message);
    }

    public DltServiceException(String message, Exception e) {
        super(message, e);
    }

    public DltServiceException(String message, Throwable e) {
        super(message, e);
    }
}