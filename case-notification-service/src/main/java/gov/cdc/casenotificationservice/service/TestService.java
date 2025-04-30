//package gov.cdc.casenotificationservice.service;
//
//import com.google.gson.Gson;
//import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
//import gov.cdc.casenotificationservice.repository.odse.CNTraportqOutRepository;
//import gov.cdc.casenotificationservice.service.nonstd.interfaces.INonStdService;
//import gov.cdc.casenotificationservice.service.std.interfaces.IXmlService;
//import jakarta.annotation.PostConstruct;
//import jakarta.xml.bind.JAXBException;
//import org.springframework.stereotype.Service;
//
//import java.io.InputStream;
//import java.nio.charset.StandardCharsets;
//import java.util.Scanner;
//
//@Service
//public class TestService {
//
//    private final CNTraportqOutRepository cnTraportqOutRepository;
//    private final IXmlService xmlService;
//    private final INonStdService inonStdService;
//
//    public TestService(CNTraportqOutRepository cnTraportqOutRepository,
//                       IXmlService xmlService,
//                       INonStdService inonStdService) {
//        this.cnTraportqOutRepository = cnTraportqOutRepository;
//        this.xmlService = xmlService;
//        this.inonStdService = inonStdService;
//    }
//
//    @PostConstruct
//    public void init() throws Exception {
//        initNonStd();
//    }
//
//    private void initStd() throws JAXBException {
//        MessageAfterStdChecker messageAfterStdChecker
//                = new MessageAfterStdChecker();
//        messageAfterStdChecker.setCnTransportqOutUid(23187L);
//        var test = cnTraportqOutRepository.findTopByRecordStatusCdAndUid("UNPROCESSED", 23187L);
//        Gson gson = new Gson();
//
//        xmlService.mappingXmlStringToObject(messageAfterStdChecker);
//    }
//
//    private void initNonStd() throws Exception {
//        String fileName = "payload-test/payload_1.txt";
//        String pl = "";
//        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
//             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
//            scanner.useDelimiter("\\A"); // Read entire file
//            pl = scanner.hasNext() ? scanner.next() : "";
//        }
//
//        MessageAfterStdChecker messageAfterStdChecker
//                = new MessageAfterStdChecker();
//        messageAfterStdChecker.setCnTransportqOutUid(23187L);
//        inonStdService.nonStdProcessor(messageAfterStdChecker);
//    }
//}
