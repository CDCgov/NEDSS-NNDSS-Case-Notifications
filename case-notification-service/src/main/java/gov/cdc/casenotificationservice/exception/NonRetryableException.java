package gov.cdc.casenotificationservice.exception;

public class NonRetryableException extends Exception{
    public NonRetryableException(String message) {
        super(message);
    }
    public NonRetryableException(String message, Throwable cause) {
        super(message, cause);
    }
}
