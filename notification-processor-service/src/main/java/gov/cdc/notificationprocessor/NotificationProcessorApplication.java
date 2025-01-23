package gov.cdc.notificationprocessor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class NotificationProcessorApplication {

    public static void main(String[] args) {
        //This is the main entry point where Spring Boot application is launched
        SpringApplication.run(NotificationProcessorApplication.class, args);
    }

}
