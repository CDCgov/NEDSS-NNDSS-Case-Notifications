package gov.cdc.casenotificationservice.model.view_model;

import gov.cdc.casenotificationservice.repository.msg.model.TransportQOut;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;
import lombok.Getter;
import lombok.Setter;

import java.sql.Blob;

@Getter
@Setter
public class TransportOutVM {
    private Long recordId;
    private String messageId;
    private String payloadFile;
    private String destinationFilename;
    private String routeInfo;
    private String service;
    private String action;
    private String arguments;
    private String messageRecipient;
    private String messageCreationTime;
    private String encryption;
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
    private String responseMessageOrigin;
    private String responseMessageSignature;
    private Integer priority;

    public TransportOutVM() {

    }

    public TransportOutVM(TransportQOut transportQOut) {}
}
