package gov.cdc.notificationprocessor.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The DataTypeModel class This class is intended to be used for
 * deserialization of DataTypes JSON  into Java objects using Jackson annotations.
 */
public class DataTypeModel {
    private final String serialNumber;
    private final String questionIdentifier;
    private final String hl7SegmentField;
    private final String mmgVersion;
    private final String dataType;
    private final String coreDataType;
    private final String mask;
    private final String width;
    private final String note;

    @JsonCreator
    public DataTypeModel(@JsonProperty("serial_number") String serialNumber,
                         @JsonProperty("question_identifier") String questionIdentifier,
                         @JsonProperty("hl7_segment_field") String hl7SegmentField,
                         @JsonProperty("mmg_version") String mmgVersion,
                         @JsonProperty("data_type") String dataType,
                         @JsonProperty("core_data_type") String coreDataType,
                         @JsonProperty("mask") String mask,
                         @JsonProperty("width") String width,
                         @JsonProperty("note") String note) {
        this.serialNumber = serialNumber;
        this.questionIdentifier = questionIdentifier;
        this.hl7SegmentField = hl7SegmentField;
        this.mmgVersion = mmgVersion;
        this.dataType = dataType;
        this.coreDataType = coreDataType;
        this.mask = mask;
        this.width = width;
        this.note = note;
    }
    public String getSerialNumber() {
        return serialNumber;
    }

    public String getQuestionIdentifier() {
        return questionIdentifier;
    }

    public String getHl7SegmentField() {
        return hl7SegmentField;
    }

    public String getMmgVersion() {
        return mmgVersion;
    }

    public String getDataType() {
        return dataType;
    }

    public String getCoreDataType() {
        return coreDataType;
    }

    public String getMask() {
        return mask;
    }

    public String getWidth() {
        return width;
    }

    public String getNote() {
        return note;
    }

    @Override
    public String toString() {
        return "DataTypeModel{" +
                "  serialNumber=" + serialNumber +
                ", questionIdentifier=" + questionIdentifier +
                ", hl7SegmentField=" + hl7SegmentField +
                ", mmgVersion=" + mmgVersion +
                ", dataType= " + dataType +
                ", coreDataType = " + coreDataType +
                ", mask = " + mask +
                ", width = " + width +
                ", node = " + note;
    }

}
