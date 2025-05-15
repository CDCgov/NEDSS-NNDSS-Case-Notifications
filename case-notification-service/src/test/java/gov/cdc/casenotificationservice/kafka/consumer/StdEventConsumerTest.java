package gov.cdc.casenotificationservice.kafka.consumer;


import com.google.gson.Gson;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.service.std.interfaces.IXmlService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

class StdEventConsumerTest {

    @Mock
    private IXmlService xmlService;

    @InjectMocks
    private StdEventConsumer consumer;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleMessage_Success() throws Exception {
        // Arrange
        MessageAfterStdChecker checker = new MessageAfterStdChecker();
        checker.setCnTransportqOutUid(100L);
        checker.setMessagePayload("org.apache.kafka.connect.data");
        String json = new Gson().toJson(checker);

        // Act
        consumer.handleMessage(json);

        // Assert
        verify(xmlService).mappingXmlStringToObject(argThat(m -> m.getCnTransportqOutUid() == 100L));
    }

//    @Test
//    void testHandleMessage_ExceptionHandled() throws Exception {
//        // Arrange
//        MessageAfterStdChecker checker = new MessageAfterStdChecker();
//        checker.setCnTransportqOutUid(200L);
//        checker.setMessagePayload("org.apache.kafka.connect.data");
//        String json = new Gson().toJson(checker);
//
//        doThrow(new RuntimeException("Simulated failure"))
//                .when(xmlService).mappingXmlStringToObject(any());
//
//        // Act & Assert: exception should be caught and logged (not thrown)
//        assertDoesNotThrow(() -> consumer.handleMessage(json));
//    }

//    @Test
//    void testHandleDlt() {
//        assertDoesNotThrow(() -> consumer.handleDlt("dead-letter-message", "std-topic-dlt"));
//    }
}
