package gov.cdc.dataextractionservice.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnvelopePayload {
    public CnTransportqOutValue before;
    public CnTransportqOutValue after;
    public SourceConnectorInfo source;
    public TransactionBlock transaction;
    public String op;
    public Long ts_ms;
    public Long ts_us;
    public Long ts_ns;
}
