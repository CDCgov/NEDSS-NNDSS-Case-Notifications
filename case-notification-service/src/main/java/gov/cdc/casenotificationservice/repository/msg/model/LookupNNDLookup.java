package gov.cdc.casenotificationservice.repository.msg.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "LOOKUP_NNDLookup")
public class LookupNNDLookup {

    @Id
    @Column(name = "NND_ID", nullable = false, length = 50)
    private String nndId;

    @Column(name = "HL_LOC", nullable = false, length = 50)
    private String hlLoc;

    @Column(name = "NND_LABEL", nullable = false, length = 255)
    private String nndLabel;

    @Column(name = "FROM_UNIQUE_ID", nullable = false, length = 50)
    private String fromUniqueId;

    @Column(name = "FROM_QUESTION_NM", nullable = false, length = 255)
    private String fromQuestionNm;

    @Column(name = "FROM_CODE_SET_NM", nullable = false, length = 255)
    private String fromCodeSetNm;

    @Column(name = "CONCEPT_CD", nullable = false, length = 50)
    private String conceptCd;

    @Column(name = "CONCEPT_PREFERRED_NM", nullable = false, length = 255)
    private String conceptPreferredNm;

    @Column(name = "FROM_CODE", nullable = false, length = 50)
    private String fromCode;

    @Column(name = "FROM_DISPLAY_NM", nullable = false, length = 255)
    private String fromDisplayNm;

    @Column(name = "NBS_CLASS_CD", nullable = false, length = 50)
    private String nbsClassCd;

    @Column(name = "TO_UNIQUE_ID", nullable = false, length = 50)
    private String toUniqueId;

    @Column(name = "TO_QUESTION_NM", nullable = false, length = 255)
    private String toQuestionNm;

    @Column(name = "TO_CODE_SET_NM", nullable = false, length = 255)
    private String toCodeSetNm;

    @Column(name = "TO_CODE", nullable = false, length = 50)
    private String toCode;

    @Column(name = "TO_DISPLAY_NM", nullable = false, length = 255)
    private String toDisplayNm;

    @Column(name = "NOTES", length = 500)
    private String notes;
}
