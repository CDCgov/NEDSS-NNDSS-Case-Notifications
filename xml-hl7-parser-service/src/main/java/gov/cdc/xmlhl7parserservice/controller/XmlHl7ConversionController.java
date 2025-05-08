package gov.cdc.xmlhl7parserservice.controller;

import gov.cdc.xmlhl7parserservice.helper.HL7MessageBuilder;
import gov.cdc.xmlhl7parserservice.model.NBSNNDIntermediaryMessage;
import gov.cdc.xmlhl7parserservice.service.XmlHl7ConversionService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.InputStream;


@RestController
@RequestMapping("/api/v1/convert")
public class XmlHl7ConversionController {
    
    private static final Logger log = LoggerFactory.getLogger(XmlHl7ConversionController.class);
    
    private final XmlHl7ConversionService xmlHl7ConversionService;

    public XmlHl7ConversionController(XmlHl7ConversionService xmlHl7ConversionService) {
        this.xmlHl7ConversionService = xmlHl7ConversionService;
    }

    @PostMapping(value = "/xml-to-hl7", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> convertXmlToHl7(@RequestParam("file") MultipartFile file) {
        log.info("Received request to convert XML file to HL7");
        
//        if (file.isEmpty()) {
//            log.warn("Empty file received");
//            return ResponseEntity.badRequest().body("Empty file");
//        }

        try {
            File xmlFile = new File("src/main/resources/sample_010724.xml");
            JAXBContext context = JAXBContext.newInstance(NBSNNDIntermediaryMessage.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            NBSNNDIntermediaryMessage nbsnndIntermediaryMessage= (NBSNNDIntermediaryMessage) unmarshaller.unmarshal(xmlFile);
            HL7MessageBuilder messageBuilder = new HL7MessageBuilder(nbsnndIntermediaryMessage);
            messageBuilder.parseXml();

        }catch (Exception e) {
            System.err.println("Exception occurred while parsing/processing NBSNNDMessage xml file" + e);
        }
        
        try (InputStream inputStream = file.getInputStream()) {
            String hl7Message = xmlHl7ConversionService.convertXmlToHl7(inputStream);
            
            if (hl7Message == null) {
                log.error("Failed to convert XML to HL7");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to convert XML to HL7");
            }
            
            return ResponseEntity.ok(hl7Message);
            
        } catch (Exception e) {
            log.error("Error converting XML to HL7", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error converting XML to HL7: " + e.getMessage());
        }
    }
} 