package gov.cdc.casenotificationservice.service.nonstd;

import gov.cdc.casenotificationservice.cache.NonStdQueue;
import gov.cdc.casenotificationservice.model.PHINMSProperties;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NonStdNonStdBatchServiceTest {

    private NonStdNonStdBatchService batchService;

    @BeforeEach
    void setUp() {
        batchService = new NonStdNonStdBatchService();
        NonStdQueue.getInstance().clearPHINMSProperties(); // ensure clean state
    }

    @Test
    void testIsBatchConditionApplied_true() {
        var props = new PHINMSProperties();
        props.setMessageControlID1("MATCH");

        var config = new CaseNotificationConfig();
        config.setBatchMesageProfileId("MATCH");

        assertTrue(batchService.isBatchConditionApplied(props, config));
    }

    @Test
    void testIsBatchConditionApplied_false() {
        var props = new PHINMSProperties();
        props.setMessageControlID1("NO_MATCH");

        var config = new CaseNotificationConfig();
        config.setBatchMesageProfileId("EXPECTED");

        assertFalse(batchService.isBatchConditionApplied(props, config));
    }

    @Test
    void testHoldQueue() {
        var props = new PHINMSProperties();
        props.setPPHINMessageID("TEST");

        batchService.holdQueue(props);

        List<PHINMSProperties> queue = NonStdQueue.getInstance().getPhinmsPropertiesList();
        assertEquals(1, queue.size());
        assertEquals("TEST", queue.getFirst().getPPHINMessageID());
    }

    @Test
    void testReleaseQueuePopulateBatchFooterProperties() {
        var props1 = createPhinmsProps("ABC|XYZ|", "20250101", "Sender1", "Facility1");
        var props2 = createPhinmsProps("DEF|XYZ|", "20250101", "Sender1", "Facility1");

        NonStdQueue.getInstance().addPHINMSProperties(props1);
        NonStdQueue.getInstance().addPHINMSProperties(props2);

        PHINMSProperties result = batchService.ReleaseQueuePopulateBatchFooterProperties();

        assertNotNull(result);
        assertTrue(result.getPPHINMessageContent2().contains("FHS|"));
        assertTrue(result.getPPHINMessageContent2().contains("BTS|2|"));
        assertTrue(result.getPPHINMessageContent2().contains("FTS|1|"));
    }

    @Test
    void testBatchLogicWithMultipleMessages() {
        var props1 = new PHINMSProperties();
        props1.setPPHINMessageContent2("MSG1");

        var props2 = new PHINMSProperties();
        props2.setPPHINMessageContent2("MSG2");

        String result = batchService.batch(List.of(props1, props2));

        assertEquals("MSG1\rMSG2\rBTS|2|\rFTS|1|", result);
    }

    @Test
    void testBatchLogicEmptyQueue() {
        String result = batchService.batch(List.of());
        assertEquals("", result);
    }

    private PHINMSProperties createPhinmsProps(String content, String timestamp, String senderApp, String facility) {
        var props = new PHINMSProperties();
        props.setPPHINMessageContent2(content);
        props.setPCurrentTimestamp(timestamp);
        props.setSENDING_APPLICATION(senderApp);
        props.setSENDING_FACILITY(facility);
        return props;
    }
}
