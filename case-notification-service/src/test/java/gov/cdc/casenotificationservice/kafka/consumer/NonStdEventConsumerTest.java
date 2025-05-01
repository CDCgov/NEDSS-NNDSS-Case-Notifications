package gov.cdc.casenotificationservice.kafka.consumer;

import com.google.gson.Gson;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.service.nonstd.NonStdService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.verify;

class NonStdEventConsumerTest {

    @Mock
    private NonStdService nonStdService;

    @InjectMocks
    private NonStdEventConsumer consumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testHandleMessage() throws Exception {
        // Arrange
        MessageAfterStdChecker mockChecker = new MessageAfterStdChecker();
        mockChecker.setCnTransportqOutUid(123L);
        String json = new Gson().toJson(mockChecker);

        // Act
        consumer.handleMessage(json);

        // Assert
        verify(nonStdService).nonStdProcessor(argThat(m -> m.getCnTransportqOutUid() == 123L));
    }

    @Test
    void testHandleDlt() {
        String message = "{\"cnTransportqOutUid\":456}";
        String topic = "nonstd-topic-dlt";

        assertDoesNotThrow(() -> consumer.handleDlt(message, topic));
    }
}
