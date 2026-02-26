package gov.cdc.casenotificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"gov.cdc.casenotificationservice", "gov.cdc.xmlhl7parser"})

// xml-hl7-parser is included because the library loads its mapping configuration from the
// database (via IServiceActionPairRepository). This is a byproduct of the parser having been a
// standalone microservice; consider removing the database interaction now that it is a library.
@EntityScan(basePackages = {"gov.cdc.casenotificationservice", "gov.cdc.xmlhl7parser"})
public class CaseNotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaseNotificationServiceApplication.class, args);
    }

}
