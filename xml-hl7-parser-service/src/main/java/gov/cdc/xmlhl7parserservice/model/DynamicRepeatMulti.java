package gov.cdc.xmlhl7parserservice.model;

public class DynamicRepeatMulti {
    String parentCode;
    int obx4counter;
    String partIndicator;
    int repeating;

    public String getParentCode() {
        return parentCode;
    }

    public void setParentCode(String parentCode) {
        this.parentCode = parentCode;
    }

    public int getObx4counter() {
        return obx4counter;
    }

    public void setObx4counter(int obx4counter) {
        this.obx4counter = obx4counter;
    }

    public String getPartIndicator() {
        return partIndicator;
    }

    public void setPartIndicator(String partIndicator) {
        this.partIndicator = partIndicator;
    }

    public int getRepeating() {
        return repeating;
    }

    public void setRepeating(int repeating) {
        this.repeating = repeating;
    }
}

