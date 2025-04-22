package gov.cdc.casenotificationservice.service.nonstd;
import gov.cdc.casenotificationservice.model.PHINMSProperties;
import gov.cdc.casenotificationservice.repository.msg.ServiceActionPairRepository;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.IPHINMSService;
import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Hd;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_type.OruR1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static gov.cdc.casenotificationservice.util.TimeStampHelper.getCurrentTimeStamp;


@Service
public class PHINMSService implements IPHINMSService {
    @Value("${service.timezone}")
    private String tz = "UTC";

    @Value("${service.nbs-certificate-url")
    private String NBS_CERTIFICATE_URL = "CERTIFICATE_URL";

    private final ServiceActionPairRepository serviceActionPairRepository;

    public PHINMSService(ServiceActionPairRepository serviceActionPairRepository) {
        this.serviceActionPairRepository = serviceActionPairRepository;
    }

    public PHINMSProperties gettingPHIMNSProperties(
            String payload,
            PHINMSProperties PHINMSProperties) throws Exception {
        Hd sendingFacility = null;
        Hd sendingApplication = null;
        var counterInt =0;

        var serviceActionPairs = this.serviceActionPairRepository.findTotal();
        Integer counterString = serviceActionPairs.getFirst().getTotalServiceActionPairs();
        if (counterString == null) {
            counterString = 1;
        }

        var statusCode="ACTIVE";
        var messageControlID1 ="";
        var conditionCode= new ArrayList<String>();
        var mappedOnce= false;
        var SAPConcatenated="";


        HL7Helper hl7Helper = new HL7Helper();
        HL7ParsedMessage<OruR1> parsedMessage = hl7Helper.hl7StringParser(payload);

        var vMessageID = PHINMSProperties.getPNotificationId();
        var vPublicHealthCaseLocalId = PHINMSProperties.getPPublicHealthCaseLocalId();

        for(var patResult : parsedMessage.getParsedMessage().getPatientResult()) {
            conditionCode.add(patResult.getOrderObservation().getFirst().getObservationRequest().getReasonForStudy().getFirst().getIdentifier());
        }

        if (parsedMessage.getParsedMessage().getMessageHeader().getMessageProfileIdentifier().size() > 1) {
            // In original code, this was get value at INDEX 2
            messageControlID1 = parsedMessage.getParsedMessage().getMessageHeader().getMessageProfileIdentifier().get(1).getEntityIdentifier();
        }
        sendingApplication = parsedMessage.getParsedMessage().getMessageHeader().getSendingApplication();
        sendingFacility = parsedMessage.getParsedMessage().getMessageHeader().getSendingFacility();

        if(messageControlID1==null || messageControlID1.isEmpty()) {
            // In original code, this was get value at INDEX 1
            messageControlID1 = parsedMessage.getParsedMessage().getMessageHeader().getMessageProfileIdentifier().getFirst().getEntityIdentifier();
        }

        var mappingERROR= PHINMSProperties.getMappingERROR();

        if(conditionCode.isEmpty() && (mappingERROR != null && !mappingERROR.isEmpty())){
            // mappingERROR Exception
            throw new Exception();
        }

//        next.getField("PATIENT_RESULT.ORDER_OBSERVATION[*].OBR[0].FillerOrderNumber.EntityIdentifier");

        var pNotificationID = PHINMSProperties.getPNotificationId();
        var reportStatusCd = PHINMSProperties.getPReportStatusCd();
        if(reportStatusCd.equalsIgnoreCase("F"))
        {
            PHINMSProperties.setReportStatusCd("CDCNND1" + pNotificationID);
        }
        else if(reportStatusCd.equalsIgnoreCase("C"))
        {
            PHINMSProperties.setReportStatusCd("CDCNND2" + pNotificationID);
        }

        var vProcessingStatus = PHINMSProperties.getNETSS_MESSAGE_ONLY();
        var currentTime = getCurrentTimeStamp(tz);

        LocalDateTime localDateTime = currentTime.toLocalDateTime();

        int currentYear = localDateTime.getYear();
        int currentMonth = localDateTime.getMonthValue();
        int currentDate = localDateTime.getDayOfMonth();
        int currentHour = localDateTime.getHour();
        int currentMinute = localDateTime.getMinute();
        int currentSecond = localDateTime.getSecond();

        // Zero-padded formatting
        String monthStr = String.format("%02d", currentMonth);
        String dateStr = String.format("%02d", currentDate);
        String hourStr = String.format("%02d", currentHour);
        String minuteStr = String.format("%02d", currentMinute);
        String secondStr = String.format("%02d", currentSecond);

        String vFormattedTimestamp = currentYear + "-" + monthStr + "-" + dateStr + "T" +
                hourStr + ":" + minuteStr + ":" + secondStr;

        String vCurrentTimestamp = currentYear + monthStr + dateStr + hourStr + minuteStr + secondStr;

        PHINMSProperties.setPPHINMessageContent2(payload);


        PHINMSProperties.setPPHINEncryption("yes");
        PHINMSProperties.setPPHINRoute("CDC"); // CDC Production
        PHINMSProperties.setPPHINSignature("no"); // CDC Production
        PHINMSProperties.setPPHINProcessingStatus(vProcessingStatus);
        PHINMSProperties.setPPHINPublicKeyLdapAddress("directory.pki.digicert.com:389");
        PHINMSProperties.setPPHINPublicKeyLdapBaseDN("o=Centers for Disease Control and Prevention");
        PHINMSProperties.setPPHINPublicKeyLdapDN("cn=cdc phinms");
        PHINMSProperties.setPPHINMessageRecipient("CDC");
        PHINMSProperties.setPPHINMessageID(vMessageID);
        PHINMSProperties.setPPHINPriority("1");
        PHINMSProperties.setPCurrentTimestamp(vCurrentTimestamp);
        PHINMSProperties.setPPHINCurrentTimestamp(vFormattedTimestamp);
        var batchProfileId = PHINMSProperties.getBATCH_MESSAGE_PROFILE_ID();
        PHINMSProperties.setMessageControlID1(messageControlID1);

        // TODO: DOUBLE CHECK VALUE FOR THESE 2 HD OBJECT
        var sendApplicationStr = sendingApplication.getNameSpaceId() + "^" + sendingApplication.getUniversalId() + "^" + sendingApplication.getUniversalIdType();
        var sendFacilityStr = sendingFacility.getNameSpaceId() + "^" + sendingFacility.getUniversalId() + "^" + sendingFacility.getUniversalIdType();

        PHINMSProperties.setSENDING_APPLICATION(sendApplicationStr);
        PHINMSProperties.setSENDING_FACILITY(sendFacilityStr);

        PHINMSProperties.setPCertificateURL(NBS_CERTIFICATE_URL);

        return PHINMSProperties;
    }
}