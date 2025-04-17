package gov.cdc.dataextractionservice.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransactionBlock {
    public String id;
    public long total_order;
    public long data_collection_order;
}
