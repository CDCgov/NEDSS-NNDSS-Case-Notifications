package gov.cdc.xmlhl7parserservice.repository.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "NNDSS_DATA_TYPE_LOOKUP")
public class DataTypeModel {
    @Id
    @Column(name = "serial_number" , columnDefinition="uniqueidentifier")
    private int serialNum;
    @Column(name = "question_identifier")
    private String questionId;
    @Column(name = "hl7_segment_field")
    private String hl7SegmentField;
    @Column(name = "mmg_version")
    private String mmgVersion;
    @Column(name = "data_type")
    private String dataType;
    @Column(name = "core_data_type")
    private String coreDataType;
    @Column(name = "mask")
    private String mask;
    @Column(name = "width")
    private int width;
    @Column(name = "note")
    private String note;

    public int getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getHl7SegmentField() {
        return hl7SegmentField;
    }

    public void setHl7SegmentField(String hl7SegmentField) {
        this.hl7SegmentField = hl7SegmentField;
    }

    public String getMmgVersion() {
        return mmgVersion;
    }

    public void setMmgVersion(String mmgVersion) {
        this.mmgVersion = mmgVersion;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getCoreDataType() {
        return coreDataType;
    }

    public void setCoreDataType(String coreDataType) {
        this.coreDataType = coreDataType;
    }

    public String getMask() {
        return mask;
    }

    public void setMask(String mask) {
        this.mask = mask;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}