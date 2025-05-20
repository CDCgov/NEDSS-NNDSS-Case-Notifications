package gov.cdc.casenotificationservice.controller;

import gov.cdc.casenotificationservice.exception.NonRetryableException;
import gov.cdc.casenotificationservice.service.nonstd.NonStdService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "bearer-key")
@Tag(name = "Non Std", description = "Non Std API")
public class NonStdController {
    private final NonStdService nonStdService;

    @Operation(
            summary = "Releasing Non Std Queue for Batch Processor",
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
            }
    )
    @PostMapping(path = "/non-std/release-queue")
    public ResponseEntity<String> releaseQueue() throws NonRetryableException {
        nonStdService.releaseHoldQueueAndProcessBatchNonStd();
        return new ResponseEntity<>(HttpStatus.OK);
    }

}
