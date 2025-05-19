package gov.cdc.casenotificationservice.controller;

import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import gov.cdc.casenotificationservice.service.common.ConfigurationService;
import gov.cdc.casenotificationservice.service.common.interfaces.IConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigurationController {
    private final IConfigurationService configurationService;

    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }



    @Operation(
            summary = "Update Case Notification config",
            description = "If 'id' is not provided, updates the top config by name; otherwise updates by given id.",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "clientid", required = true, schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "clientsecret", required = true, schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.QUERY, name = "id", required = false, schema = @Schema(type = "integer"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Update success"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PutMapping("config/update")
    public ResponseEntity<Void> updateConfiguration(@RequestParam(value = "id", required = false) Integer id) {
        configurationService.updateConfiguration(id);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get the applied Case Notification config",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "clientid", required = true, schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "clientsecret", required = true, schema = @Schema(type = "string"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Returned config", content = @Content(schema = @Schema(implementation = CaseNotificationConfig.class))),
                    @ApiResponse(responseCode = "404", description = "Config not found"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @GetMapping("config/check")
    public ResponseEntity<CaseNotificationConfig> getAppliedConfig() {
        CaseNotificationConfig config = configurationService.getAppliedCaseNotificationConfig();
        if (config == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(config);
    }
}
