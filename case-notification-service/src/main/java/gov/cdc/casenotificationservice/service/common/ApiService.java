package gov.cdc.casenotificationservice.service.common;

import gov.cdc.casenotificationservice.exception.APIException;
import gov.cdc.casenotificationservice.service.common.interfaces.IApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class ApiService implements IApiService {
    private static final Logger logger = LoggerFactory.getLogger(ApiService.class);

    @Value("${api.clientId}")
    private String clientId;

    @Value("${api.secret}")
    private String clientSecret;


    @Value("${api.endpoint_hl7}")
    protected String hl7Endpoint;


    @Value("${api.endpoint_token}")
    private String tokenEndpoint;



    private final RestTemplate restTemplate = new RestTemplate();

    public String callToken() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("clientid", clientId);
        headers.add("clientsecret", clientSecret);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        URI uri = UriComponentsBuilder.fromHttpUrl(tokenEndpoint)
                .build()
                .toUri();
        ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
        return response.getBody();
    }

    public String callHl7Endpoint(String token, String recordId, boolean hl7ValidationEnabled) throws APIException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(token);
            headers.add("clientid", clientId);
            headers.add("clientsecret", clientSecret);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            URI uri = UriComponentsBuilder.fromHttpUrl(hl7Endpoint)
                    .pathSegment(recordId)
                    .queryParam("validationEnabled", hl7ValidationEnabled)
                    .build()
                    .toUri();

            logger.info("API POST Request to HL7 endpoint: {}", uri);

            ResponseEntity<String> response = restTemplate.exchange(uri, HttpMethod.POST, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            throw new APIException("Error calling HL7 endpoint: " + e.getMessage());
        }
    }

}
