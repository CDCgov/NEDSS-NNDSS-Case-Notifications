package gov.cdc.xmlhl7parserservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"gov.cdc.xmlhl7parserservice", "gov.cdc.xmlhl7parserlib"})
public class XmlHL7ParserApplication {

    public static void main(String[] args) {
        SpringApplication.run(XmlHL7ParserApplication.class, args);
    }

}