package gov.cdc.casenotificationservice.service.cntransportqout;

import gov.cdc.casenotificationservice.model.CnTransportqOutValue;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
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

    String recordStatusCd = value.getRecord_status_cd();
    if (!"UNPROCESSED".equalsIgnoreCase(recordStatusCd)) {
      log.info("Skipping message: record_status_cd is not UNPROCESSED or missing.");
      return null;
    }

    String body = value.getMessage_payload();
    if (body == null) {
      log.warn("TransformerService: message_payload is null.");
      return null;
    }

    // Replace problematic characters
    String cleanedPayload = body.replaceAll("'", "&apos;").replaceAll("â€™", "&apos;");

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

    MessageAfterStdChecker msg = new MessageAfterStdChecker();

    msg.setCnTransportqOutUid(value.getCn_transportq_out_uid());
    // n.b.: this was commented out previous to being consolidated into case-notification-service
    //  msg.setMessagePayload(cleanedPayload);
    msg.setNotificationLocalId(value.getNotification_local_id());
    msg.setPublicHealthCaseLocalId(value.getPublic_health_case_local_id());
    msg.setReportStatusCd(value.getReport_status_cd());
    msg.setRecordStatusCd(value.getRecord_status_cd());
    msg.setNetssMessageOnly(netssStatus);
    msg.setStdMessageDetected(containsTrigger);

    return msg;
  }
}
