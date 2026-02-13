package gov.cdc.casenotificationservice.exception;

public class XmlHl7ParsingException extends Exception{
    public XmlHl7ParsingException(String message) {
        super(message);
    }
    public XmlHl7ParsingException(String message, Throwable cause) {
        super(message, cause);
    }
}
