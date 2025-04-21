package gov.cdc.xmlhl7parserservice.model;

public class DTM_TEMP {
    private int year;
    private Integer month;
    private Integer day;
    private Integer hours;
    private Integer minutes;
    private Integer seconds;
    private String separator;
    private Integer millis;
    private String gmtOffset;

    public int getYear() { return year; }
    public void setYear(int year) { this.year = year; }

    public Integer getMonth() { return month; }
    public void setMonth(Integer month) { this.month = month; }

    public Integer getDay() { return day; }
    public void setDay(Integer day) { this.day = day; }

    public Integer getHours() { return hours; }
    public void setHours(Integer hours) { this.hours = hours; }

    public Integer getMinutes() { return minutes; }
    public void setMinutes(Integer minutes) { this.minutes = minutes; }

    public Integer getSeconds() { return seconds; }
    public void setSeconds(Integer seconds) { this.seconds = seconds; }

    public String getSeparator() { return separator; }
    public void setSeparator(String separator) { this.separator = separator; }

    public Integer getMillis() { return millis; }
    public void setMillis(Integer millis) { this.millis = millis; }

    public String getGmtOffset() { return gmtOffset; }
    public void setGmtOffset(String gmtOffset) { this.gmtOffset = gmtOffset; }
}

