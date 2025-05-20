package gov.cdc.casenotificationservice.kafka.consumer;

import com.google.gson.Gson;
import gov.cdc.casenotificationservice.exception.*;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.repository.odse.model.CNTransportqOut;
import gov.cdc.casenotificationservice.service.common.interfaces.IConfigurationService;
import gov.cdc.casenotificationservice.service.common.interfaces.IDltService;
import gov.cdc.casenotificationservice.service.nonstd.NonStdService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
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
    private static final Logger logger = LoggerFactory.getLogger(NonStdEventConsumer.class); //NOSONAR
    private final NonStdService nonStdService;
    private final IDltService dltService;
    private final IConfigurationService configurationService;

    @Value("${spring.kafka.topic.non-std-topic}")
    public String topic;
    public NonStdEventConsumer(NonStdService nonStdService, IDltService dltService, IConfigurationService configurationService) {
        this.nonStdService = nonStdService;
        this.dltService = dltService;
        this.configurationService = configurationService;
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
            exclude = {NonRetryableException.class}
    )
    @KafkaListener(
            topics = "${spring.kafka.topic.non-std-topic}",
            containerFactory = "kafkaListenerContainerFactoryConsumerForNonStd"
    )
    public void handleMessage(String message) throws IgnorableException, NonStdProcessorServiceException, NonStdBatchProcessorServiceException, APIException {
        logger.info("Received non std message");
        if(configurationService.checkConfigurationAvailable()) {
            var hl7Applied = configurationService.checkHl7ValidationApplied();
            var gson = new Gson();
            if (message.contains("cnTransportqOutUid")) {
                var data = gson.fromJson(message, MessageAfterStdChecker.class);
                nonStdService.nonStdProcessor(data, hl7Applied);
            }
            else
            {
                var dlt = dltService.getDlt(message);
                MessageAfterStdChecker checker = new MessageAfterStdChecker();
                checker.setCnTransportqOutUid(dlt.getCnTranportqOutUid());
                checker.setReprocessApplied(true);
                nonStdService.nonStdProcessor(checker, hl7Applied);
            }
        }
        logger.info("Completed non std message");

    }

    @DltHandler()
    public void handleDlt(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.EXCEPTION_STACKTRACE) String stacktrace
    ) {
        logger.info("Received DLT message: {}", message);
        dltService.creatingDlt(message, topic, stacktrace, topic);

    }
}
