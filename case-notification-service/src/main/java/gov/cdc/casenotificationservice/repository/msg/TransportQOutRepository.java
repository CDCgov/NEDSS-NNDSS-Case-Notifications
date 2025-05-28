package gov.cdc.casenotificationservice.repository.msg;

import gov.cdc.casenotificationservice.repository.msg.model.TransportQOut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransportQOutRepository extends JpaRepository<TransportQOut, Long> {
    /**
     * INSERT INTO transportq_out
     * 		 (messageCreationTime,messageId,payloadContent,processingStatus,routeInfo,service,action,
     * 		 priority,encryption,signature,publicKeyLdapAddress,publicKeyLdapBaseDN,publicKeyLdapDN,certificateURL,
     * 			destinationFilename,messageRecipient)
     * 			VALUES (
     * 			CONVERT(varchar(19),GETDATE(),126),
     * 			$pPHINMessageID, $pPHINMessageContent2, $pPHINProcessingStatus, $pPHINRoute,
     * 			$pPHINService, $pPHINAction,
     * 			$pPHINPriority, $pPHINEncryption, $pPHINSignature, $pPHINPublicKeyLdapAddress, $pPHINPublicKeyLdapBaseDN, $pPHINPublicKeyLdapDN,
     * 			$pCertificateURL, $reportStatusCd,$pPHINMessageRecipient)
     *
     *
     * 	NOTE: $pPHINMessageContent2 is encoded to binary -- maybe NOT
     * */

    @Query(value = "select * from TransportQ_out where messageId = :notificationLocalId ", nativeQuery = true)
    List<TransportQOut> findByNotificationLocalUid(@Param("notificationLocalId") String notificationLocalId);
}