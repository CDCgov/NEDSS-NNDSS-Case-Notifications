package gov.cdc.dataextractionservice.service;

import gov.cdc.dataextractionservice.model.CnTransportqOutValue;
import gov.cdc.dataextractionservice.model.MessageAfterStdChecker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j

public class StdCheckerTransformerService {

    @Value("${app.transformer.netss-message-only}")
    private String netssMessageOnlyConfig;

    public MessageAfterStdChecker transform(CnTransportqOutValue value) {
        if (value == null) {
            log.warn("TransformerService: Received null payload.");
            return null;
        }

        if (!"UNPROCESSED".equalsIgnoreCase(value.getRecord_status_cd())) {
            log.info("Skipping message: record_status_cd is not UNPROCESSED.");
            return null;
        }

        String body = value.getMessage_payload();
        if (body == null) {
            log.warn("TransformerService: message_payload is null.");
            return null;
        }

        // Replace problematic characters
        String cleanedPayload = body
                .replaceAll("'", "&apos;")
                .replaceAll("â€™", "&apos;");

        // Check if body contains <stringData>STD_MMG_V1.0</stringData>
        boolean containsTrigger = cleanedPayload.contains("<stringData>STD_MMG_V1.0</stringData>");
        String netssStatus = "queued";

        if (containsTrigger) {
            switch (netssMessageOnlyConfig) {
                case "NETSS_MESSAGE_ONLY":
                    netssStatus = "NETSS_MESSAGE_ONLY";
                    break;
                case "BOTH":
                    netssStatus = "BOTH";
                    break;
                default:
                    netssStatus = "queued";
            }
        }

        return MessageAfterStdChecker.builder()
                .cnTransportqOutUid(value.getCn_transportq_out_uid())
                .messagePayload(cleanedPayload)
                .notificationLocalId(value.getNotification_local_id())
                .publicHealthCaseLocalId(value.getPublic_health_case_local_id())
                .reportStatusCd(value.getReport_status_cd())
                .recordStatusCd(value.getRecord_status_cd())
                .netssMessageOnly(netssStatus)
                .build();
    }
}
