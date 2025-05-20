package gov.cdc.xmlhl7parserservice.controller;

import gov.cdc.xmlhl7parserservice.helper.HL7MessageBuilder;
import gov.cdc.xmlhl7parserservice.model.generated.jaxb.NBSNNDIntermediaryMessage;
import gov.cdc.xmlhl7parserservice.repository.odse.CNTraportqOutRepository;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;
import java.io.StringReader;


@RestController
@SecurityRequirement(name = "bearer-key")
@Tag(name = "XML Parser", description = "XML to HL7 parser endpoint")
public class XmlHL7ConversionController {

    private static final Logger log = LoggerFactory.getLogger(XmlHL7ConversionController.class);

    private final HL7MessageBuilder hl7MessageBuilder;
    private final CNTraportqOutRepository cnTraportqOutRepository;

    public XmlHL7ConversionController(HL7MessageBuilder hl7MessageBuilder,
                                      CNTraportqOutRepository cnTraportqOutRepository) {
        this.hl7MessageBuilder = hl7MessageBuilder;
        this.cnTraportqOutRepository = cnTraportqOutRepository;
    }

    @Hidden
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

    @Operation(
            summary = "Service class to convert the incoming XML into HL7",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientid",
                            description = "The Client Id",
                            required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientsecret",
                            description = "The Client Secret",
                            required = true,
                            schema = @Schema(type = "string"))
            }
    )
    @PostMapping(value = "/xml-to-hl7/{recordId}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> convertXmlToHl7ById(@PathVariable String recordId) {
        try {
            String messagePayload = cnTraportqOutRepository
                    .findTopByRecordUid(Long.valueOf(recordId))
                    .getMessagePayload();

            System.setProperty(
                    "jakarta.xml.bind.context.factory",
                    "org.glassfish.jaxb.runtime.v2.ContextFactory"
            );

            JAXBContext context = JAXBContext.newInstance(NBSNNDIntermediaryMessage.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            StringReader reader = new StringReader(messagePayload);
            NBSNNDIntermediaryMessage nbsnndIntermediaryMessage =
                    (NBSNNDIntermediaryMessage) unmarshaller.unmarshal(reader);

            // Convert to HL7
            String response = hl7MessageBuilder.parseXml(nbsnndIntermediaryMessage, false);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Error during XML to HL7 conversion for recordId {}: {}", recordId, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to convert XML to HL7: " + e.getMessage());
        }
    }

}