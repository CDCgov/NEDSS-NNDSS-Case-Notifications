package gov.cdc.xmlhl7parserservice.model;

public class ParentLink {
    private String identifier;
    private String nameOfCodingSystem;
    private String text;
    private String alternateIdentifier;
    private String nameOfAlternateCodingSystem;
    private String alternateText;
    private String observationSubID;
    private String observationValue;

    public ParentLink() {}

    public ParentLink(String identifier, String nameOfCodingSystem, String text,
                      String alternateIdentifier, String nameOfAlternateCodingSystem,
                      String alternateText, String observationSubID, String observationValue) {
        this.identifier = identifier;
        this.nameOfCodingSystem = nameOfCodingSystem;
        this.text = text;
        this.alternateIdentifier = alternateIdentifier;
        this.nameOfAlternateCodingSystem = nameOfAlternateCodingSystem;
        this.alternateText = alternateText;
        this.observationSubID = observationSubID;
        this.observationValue = observationValue;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getNameOfCodingSystem() {
        return nameOfCodingSystem;
    }

    public void setNameOfCodingSystem(String nameOfCodingSystem) {
        this.nameOfCodingSystem = nameOfCodingSystem;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAlternateIdentifier() {
        return alternateIdentifier;
    }

    public void setAlternateIdentifier(String alternateIdentifier) {
        this.alternateIdentifier = alternateIdentifier;
    }

    public String getNameOfAlternateCodingSystem() {
        return nameOfAlternateCodingSystem;
    }

    public void setNameOfAlternateCodingSystem(String nameOfAlternateCodingSystem) {
        this.nameOfAlternateCodingSystem = nameOfAlternateCodingSystem;
    }

    public String getAlternateText() {
        return alternateText;
    }

    public void setAlternateText(String alternateText) {
        this.alternateText = alternateText;
    }

    public String getObservationSubID() {
        return observationSubID;
    }

    public void setObservationSubID(String observationSubID) {
        this.observationSubID = observationSubID;
    }

    public String getObservationValue() {
        return observationValue;
    }

    public void setObservationValue(String observationValue) {
        this.observationValue = observationValue;
    }
}

