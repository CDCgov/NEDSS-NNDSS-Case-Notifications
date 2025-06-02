package gov.cdc.casenotificationservice.controller;

import gov.cdc.casenotificationservice.model.dto.CaseNotificationConfigDto;
import gov.cdc.casenotificationservice.repository.msg.model.CaseNotificationConfig;
import gov.cdc.casenotificationservice.service.common.ConfigurationService;
import gov.cdc.casenotificationservice.service.common.interfaces.IConfigurationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static gov.cdc.casenotificationservice.util.ErrorResponseBuilder.buildErrorResponse;

@RestController
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Configuration", description = "Configuration API")
public class ConfigurationController {
    private final IConfigurationService configurationService;

    public ConfigurationController(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    @Operation(
            summary = "Create or Update Case Notification Configuration",
            description = """
        Adds a new or updates an existing case notification configuration.
        
        - If `configName` already exists, the configuration will be updated.
        - Otherwise, a new configuration will be inserted.
        
        The request body must match the following model:
        ```json
        {
          "configName": "string (required)",
          "configApplied": true,
          "batchMesageProfileId": "string",
          "nbsCertificateUrl": "string",
          "phinEncryption": "string",
          "phinRoute": "string",
          "phinSignature": "string",
          "phinPublicKeyAddress": "string",
          "phinPublicKeyBaseDn": "string",
          "phinPublicKeyDn": "string",
          "phinRecipient": "string",
          "phinPriority": "string",
          "hl7ValidationEnabled": true
        }
        ```
        """,
            parameters = {
                    @Parameter(
                            in = ParameterIn.HEADER,
                            name = "clientid",
                            description = "The Client ID for authentication",
                            required = true,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            in = ParameterIn.HEADER,
                            name = "clientsecret",
                            description = "The Client Secret for authentication",
                            required = true,
                            schema = @Schema(type = "string")
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    description = "JSON representation of the CaseNotificationConfigDto object",
                    content = @Content(
                            schema = @Schema(implementation = CaseNotificationConfigDto.class)
                    )
            )
    )
    @PostMapping("/api/notification-config")
    public ResponseEntity<?> saveNotificationConfig(@RequestBody CaseNotificationConfigDto dto,
                                                  HttpServletRequest request)  {

        try {
            CaseNotificationConfig savedEntity = configurationService.saveConfig(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body(CaseNotificationConfigDto.fromEntity(savedEntity));
        } catch (Exception e) {
            return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @Operation(
            summary = "Get Case Notification Configuration",
            description = """
        Fetches one or more case notification configuration records.
        
        - If `configName` is provided, returns the specific matching configuration.
        - If `configName` is omitted, returns all configurations.
        """,
            parameters = {
                    @Parameter(
                            in = ParameterIn.HEADER,
                            name = "clientid",
                            description = "The Client ID for authentication",
                            required = true,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            in = ParameterIn.HEADER,
                            name = "clientsecret",
                            description = "The Client Secret for authentication",
                            required = true,
                            schema = @Schema(type = "string")
                    ),
                    @Parameter(
                            in = ParameterIn.QUERY,
                            name = "configName",
                            description = "Optional configuration name to retrieve a specific record",
                            required = false,
                            schema = @Schema(type = "string")
                    )
            }
    )
    @GetMapping("/api/notification-config")
    public ResponseEntity<?> getNotificationConfig(@RequestParam(name = "configName", required = false) String configName,
                                                    HttpServletRequest request)  {

        try {
            var configs = configurationService.getConfigs(configName);
            return ResponseEntity.status(HttpStatus.CREATED).body(configs);
        } catch (Exception e) {
            return buildErrorResponse(e, HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }







    @Operation(
            summary = "Update Case Notification config",
            description = "If 'id' is not provided, updates the top config by name; otherwise updates by given id.",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER, name = "clientid", required = true, schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER, name = "clientsecret", required = true, schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.QUERY, name = "id", required = false, schema = @Schema(type = "integer")),
                    @Parameter(in = ParameterIn.QUERY, name = "enabled", required = true, schema = @Schema(type = "boolean"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Update success"),
                    @ApiResponse(responseCode = "500", description = "Internal server error")
            }
    )
    @PutMapping("config/update")
    public ResponseEntity<Void> updateConfiguration(@RequestParam(value = "id", required = false) Integer id,
                                                    @RequestParam(value = "enabled", required = true) boolean enabled) {
        configurationService.updateConfiguration(id, enabled);
        return ResponseEntity.ok().build();
    }

    @Operation(
            summary = "Get the applied Case Notification config",
            parameters = {
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientid",
                            description = "The Client Id",
                            required = true,
                            schema = @Schema(type = "string")),
                    @Parameter(in = ParameterIn.HEADER,
                            name = "clientsecret",
                            description = "The Client Secret",
                            required = true,
                            schema = @Schema(type = "string"))
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
