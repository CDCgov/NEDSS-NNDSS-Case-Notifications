package gov.cdc.casenotificationservice.controller;

import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationDlt;
import gov.cdc.casenotificationservice.service.deadletter.interfaces.IDltService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;

@RestController
public class DeadLetterController {
    private final IDltService dltService;

    public DeadLetterController(IDltService dltService) {
        this.dltService = dltService;
    }

    @Operation(
            summary = "Get Case Notification DLT entries by timestamp range",
            description = "Returns a paginated list of CaseNotificationDlt records filtered by a timestamp range. Timestamps must be provided as strings in 'yyyy-MM-dd HH:mm:ss' format.",
            parameters = {
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "from",
                            description = "Start of timestamp range (format: yyyy-MM-dd HH:mm:ss)",
                            required = true,
                            schema = @Schema(type = "string", example = "2025-05-01 00:00:00")
                    ),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "to",
                            description = "End of timestamp range (format: yyyy-MM-dd HH:mm:ss)",
                            required = true,
                            schema = @Schema(type = "string", example = "2025-05-01 23:59:59")
                    ),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "page",
                            description = "Page number (zero-based)",
                            required = false,
                            schema = @Schema(type = "integer", defaultValue = "0")
                    ),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "size",
                            description = "Page size (number of records per page)",
                            required = false,
                            schema = @Schema(type = "integer", defaultValue = "20")
                    )
            }
    )
    @GetMapping
    public ResponseEntity<Page<CaseNotificationDlt>> getDltsByTimestampRange(
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Timestamp start = Timestamp.valueOf(from);
            Timestamp end = Timestamp.valueOf(to);

            Page<CaseNotificationDlt> results = dltService
                    .getDltsBetweenWithPagination(start, end, page, size);
            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

}
