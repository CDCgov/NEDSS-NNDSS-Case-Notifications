package gov.cdc.casenotificationservice.constant;

public class NonStdConstantValue {
    public static final String REPORT_STATUS_CD_1 = "CDCNND1";
    public static final String REPORT_STATUS_CD_2 = "CDCNND2";
    public static final String REPORT_CD_F = "F";
    public static final String REPORT_CD_C = "C";
    public static final String TS_DASH = "-";
    public static final String TS_T = "T";
    public static final String TS_COLON = ":";
    public static final String TS_FORMAT_CHARACTER = "%02d";
    public static final String CARET = "^";

    public static final String HL7_BATCH_HEADER_TEMPLATE =
    "FHS|^~\\&|%s|PHINCDS^2.16.840.1.114222.4.3.2.10^ISO|PHIN^2.16.840.1.114222^ISO|%s|||||\r" +
    "BHS|^~\\&|%s|PHINCDS^2.16.840.1.114222.4.3.2.10^ISO|PHIN^2.16.840.1.114222^ISO|%s|||||\r";
    public static final String HL7_BATCH_FOOTER_BTS = "\rBTS|";
    public static final String HL7_BATCH_FOOTER_FTS = "|\rFTS|1|";
    public static final String HL7_PIPE = "|";
    public static final String HL7_NEWLINE = "\r";
}
