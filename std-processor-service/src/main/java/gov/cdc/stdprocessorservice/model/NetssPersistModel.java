package gov.cdc.stdprocessorservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NetssPersistModel {
    private Netss netss;
    private String vCaseReptId;
    private String vMessageWeek;
    private String vMessageYr;
    private String pRecordStatus;
    private String recordStatusCd;
}
