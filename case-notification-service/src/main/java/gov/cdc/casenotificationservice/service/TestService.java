package gov.cdc.casenotificationservice.service;

import com.google.gson.Gson;
import gov.cdc.casenotificationservice.exception.StdProcessorServiceException;
import gov.cdc.casenotificationservice.kafka.consumer.StdEventConsumer;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.repository.odse.CNTraportqOutRepository;
import gov.cdc.casenotificationservice.service.nonstd.interfaces.INonStdService;
import gov.cdc.casenotificationservice.service.std.interfaces.IXmlService;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

@Service
public class TestService {
    private static final Logger logger = LoggerFactory.getLogger(TestService.class); //NOSONAR

    private final CNTraportqOutRepository cnTraportqOutRepository;
    private final IXmlService xmlService;
    private final INonStdService inonStdService;

    public TestService(CNTraportqOutRepository cnTraportqOutRepository,
                       IXmlService xmlService,
                       INonStdService inonStdService) {
        this.cnTraportqOutRepository = cnTraportqOutRepository;
        this.xmlService = xmlService;
        this.inonStdService = inonStdService;
    }
//
//    @PostConstruct
//    public void init() throws Exception {
//        initNonStd();
//    }

//    @Scheduled(cron = "0 */1 * * * *", zone = "UTC")
    public void initStd() throws JAXBException, StdProcessorServiceException {
        logger.info("Initializing std");
        MessageAfterStdChecker messageAfterStdChecker
                = new MessageAfterStdChecker();
//        messageAfterStdChecker.setCnTransportqOutUid(23187L);
//        var test = cnTraportqOutRepository.findTopByRecordStatusCdAndUid("UNPROCESSED", 23187L);
//        Gson gson = new Gson();

                var test = cnTraportqOutRepository.findTopByRecordStatusCdAndUid("UNPROCESSED", 23187L);

//        var test = cnTraportqOutRepository.findTopStdForTesting();
        messageAfterStdChecker.setCnTransportqOutUid(test.getCnTransportqOutUid());

        xmlService.mappingXmlStringToObject(messageAfterStdChecker);
        logger.info("Completed std");

    }

    @Scheduled(cron = "0 */1 * * * *", zone = "UTC")
    public void initNonStd() throws Exception {
        logger.info("Initializing non std");
        MessageAfterStdChecker messageAfterStdChecker
                = new MessageAfterStdChecker();
//        messageAfterStdChecker.setCnTransportqOutUid(23187L);
        var test = cnTraportqOutRepository.findTopNonStdForTesting();
        messageAfterStdChecker.setCnTransportqOutUid(test.getCnTransportqOutUid());

        inonStdService.nonStdProcessor(messageAfterStdChecker);
        logger.info("Completed non std");
    }
}
