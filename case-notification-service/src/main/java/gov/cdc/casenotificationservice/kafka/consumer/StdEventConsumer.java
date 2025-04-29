package gov.cdc.casenotificationservice.kafka.consumer;

import com.google.gson.Gson;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.service.std.interfaces.IXmlService;
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
public class StdEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(StdEventConsumer.class); //NOSONAR
    private final IXmlService xmlService;

    public StdEventConsumer(IXmlService xmlService) {
        this.xmlService = xmlService;
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
            // if these exceptions occur, skip retry then push message to DLQ
            exclude = {

            }

    )
    @KafkaListener(
            topics = "${spring.kafka.topic.std-topic}",
            containerFactory = "kafkaListenerContainerFactoryConsumerForStd"
    )
    public void handleMessage(String message){

        try {
            var gson = new Gson();
            var data = gson.fromJson(message, MessageAfterStdChecker.class);
            xmlService.mappingXmlStringToObject(data);
        } catch (Exception e) {
            logger.error("KafkaEdxLogConsumer.handleMessage: {}", e.getMessage());
        }

    }

    @DltHandler()
    public void handleDlt(
            String message,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic
    ) {
    }
}
