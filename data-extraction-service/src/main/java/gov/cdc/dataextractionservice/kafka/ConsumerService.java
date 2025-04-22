package gov.cdc.dataextractionservice.kafka;

import com.google.gson.Gson;
import gov.cdc.dataextractionservice.model.CnTransportqOutMessage;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerService.class);

    @Value("${kafka.topic.cn-tranport-out-topic:}")
    private String transportOutQTopic = "nbs_CN_transportq_out";


    @KafkaListener(
            topics = "${kafka.topic.cn-tranport-out-topic}",
            containerFactory = "kafkaListenerContainerFactoryDebeziumConsumer"
    )
    public void handleMessage(String messages) {
        try {
            logger.info(messages);
            Gson gson = new Gson();
            CnTransportqOutMessage message = gson.fromJson(messages, CnTransportqOutMessage.class);
            logger.info(messages);

        } catch (Exception e) {
            logger.error("ConsumerService.handleMessage: {}", e.getMessage());
        }
    }
}
