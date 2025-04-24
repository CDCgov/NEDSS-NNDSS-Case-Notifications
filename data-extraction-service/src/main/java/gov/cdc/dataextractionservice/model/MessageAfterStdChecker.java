package gov.cdc.dataextractionservice.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

public class MessageAfterStdChecker {
    private long cnTransportqOutUid;
    private String messagePayload;
    private String notificationLocalId;
    private String publicHealthCaseLocalId;
    private String reportStatusCd;
    private String recordStatusCd;
    private String netssMessageOnly; // "queued", "NETSS_MESSAGE_ONLY", etc.
}
