package gov.cdc.dataextractionservice.service;

import gov.cdc.dataextractionservice.model.CnTransportqOutValue;
import gov.cdc.dataextractionservice.model.MessageAfterStdChecker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class StdCheckerTransformerServiceTest {

    private StdCheckerTransformerService transformerService;

    @BeforeEach
    void setUp() {
        transformerService = new StdCheckerTransformerService();
    }

    @Test
    void testTransform_NullInput() {
        MessageAfterStdChecker result = transformerService.transform(null);
        assertNull(result);
    }

    @Test
    void testTransform_RecordStatusCdNotUnprocessed() {
        var input = new CnTransportqOutValue();
        input.setRecord_status_cd("PROCESSED");

        MessageAfterStdChecker result = transformerService.transform(input);
        assertNull(result);
    }

    @Test
    void testTransform_NullPayload() {
        var input = new CnTransportqOutValue();
        input.setRecord_status_cd("UNPROCESSED");
        input.setMessage_payload(null);

        MessageAfterStdChecker result = transformerService.transform(input);
        assertNull(result);
    }

    @Test
    void testTransform_STDTrigger_NETSS_MESSAGE_ONLY() {
        ReflectionTestUtils.setField(transformerService, "netssMessageOnlyConfig", "NETSS_MESSAGE_ONLY");

        var input = new CnTransportqOutValue();
        input.setRecord_status_cd("UNPROCESSED");
        input.setMessage_payload("<stringData>STD_MMG_V1.0</stringData>");
        input.setCn_transportq_out_uid(123L);
        input.setNotification_local_id("NID123");
        input.setPublic_health_case_local_id("PHCID123");
        input.setReport_status_cd("REPORT123");

        MessageAfterStdChecker result = transformerService.transform(input);

        assertNotNull(result);
        assertTrue(result.isStdMessageDetected());
        assertEquals("NETSS_MESSAGE_ONLY", result.getNetssMessageOnly());
        assertEquals("NID123", result.getNotificationLocalId());
        assertEquals("PHCID123", result.getPublicHealthCaseLocalId());
        assertEquals("REPORT123", result.getReportStatusCd());
    }

    @Test
    void testTransform_STDTrigger_BOTH() {
        ReflectionTestUtils.setField(transformerService, "netssMessageOnlyConfig", "BOTH");

        var input = new CnTransportqOutValue();
        input.setRecord_status_cd("UNPROCESSED");
        input.setMessage_payload("<stringData>STD_MMG_V1.0</stringData>");

        MessageAfterStdChecker result = transformerService.transform(input);

        assertNotNull(result);
        assertEquals("BOTH", result.getNetssMessageOnly());
    }

    @Test
    void testTransform_STDTrigger_DefaultQueued() {
        ReflectionTestUtils.setField(transformerService, "netssMessageOnlyConfig", "UNKNOWN");

        var input = new CnTransportqOutValue();
        input.setRecord_status_cd("UNPROCESSED");
        input.setMessage_payload("<stringData>STD_MMG_V1.0</stringData>");

        MessageAfterStdChecker result = transformerService.transform(input);

        assertNotNull(result);
        assertEquals("queued", result.getNetssMessageOnly());
    }

    @Test
    void testTransform_ReplacesSpecialCharacters() {
        ReflectionTestUtils.setField(transformerService, "netssMessageOnlyConfig", "NETSS_MESSAGE_ONLY");
        var input = new CnTransportqOutValue();
        input.setRecord_status_cd("UNPROCESSED");
        input.setMessage_payload("Don't use â€™ or ' characters<stringData>STD_MMG_V1.0</stringData>");
        MessageAfterStdChecker result = transformerService.transform(input);
        assertNotNull(result);
    }
}
