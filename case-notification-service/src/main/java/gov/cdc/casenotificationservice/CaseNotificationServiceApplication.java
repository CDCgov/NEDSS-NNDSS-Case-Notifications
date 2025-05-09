package gov.cdc.casenotificationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CaseNotificationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(CaseNotificationServiceApplication.class, args);
    }

}
