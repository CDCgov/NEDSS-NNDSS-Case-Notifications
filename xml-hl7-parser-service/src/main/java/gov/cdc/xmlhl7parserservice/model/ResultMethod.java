package gov.cdc.xmlhl7parserservice.model;

public class ResultMethod {
    private String code;
    private String nameOfCodingSystem;
    private String text;

    public ResultMethod() {}

    public ResultMethod(String code, String nameOfCodingSystem, String text) {
        this.code = code;
        this.nameOfCodingSystem = nameOfCodingSystem;
        this.text = text;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
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
}

