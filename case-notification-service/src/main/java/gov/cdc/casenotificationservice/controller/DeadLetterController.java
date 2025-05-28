package gov.cdc.casenotificationservice.controller;

import gov.cdc.casenotificationservice.exception.DltServiceException;
import gov.cdc.casenotificationservice.model.ApiDltResponseModel;
import gov.cdc.casenotificationservice.model.MessageAfterStdChecker;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationDlt;
import gov.cdc.casenotificationservice.service.common.interfaces.IDltService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;

@RestController
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Dead Letter", description = "Dead Letter API")
public class DeadLetterController {
    private final IDltService dltService;

    public DeadLetterController(IDltService dltService) {
        this.dltService = dltService;
    }

    @Operation(
            summary = "Get Case Notification DLT entries by timestamp range",
            description = "Returns a paginated list of CaseNotificationDlt records filtered by a timestamp range. Timestamps must be provided as strings in 'yyyy-MM-dd HH:mm:ss' format.",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientid",
                            description = "The Client Id for authentication",
                            required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientsecret",
                            description = "The Client Secret for authentication",
                            required = true,
                            schema = @Schema(type = "string")),
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
    @GetMapping("/getDlts")
    public ResponseEntity<Page<CaseNotificationDlt>> getDltsByTimestampRange(
            @RequestParam("from") String from,
            @RequestParam("to") String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    )
    {
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

    @Operation(
            summary = "Reprocess a dead letter case notification",
            description = "Reprocesses a previously dead-lettered message using the given payload and UUID.",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientid",
                            description = "The Client Id for authentication",
                            required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientsecret",
                            description = "The Client Secret for authentication",
                            required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "uuid",
                            description = "UUID of the DLT entry",
                            required = true,
                            schema = @Schema(type = "string")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Payload for the case notification message",
                    required = true,
                    content = @Content(schema = @Schema(type = "string"))
            )
    )
    @PostMapping(value = "/reprocess/{uuid}", consumes = "text/plain")
    public ResponseEntity<Void> reprocessCaseNotification(
            @RequestBody String payload,
            @PathVariable("uuid") String uuid
    )
    {
        try {
            dltService.reprocessingCaseNotification(payload, uuid);
            return ResponseEntity.ok().build();
        } catch (DltServiceException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(
            summary = "Get a DLT entry by UUID",
            description = "Returns a DLT message with the given UUID.",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientid",
                            description = "The Client Id for authentication",
                            required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientsecret",
                            description = "The Client Secret for authentication",
                            required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(
                            in = ParameterIn.PATH,
                            name = "uuid",
                            description = "UUID of the DLT entry",
                            required = true,
                            schema = @Schema(type = "string")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "DLT entry found",
                            content = @Content(schema = @Schema(implementation = ApiDltResponseModel.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "DLT entry not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("getDlts/{uuid}")
    public ResponseEntity<ApiDltResponseModel<CaseNotificationDlt>> getDltByUuid(
            @PathVariable("uuid") String uuid
    )
    {
        try {
            ApiDltResponseModel<CaseNotificationDlt> response = dltService.getDltByUid(uuid);
            if (response == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(response);
        } catch (DltServiceException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

}
