package gov.cdc.casenotificationservice.controller;

import gov.cdc.casenotificationservice.model.ApiDltResponseModel;
import gov.cdc.casenotificationservice.model.ApiStatusResponseModel;
import gov.cdc.casenotificationservice.service.common.StatusService;
import gov.cdc.casenotificationservice.service.common.interfaces.IStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Status", description = "Status API")
public class StatusController {
    private final IStatusService statusService;

    public StatusController(StatusService statusService) {
        this.statusService = statusService;
    }

    @Operation(
            summary = "Get a Status by CnTransportUid",
            description = "Return pipeline status",
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
                            name = "cnTransportUid",
                            description = "CnTransportUid",
                            required = true,
                            schema = @Schema(type = "long")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Status found",
                            content = @Content(schema = @Schema(implementation = ApiDltResponseModel.class))
                    ),
                    @ApiResponse(responseCode = "404", description = "Status not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("/message-status")
    public ResponseEntity<ApiStatusResponseModel> getStatus (@RequestParam("cnTransportUid") long cnTransportUid)
    {
        try {
            var response = statusService.getProcessedStatus(cnTransportUid);
            if (response == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
