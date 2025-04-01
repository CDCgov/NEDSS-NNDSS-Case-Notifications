package gov.cdc.notificationprocessor.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The DataTypeModel class This class is intended to be used for
 * deserialization of DataTypes JSON  into Java objects using Jackson annotations.
 */
public record DateTypeModel(String serialNumber, String questionIdentifier, String hl7SegmentField, String mmgVersion,
                            String dataType, String coreDataType, String mask, String width, String note) {
    @JsonCreator
    public DateTypeModel(@JsonProperty("serial_number") String serialNumber,
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
                ", note = " + note;
    }

}
