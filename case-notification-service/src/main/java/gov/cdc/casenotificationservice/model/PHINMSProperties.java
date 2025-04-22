package gov.cdc.casenotificationservice.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PHINMSProperties {
    String pNotificationId;
    String pPublicHealthCaseLocalId;
    String NETSS_MESSAGE_ONLY;
    String mappingERROR;
    String pReportStatusCd;
    String BATCH_MESSAGE_PROFILE_ID;

    String reportStatusCd;
    String pPHINMessageContent2;
    String pPHINEncryption;
    String pPHINRoute;
    String pPHINSignature;
    String pPHINProcessingStatus;
    String pPHINPublicKeyLdapAddress;
    String pPHINPublicKeyLdapBaseDN;
    String pPHINPublicKeyLdapDN;
    String pPHINMessageRecipient;
    String pPHINMessageID;
    String pPHINPriority;
    String pCurrentTimestamp;
    String pPHINCurrentTimestamp;
    String messageControlID1;
    String SENDING_APPLICATION = "";
    String SENDING_FACILITY = "";
    String pCertificateURL;
}