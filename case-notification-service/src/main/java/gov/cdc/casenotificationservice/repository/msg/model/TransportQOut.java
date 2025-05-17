package gov.cdc.casenotificationservice.repository.msg.model;


import gov.cdc.casenotificationservice.model.PHINMSProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.sql.Blob;
import java.time.format.DateTimeFormatter;

import static gov.cdc.casenotificationservice.util.TimeStampHelper.getCurrentTimeStamp;

@Entity
@Table(name = "TransportQ_out", schema = "dbo")
@Data
public class TransportQOut {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long recordId;

    private String messageId;
    private String payloadFile;

    @Lob
    private Blob payloadContent;

    private String destinationFilename;
    private String routeInfo;
    private String service;
    private String action;
    private String arguments;
    private String messageRecipient;
    private String messageCreationTime;

    @Column(length = 10)
    private String encryption;

    @Column(length = 10)
    private String signature;

    private String publicKeyLdapAddress;
    private String publicKeyLdapBaseDN;
    private String publicKeyLdapDN;
    private String certificateURL;
    private String processingStatus;
    private String transportStatus;
    private String transportErrorCode;
    private String applicationStatus;
    private String applicationErrorCode;
    private String applicationResponse;
    private String messageSentTime;
    private String messageReceivedTime;
    private String responseMessageId;
    private String responseArguments;
    private String responseLocalFile;
    private String responseFilename;

    @Lob
    private Blob responseContent;

    private String responseMessageOrigin;
    private String responseMessageSignature;
    private Integer priority;

    public TransportQOut() {

    }

    public TransportQOut(PHINMSProperties props, String tz) {
        var ts = getCurrentTimeStamp(tz);
        String formatted = ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
        messageCreationTime = formatted;

        this.messageId = props.getPMessageUid();
        this.processingStatus = props.getPPHINProcessingStatus();
        this.routeInfo = props.getPPHINRoute();
        this.service = props.getSENDING_APPLICATION();
        this.action = props.getSENDING_FACILITY();
        this.priority = parsePriority(props.getPPHINPriority());
        this.encryption = props.getPPHINEncryption();
        this.signature = props.getPPHINSignature();
        this.publicKeyLdapAddress = props.getPPHINPublicKeyLdapAddress();
        this.publicKeyLdapBaseDN = props.getPPHINPublicKeyLdapBaseDN();
        this.publicKeyLdapDN = props.getPPHINPublicKeyLdapDN();
        this.certificateURL = props.getPCertificateURL();
        this.destinationFilename = props.getReportStatusCd();
        this.messageRecipient = props.getPPHINMessageRecipient();

        this.payloadContent = stringToBlob(props.getPPHINMessageContent2());
    }

    private Blob stringToBlob(String data) {
        if (data == null) return null;
        try {
            return new javax.sql.rowset.serial.SerialBlob(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert string to Blob", e);
        }
    }

    private Integer parsePriority(String val) {
        try {
            return val != null ? Integer.valueOf(val) : null;
        } catch (NumberFormatException e) {
            return null;
        }
    }
}