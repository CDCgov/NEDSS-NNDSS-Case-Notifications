package gov.cdc.nonstdprocessorservice.non_std.repository.msg.model;

import jakarta.persistence.*;
import lombok.Data;

import java.sql.Blob;

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
}