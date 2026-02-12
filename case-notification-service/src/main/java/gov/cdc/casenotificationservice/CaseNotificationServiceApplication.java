package gov.cdc.casenotificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"gov.cdc.casenotificationservice", "gov.cdc.xmlhl7parserlib"})
public class CaseNotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaseNotificationServiceApplication.class, args);
    }

}
