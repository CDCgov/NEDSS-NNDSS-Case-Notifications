package gov.cdc.dataextractionservice.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SourceConnectorInfo {
    public String version;
    public String connector;
    public String name;
    public long ts_ms;
    public String snapshot;
    public String db;
    public String sequence;
    public Long ts_us;
    public Long ts_ns;
    public String schema;
    public String table;
    public String change_lsn;
    public String commit_lsn;
    public Long event_serial_no;
}
