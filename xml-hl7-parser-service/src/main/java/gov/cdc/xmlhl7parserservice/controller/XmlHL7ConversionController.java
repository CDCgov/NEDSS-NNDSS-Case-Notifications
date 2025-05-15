package gov.cdc.xmlhl7parserservice.controller;

import gov.cdc.xmlhl7parserservice.helper.HL7MessageBuilder;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.NBSNNDIntermediaryMessage;
import gov.cdc.xmlhl7parserservice.service.XmlHL7ConversionService;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;


@RestController
public class XmlHL7ConversionController {

    private static final Logger log = LoggerFactory.getLogger(XmlHL7ConversionController.class);

    private final XmlHL7ConversionService xmlHl7ConversionService;
    private final HL7MessageBuilder hl7MessageBuilder;

    public XmlHL7ConversionController(XmlHL7ConversionService xmlHl7ConversionService,
                                      HL7MessageBuilder hl7MessageBuilder) {
        this.xmlHl7ConversionService = xmlHl7ConversionService;
        this.hl7MessageBuilder = hl7MessageBuilder;
    }

    @PostMapping(value = "/xml-to-hl7", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> convertXmlToHl7(@RequestParam(defaultValue = "true") boolean validationEnabled) {
        log.info("Received request to convert XML file to HL7");
        String message = "";

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("sample_xml_dts1.xml")) {

            System.setProperty(
                    "jakarta.xml.bind.context.factory",
                    "org.glassfish.jaxb.runtime.v2.ContextFactory"
            );
            JAXBContext context = JAXBContext.newInstance(NBSNNDIntermediaryMessage.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            NBSNNDIntermediaryMessage nbsnndIntermediaryMessage = (NBSNNDIntermediaryMessage) unmarshaller.unmarshal(is);

            message = hl7MessageBuilder.parseXml(nbsnndIntermediaryMessage, validationEnabled);

        } catch (Exception e) {
            log.error("Exception occurred while parsing/processing NBSNNDMessage xml file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to convert XML to HL7: " + e.getMessage());
        }

        return ResponseEntity.ok(message);
    }

    @PostMapping(value = "/xml-to-hl7/{recordId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> convertXmlToHl7ById(@PathVariable String recordId) {
        log.info("Received request to convert XML file to HL7");
        String message = "";

        try (InputStream is = getClass().getClassLoader().getResourceAsStream("sample_xml_dts1.xml")) {

            System.setProperty(
                    "jakarta.xml.bind.context.factory",
                    "org.glassfish.jaxb.runtime.v2.ContextFactory"
            );
            JAXBContext context = JAXBContext.newInstance(NBSNNDIntermediaryMessage.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            NBSNNDIntermediaryMessage nbsnndIntermediaryMessage = (NBSNNDIntermediaryMessage) unmarshaller.unmarshal(is);

            message = hl7MessageBuilder.parseXml(nbsnndIntermediaryMessage, false);

        } catch (Exception e) {
            log.error("Exception occurred while parsing/processing NBSNNDMessage xml file", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to convert XML to HL7: " + e.getMessage());
        }

        return ResponseEntity.ok(message);
    }

}