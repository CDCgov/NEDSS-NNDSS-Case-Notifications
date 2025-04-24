package gov.cdc.dataextractionservice.kafka;

import com.google.gson.Gson;
import gov.cdc.dataextractionservice.model.CnTransportqOutMessage;
import gov.cdc.dataextractionservice.model.CnTransportqOutValue;
import gov.cdc.dataextractionservice.model.MessageAfterStdChecker;
import gov.cdc.dataextractionservice.service.StdCheckerTransformerService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    @Value("${kafka.topic.cn-tranport-out-topic:}")
    private String transportOutQTopic = "nbs_CN_transportq_out";

    @Autowired
    private StdCheckerTransformerService transformerService;

    @KafkaListener(
            topics = "${kafka.topic.cn-tranport-out-topic}",
            containerFactory = "kafkaListenerContainerFactoryDebeziumConsumer"
    )
    public void handleMessage(String messages) {
        try {
            logger.info("Raw message: {}", messages);
            Gson gson = new Gson();
            CnTransportqOutMessage message = gson.fromJson(messages, CnTransportqOutMessage.class);

            CnTransportqOutValue after = message.getPayload().getAfter();
            if (after != null) {
                MessageAfterStdChecker transformed = transformerService.transform(after);

                if (transformed != null) {
                    // TODO: Send transformed to Kafka Producer
                    logger.info("Transformed message ready: {}", transformed);
                } else {
                    logger.info("Message skipped - did not meet transformation criteria");
                }
            } else {
                logger.info("CDC event ignored (no 'after' state)");
            }

        } catch (Exception e) {
            logger.error("ConsumerService.handleMessage: {}", e.getMessage(), e);
        }
    }
}
