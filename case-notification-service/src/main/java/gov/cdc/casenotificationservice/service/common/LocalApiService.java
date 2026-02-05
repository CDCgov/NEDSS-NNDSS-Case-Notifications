package gov.cdc.casenotificationservice.service.common;

import gov.cdc.casenotificationservice.exception.APIException;
import gov.cdc.casenotificationservice.service.common.interfaces.IApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
@Profile("local")
@Primary
public class LocalApiService implements IApiService {
    private static final Logger logger = LoggerFactory.getLogger(LocalApiService.class);

    @Value("${api.endpoint_hl7}")
    protected String hl7Endpoint;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public String callToken() {
        return "local-no-auth";
    }

    @Override
    public String callHl7Endpoint(String token, String recordId, boolean hl7ValidationEnabled) throws APIException {
        try {
            HttpEntity<String> entity = new HttpEntity<>(new HttpHeaders());

            URI uri = UriComponentsBuilder.fromHttpUrl(hl7Endpoint)
                    .pathSegment(recordId)
                    .queryParam("validationEnabled", hl7ValidationEnabled)
                    .build()
                    .toUri();

            logger.info("LOCAL API POST Request to HL7 endpoint (no auth): {}", uri);

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            throw new APIException("Error calling HL7 endpoint: " + e.getMessage());
        }
    }
}
