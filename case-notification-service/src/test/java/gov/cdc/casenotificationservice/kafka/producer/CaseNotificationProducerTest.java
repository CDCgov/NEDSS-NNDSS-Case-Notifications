package gov.cdc.casenotificationservice.kafka.producer;

import static org.mockito.Mockito.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;

class CaseNotificationProducerTest {

  @Mock private KafkaTemplate<String, String> kafkaTemplate;

  @InjectMocks private CaseNotificationProducer producer;

  private AutoCloseable mockCloseable;

  private static final String TEST_TOPIC = "test-topic";
  private static final String TEST_PAYLOAD = "test-payload";

  @BeforeEach
  void beforeEach() {
    mockCloseable = MockitoAnnotations.openMocks(this);
  }

  @AfterEach
  void afterEach() throws Exception {
    mockCloseable.close();
  }

  @Test
  void testSendMessage_Success() {
    producer.sendMessage(TEST_PAYLOAD, TEST_TOPIC);

    verify(kafkaTemplate).send(eq(TEST_TOPIC), eq(TEST_PAYLOAD));
  }
}
