package gov.cdc.casenotificationservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MessageAfterStdChecker {
    private Long cnTransportqOutUid;
    private String messagePayload;
    private String notificationLocalId;
    private String publicHealthCaseLocalId;
    private String reportStatusCd;
    private String recordStatusCd;
    private String netssMessageOnly; // "queued", "NETSS_MESSAGE_ONLY", etc.
    private boolean stdMessageDetected;  // flag for STD detection
    private boolean reprocessApplied;
}
