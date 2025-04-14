package gov.cdc.stdprocessorservice.constant;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StringConfig {

    public static final Set<String> LENGTH_2 = new HashSet<>(Arrays.asList(
            "state",
            "year",
            "week",
            "infosrce",
            "methodCaseDetectn",
            "netssVersion"
    ));

    public static final Set<String> LENGTH_3 = new HashSet<>(Arrays.asList(
            "siteCode",
            "county",
            "age",
            "totalNbrSexPartner12Month"
    ));

    public static final Set<String> LENGTH_4 = new HashSet<>(Arrays.asList(
            "city"
    ));

    public static final Set<String> LENGTH_5 = new HashSet<>(Arrays.asList(
            "event",
            "count",
            "future",
            "zip"
    ));

    public static final Set<String> LENGTH_6 = new HashSet<>(Arrays.asList(
            "caseReportId",
            "eventDate",
            "censusTract",
            "quantitativeSyphilisTestResult"
    ));

    public static final Set<String> LENGTH_8 = new HashSet<>(Arrays.asList(
            "dateOfBirth",
            "dxDate",
            "specimenSource",
            "dateInitHealthExam",
            "dateFirstReportOfCase",
            "treatmentDate",
            "dateReportedToCdc"
    ));
}
