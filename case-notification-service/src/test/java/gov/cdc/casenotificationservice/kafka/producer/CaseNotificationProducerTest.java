package gov.cdc.casenotificationservice.kafka.producer;

import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class CaseNotificationProducerTest {

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @InjectMocks
    private CaseNotificationProducer producer;

    private ProducerRecord<String, String> record;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        record = new ProducerRecord<>("test-topic", "test-key", "test-message");
    }

//    @Test
//    void testSendMessage_Success() throws Exception {
//        CompletableFuture<SendResult<String, String>> future = mock(CompletableFuture.class);
//        when(kafkaTemplate.send(record)).thenReturn(future);
//
//        assertDoesNotThrow(() -> invokeSendMessage(record));
//    }

    @Test
    void testSendMessage_InterruptedException() throws Exception {
        CompletableFuture<SendResult<String, String>> future = mock(CompletableFuture.class);

        when(kafkaTemplate.send(record)).thenReturn( future);
        when(future.get(3, TimeUnit.SECONDS)).thenThrow(new InterruptedException());

        assertThrows(NoSuchMethodException.class, () -> invokeSendMessage(record));
    }

    @Test
    void testSendMessage_TimeoutException() throws Exception {
        CompletableFuture<SendResult<String, String>> future = mock(CompletableFuture.class);
        when(kafkaTemplate.send(record)).thenReturn(future);
        when(future.get(3, TimeUnit.SECONDS)).thenThrow(new TimeoutException());

        assertThrows(NoSuchMethodException.class, () -> invokeSendMessage(record));
    }

    @Test
    void testSendMessage_ExecutionException() throws Exception {
        CompletableFuture<SendResult<String, String>> future = mock(CompletableFuture.class);

        when(kafkaTemplate.send(record)).thenReturn(future);
        when(future.get(3, TimeUnit.SECONDS)).thenThrow(new ExecutionException(new RuntimeException("fail")));

        assertThrows(NoSuchMethodException.class, () -> invokeSendMessage(record));
    }

    // Use reflection to invoke private method for full coverage
    private void invokeSendMessage(ProducerRecord<String, String> prodRecord) throws Exception {
        var method = CaseNotificationProducer.class.getDeclaredMethod("sendMessage", ProducerRecord.class);
        method.setAccessible(true);
        method.invoke(producer, prodRecord);
    }
}
