package gov.cdc.casenotificationservice.service.nonstd;
import gov.cdc.casenotificationservice.model.PHINMSProperties;
import gov.cdc.casenotificationservice.repository.msg.ServiceActionPairRepository;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.IPHINMSService;
import gov.cdc.dataingestion.hl7.helper.HL7Helper;
import gov.cdc.dataingestion.hl7.helper.model.HL7ParsedMessage;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_data_type.Hd;
import gov.cdc.dataingestion.hl7.helper.model.hl7.message_type.OruR1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static gov.cdc.casenotificationservice.constant.NonStdConstantValue.*;
import static gov.cdc.casenotificationservice.util.TimeStampHelper.getCurrentTimeStamp;


@Service
public class PHINMSService implements IPHINMSService {
    @Value("${service.timezone}")
    private String tz = "UTC";

//    @Value("${service.nbs-certificate-url")
//    private String NBS_CERTIFICATE_URL = "CERTIFICATE_URL";

    private final ServiceActionPairRepository serviceActionPairRepository;

    public PHINMSService(ServiceActionPairRepository serviceActionPairRepository) {
        this.serviceActionPairRepository = serviceActionPairRepository;
    }

    public PHINMSProperties gettingPHIMNSProperties(
            String payload,
            PHINMSProperties PHINMSProperties,
            CaseNotificationConfig caseNotificationConfig) throws Exception {
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
            throw new Exception();
        }


        var pNotificationID = PHINMSProperties.getPNotificationId();
        var reportStatusCd = PHINMSProperties.getPReportStatusCd();
        if(reportStatusCd.equalsIgnoreCase(REPORT_CD_F))
        {
            PHINMSProperties.setReportStatusCd(REPORT_STATUS_CD_1 + pNotificationID);
        }
        else if(reportStatusCd.equalsIgnoreCase(REPORT_CD_C))
        {
            PHINMSProperties.setReportStatusCd(REPORT_STATUS_CD_2 + pNotificationID);
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
        String monthStr = String.format(TS_FORMAT_CHARACTER, currentMonth);
        String dateStr = String.format(TS_FORMAT_CHARACTER, currentDate);
        String hourStr = String.format(TS_FORMAT_CHARACTER, currentHour);
        String minuteStr = String.format(TS_FORMAT_CHARACTER, currentMinute);
        String secondStr = String.format(TS_FORMAT_CHARACTER, currentSecond);

        String vFormattedTimestamp = currentYear + TS_DASH + monthStr + TS_DASH +
                dateStr + TS_T + hourStr + TS_COLON +
                minuteStr + TS_COLON + secondStr;

        String vCurrentTimestamp = currentYear + monthStr + dateStr + hourStr + minuteStr + secondStr;

        PHINMSProperties.setPPHINMessageContent2(payload);


        PHINMSProperties.setPPHINEncryption(caseNotificationConfig.getPhinEncryption());
        PHINMSProperties.setPPHINRoute(caseNotificationConfig.getPhinRoute()); // CDC Production
        PHINMSProperties.setPPHINSignature(caseNotificationConfig.getPhinSignature()); // CDC Production
        PHINMSProperties.setPPHINProcessingStatus(vProcessingStatus);
        PHINMSProperties.setPPHINPublicKeyLdapAddress(caseNotificationConfig.getPhinPublicKeyAddress());
        PHINMSProperties.setPPHINPublicKeyLdapBaseDN(caseNotificationConfig.getPhinPublicKeyBaseDn());
        PHINMSProperties.setPPHINPublicKeyLdapDN(caseNotificationConfig.getPhinPublicKeyDn());
        PHINMSProperties.setPPHINMessageRecipient(caseNotificationConfig.getPhinRecipient());
        PHINMSProperties.setPPHINMessageID(vMessageID);
        PHINMSProperties.setPPHINPriority(caseNotificationConfig.getPhinPriority());
        PHINMSProperties.setPCurrentTimestamp(vCurrentTimestamp);
        PHINMSProperties.setPPHINCurrentTimestamp(vFormattedTimestamp);
        var batchProfileId = PHINMSProperties.getBATCH_MESSAGE_PROFILE_ID();
        PHINMSProperties.setMessageControlID1(messageControlID1);

        // TODO: DOUBLE CHECK VALUE FOR THESE 2 HD OBJECT
        var sendApplicationStr = sendingApplication.getNameSpaceId() + CARET +
                sendingApplication.getUniversalId() + CARET + sendingApplication.getUniversalIdType();
        var sendFacilityStr = sendingFacility.getNameSpaceId() + CARET +
                sendingFacility.getUniversalId() + CARET + sendingFacility.getUniversalIdType();

        PHINMSProperties.setSENDING_APPLICATION(sendApplicationStr);
        PHINMSProperties.setSENDING_FACILITY(sendFacilityStr);

        PHINMSProperties.setPCertificateURL(caseNotificationConfig.getNbsCertificateUrl());

        return PHINMSProperties;
    }
}