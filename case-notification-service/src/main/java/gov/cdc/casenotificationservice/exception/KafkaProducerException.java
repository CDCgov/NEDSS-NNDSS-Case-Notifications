package gov.cdc.casenotificationservice.exception;

public class KafkaProducerException extends Exception{
    public KafkaProducerException(String message) {
        super(message);
    }
}
