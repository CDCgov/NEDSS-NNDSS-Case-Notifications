package gov.cdc.casenotificationservice.kafka.consumer;

import com.google.gson.Gson;
import gov.cdc.casenotificationservice.exception.NonRetryableException;
import gov.cdc.casenotificationservice.exception.StdProcessorServiceException;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.service.common.interfaces.IConfigurationService;
import gov.cdc.casenotificationservice.service.common.interfaces.IDltService;
import gov.cdc.casenotificationservice.service.std.interfaces.IXmlService;
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
public class StdEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(StdEventConsumer.class); //NOSONAR
    private final IXmlService xmlService;
    private final IDltService dltService;
    private final IConfigurationService configurationService;
    @Value("${spring.kafka.topic.std-topic}")
    public String topic;
    public StdEventConsumer(IXmlService xmlService, IDltService dltService, IConfigurationService configurationService) {
        this.xmlService = xmlService;
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
            topics = "${spring.kafka.topic.std-topic}",
            containerFactory = "kafkaListenerContainerFactoryConsumerForStd"
    )
    public void handleMessage(String message) throws StdProcessorServiceException, NonRetryableException {
        logger.info("Received std message");
        if (configurationService.checkConfigurationAvailable()) {
            var gson = new Gson();
            if (message.contains("cnTransportqOutUid"))
            {
                logger.info("STD DLT: started");
                var data = gson.fromJson(message, MessageAfterStdChecker.class);
                xmlService.mappingXmlStringToObject(data);
                logger.info("STD DLT: completed");
            }
            else
            {
                logger.info("STD: started");
                var dlt = dltService.getDlt(message);
                MessageAfterStdChecker checker = new MessageAfterStdChecker();
                checker.setCnTransportqOutUid(dlt.getCnTranportqOutUid());
                checker.setReprocessApplied(true);
                xmlService.mappingXmlStringToObject(checker);
                logger.info("STD: completed");
            }
        }
        logger.info("Completed std message");
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
