package gov.cdc.casenotificationservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NetssPersistModel {
    private String netss;
    private String vCaseReptId;
    private String vMessageWeek;
    private String vMessageYr;
    private String recordStatusCd;
}
