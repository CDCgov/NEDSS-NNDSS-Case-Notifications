package gov.cdc.casenotificationservice.kafka.consumer;

import com.google.gson.Gson;
import gov.cdc.casenotificationservice.exception.IgnorableException;
import gov.cdc.casenotificationservice.exception.NonStdBatchProcessorServiceException;
import gov.cdc.casenotificationservice.exception.NonStdProcessorServiceException;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.service.deadletter.interfaces.IDltService;
import gov.cdc.casenotificationservice.service.nonstd.NonStdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

@Service
public class NonStdEventConsumer {
    private static final Logger logger = LoggerFactory.getLogger(StdEventConsumer.class); //NOSONAR
    private final NonStdService nonStdService;
    private final IDltService dltService;

    public NonStdEventConsumer(NonStdService nonStdService, IDltService dltService) {
        this.nonStdService = nonStdService;
        this.dltService = dltService;
    }

    @RetryableTopic(
            attempts = "${spring.kafka.retry.max-retry}",
            autoCreateTopics = "false",
            dltStrategy = DltStrategy.FAIL_ON_ERROR,
            retryTopicSuffix = "${spring.kafka.retry.suffix}",
            dltTopicSuffix = "${spring.kafka.dlt.suffix}",
            // retry topic name, such as topic-retry-1, topic-retry-2, etc
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            // time to wait before attempting to retry
            backoff = @Backoff(delay = 1000, multiplier = 2.0),
            include = {NonStdProcessorServiceException.class, NonStdBatchProcessorServiceException.class, IgnorableException.class}
    )
    @KafkaListener(
            topics = "${spring.kafka.topic.non-std-topic}",
            containerFactory = "kafkaListenerContainerFactoryConsumerForNonStd"
    )
    public void handleMessage(String message) throws IgnorableException, NonStdProcessorServiceException, NonStdBatchProcessorServiceException {
        var gson = new Gson();
        var data = gson.fromJson(message, MessageAfterStdChecker.class);
        nonStdService.nonStdProcessor(data);
    }

    @DltHandler()
    public void handleDlt(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.EXCEPTION_STACKTRACE) String stacktrace,
            @Header(KafkaHeaders.EXCEPTION_MESSAGE) String errorMessage,
            @Header(KafkaHeaders.EXCEPTION_CAUSE_FQCN) String exceptionRoot
    ) {
        logger.info("Received DLT message: {}", message);
        dltService.creatingDlt(message, topic, stacktrace, errorMessage, exceptionRoot);

    }
}
