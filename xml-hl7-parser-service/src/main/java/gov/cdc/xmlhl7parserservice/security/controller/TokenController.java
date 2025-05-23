package gov.cdc.xmlhl7parserservice.security.controller;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@Tag(name = "Token API", description = "Token API to create the JWT Token")
@RestController
@Slf4j
/**
 1118 - require constructor complaint
 125 - comment complaint
 6126 - String block complaint
 1135 - todos complaint
 * */
@SuppressWarnings({"java:S1118","java:S125", "java:S6126", "java:S1135"})
public class TokenController {
    @Value("${auth.token-uri}")
    String authTokenUri;
    private RestTemplate restTemplate;
    public TokenController( @Qualifier("restTemplate") RestTemplate restTemplate) {
        this.restTemplate=restTemplate;
    }
    @Bean(name = "restTemplate")
    public static RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
    @Operation(
            summary = "Create JWT Token",
            description = "Create JWT Token using Keycloak Client Id and Client Secret."
    )
    @PostMapping("/api/auth/token")
    public ResponseEntity<String> token(@RequestHeader("clientid") String clientId, @RequestHeader("clientsecret") String clientSecret) {
        log.debug("Token URL : " + authTokenUri);
        String accessToken = null;
        String postBody = "grant_type=client_credentials" +
                "&client_id=" + clientId
                + "&client_secret=" + clientSecret;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");
        HttpEntity<String> request = new HttpEntity<>(postBody, headers);
        try{
            ResponseEntity<String> exchange =
                    restTemplate.exchange(
                            authTokenUri,
                            HttpMethod.POST,
                            request,
                            String.class);
            String response = exchange.getBody();
            JsonElement jsonElement = JsonParser.parseString(response);
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            accessToken = jsonObject.get("access_token").getAsString();
        }catch (Exception ex){
            log.error("Error message in token endpoint : " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
        }
        return ResponseEntity.ok(accessToken);
    }
}
