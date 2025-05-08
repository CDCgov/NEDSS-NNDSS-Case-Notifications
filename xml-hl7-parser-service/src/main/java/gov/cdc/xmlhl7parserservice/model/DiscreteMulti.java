package gov.cdc.xmlhl7parserservice.model;

public class DiscreteMulti {
    private String code;
    private String indicatorCode;
    private String cweQuestionIdentifier;
    private String snuIndicatorCode;
    private int counter;
    private int obsValueCounter;
    private String otherText;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getIndicatorCode() {
        return indicatorCode;
    }

    public void setIndicatorCode(String indicatorCode) {
        this.indicatorCode = indicatorCode;
    }

    public String getCweQuestionIdentifier() {
        return cweQuestionIdentifier;
    }

    public void setCweQuestionIdentifier(String cweQuestionIdentifier) {
        this.cweQuestionIdentifier = cweQuestionIdentifier;
    }

    public String getSnuIndicatorCode() {
        return snuIndicatorCode;
    }

    public void setSnuIndicatorCode(String snuIndicatorCode) {
        this.snuIndicatorCode = snuIndicatorCode;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getObsValueCounter() {
        return obsValueCounter;
    }

    public void setObsValueCounter(int obsValueCounter) {
        this.obsValueCounter = obsValueCounter;
    }

    public String getOtherText() {
        return otherText;
    }

    public void setOtherText(String otherText) {
        this.otherText = otherText;
    }
}
