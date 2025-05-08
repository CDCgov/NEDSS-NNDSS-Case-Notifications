package gov.cdc.xmlhl7parserservice.model;

public class DiscreteRepeat {
    private String code;
    private String indicatorCode;
    private String mappedIndicatorCode;
    private int obx4counter;
    private int counter;
    private int obxCounter;
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

    public String getMappedIndicatorCode() {
        return mappedIndicatorCode;
    }

    public void setMappedIndicatorCode(String mappedIndicatorCode) {
        this.mappedIndicatorCode = mappedIndicatorCode;
    }

    public int getObx4counter() {
        return obx4counter;
    }

    public void setObx4counter(int obx4counter) {
        this.obx4counter = obx4counter;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public int getObxCounter() {
        return obxCounter;
    }

    public void setObxCounter(int obxCounter) {
        this.obxCounter = obxCounter;
    }

    public String getOtherText() {
        return otherText;
    }

    public void setOtherText(String otherText) {
        this.otherText = otherText;
    }
}

