//package gov.cdc.casenotificationservice.service;
//
//import com.google.gson.Gson;
//import gov.cdc.casenotificationservice.repository.odse.CNTraportqOutRepository;
//import gov.cdc.casenotificationservice.service.nonstd.interfaces.INonStdService;
//import gov.cdc.casenotificationservice.service.std.interfaces.IXmlService;
//import jakarta.annotation.PostConstruct;
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
//        String fileName = "payload-test/payload_1.txt";
//
//        String payload = "";
//        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
//             Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name())) {
//            scanner.useDelimiter("\\A"); // Read entire file
//            payload = scanner.hasNext() ? scanner.next() : "";
//        }
//
//
//        initNonStd(payload);
//    }
//
//    private void initStd() {
//        var test = cnTraportqOutRepository.findTopByRecordStatusCdAndUid("UNPROCESSED", 23187L);
//        Gson gson = new Gson();
//
//        xmlService.mappingXmlStringToObject(gson.toJson(test));
//    }
//
//    private void initNonStd(String payload) throws Exception {
//        inonStdService.nonStdProcessor(payload);
//    }
//}
