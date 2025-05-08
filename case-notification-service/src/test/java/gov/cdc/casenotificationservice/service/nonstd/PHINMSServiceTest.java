package gov.cdc.casenotificationservice.service.nonstd;


import gov.cdc.casenotificationservice.model.PHINMSProperties;
import gov.cdc.casenotificationservice.repository.msg.ServiceActionPairRepository;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import gov.cdc.casenotificationservice.repository.msg.model.ServiceActionPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class PHINMSServiceTest {

    private ServiceActionPairRepository serviceActionPairRepository;
    private PHINMSService phinmsService;

    @BeforeEach
    void setUp() {
        serviceActionPairRepository = mock(ServiceActionPairRepository.class);
        phinmsService = new PHINMSService(serviceActionPairRepository);
    }

    @Test
    void testGettingPHIMNSProperties_success() throws Exception {
        // Arrange
        String payload = readFileFromResources("payload_1.txt"); // You will create a simple sample HL7 string

        PHINMSProperties phinmsProperties = new PHINMSProperties();
        phinmsProperties.setPNotificationId("NOTIF-123");
        phinmsProperties.setPPublicHealthCaseLocalId("CASE-456");
        phinmsProperties.setPReportStatusCd("F");
        phinmsProperties.setNETSS_MESSAGE_ONLY("Processed");
        phinmsProperties.setMappingERROR("noerror");

        CaseNotificationConfig config = new CaseNotificationConfig();
        config.setPhinEncryption("encrypt");
        config.setPhinRoute("route");
        config.setPhinSignature("signature");
        config.setPhinPublicKeyAddress("ldapAddress");
        config.setPhinPublicKeyBaseDn("baseDn");
        config.setPhinPublicKeyDn("dn");
        config.setPhinRecipient("recipient");
        config.setPhinPriority("high");
        config.setNbsCertificateUrl("https://certificate.url");

        ServiceActionPair serviceActionPair = new ServiceActionPair();
        serviceActionPair.setTotalServiceActionPairs(1);

        when(serviceActionPairRepository.findTotal()).thenReturn(Collections.singletonList(serviceActionPair));

        // Act
        PHINMSProperties result = phinmsService.gettingPHIMNSProperties(payload, phinmsProperties, config);

        // Assert
        assertThat(result.getPPHINEncryption()).isEqualTo("encrypt");
        assertThat(result.getPPHINRoute()).isEqualTo("route");
        assertThat(result.getPPHINSignature()).isEqualTo("signature");
        assertThat(result.getPPHINMessageRecipient()).isEqualTo("recipient");
        assertThat(result.getPPHINPriority()).isEqualTo("high");
        assertThat(result.getPCertificateURL()).isEqualTo("https://certificate.url");

        verify(serviceActionPairRepository, times(1)).findTotal();
    }

    @Test
    void testGettingPHIMNSProperties_throwsExceptionWhenConditionCodeMissing() {
        // Arrange
        String payload = SampleHl7Message.ORU_R01_INVALID_NO_CONDITION;

        PHINMSProperties phinmsProperties = new PHINMSProperties();
        phinmsProperties.setPNotificationId("NOTIF-123");
        phinmsProperties.setPPublicHealthCaseLocalId("CASE-456");
        phinmsProperties.setPReportStatusCd("F");
        phinmsProperties.setNETSS_MESSAGE_ONLY("Processed");
        phinmsProperties.setMappingERROR("someError");

        CaseNotificationConfig config = new CaseNotificationConfig();
        config.setPhinEncryption("encrypt");

        ServiceActionPair serviceActionPair = new ServiceActionPair();
        serviceActionPair.setTotalServiceActionPairs(1);

        when(serviceActionPairRepository.findTotal()).thenReturn(Collections.singletonList(serviceActionPair));

        // Act + Assert
        assertThrows(Exception.class, () -> {
            phinmsService.gettingPHIMNSProperties(payload, phinmsProperties, config);
        });
    }

    private String readFileFromResources(String filename) throws IOException {
        String payload = "";
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filename);
             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
            scanner.useDelimiter("\\A"); // Read entire file
            payload = scanner.hasNext() ? scanner.next() : "";
        }
        return payload;
    }
}