package gov.cdc.casenotificationservice.util;


import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ErrorResponseBuilder {
    private static Logger logger = LoggerFactory.getLogger(ErrorResponseBuilder.class);

    private ErrorResponseBuilder() {
        //SONARQ
    }
    public static ResponseEntity<Map<String, Object>> buildErrorResponse(Exception e,
                                                                         HttpStatus status,
                                                                         HttpServletRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("status", status.value());
        errorResponse.put("error", status.getReasonPhrase());
        errorResponse.put("message", e.getMessage());
        errorResponse.put("path", request.getRequestURI());

        // Include stack trace
        StackTraceElement[] stackTrace = e.getStackTrace();
        StringBuilder stackTraceString = new StringBuilder();
        for (int i = 0; i < Math.min(stackTrace.length, 5); i++) { // Limit stack trace size for readability
            stackTraceString.append(stackTrace[i].toString()).append("\n");
        }
        errorResponse.put("stackTrace", stackTraceString.toString());

        logger.error(stackTraceString.toString());

        return ResponseEntity.status(status).body(errorResponse);
    }
}
