package gov.cdc.casenotificationservice.exception;

public class IgnorableException extends Exception {
    public IgnorableException(String message) {
        super(message);
    }

    public IgnorableException(String message, Exception e) {
        super(message, e);
    }

    public IgnorableException(String message, Throwable e) {
        super(message, e);
    }
}