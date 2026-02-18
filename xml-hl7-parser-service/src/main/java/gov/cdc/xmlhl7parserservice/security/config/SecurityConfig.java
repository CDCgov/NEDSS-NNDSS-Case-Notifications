package gov.cdc.xmlhl7parserservice.security.config;

import gov.cdc.xmlhl7parserservice.security.CustomAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

/**
 *
 *
 * <ul>
 *   <li>1118 - require constructor complaint
 *   <li>125 - comment complaint
 *   <li>6126 - String block complaint
 *   <li>1135 - todos complaint
 * </ul>
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@SuppressWarnings({"java:S1118", "java:S125", "java:S6126", "java:S1135"})
public class SecurityConfig {
  private static final String[] AUTH_WHITELIST_DEV = {
    "/v2/api-docs",
    "/swagger-resources",
    "/swagger-resources/**",
    "/configuration/ui",
    "/configuration/security",
    "/swagger-ui.html",
    "/webjars/**",
    "/v3/api-docs/**",
    "/swagger-ui/**",
    "/api/auth/token",
    "/actuator/health",
    "/actuator/info"
  };
  private static final String[] AUTH_WHITELIST_PROD = {
    "/configuration/ui",
    "/configuration/security",
    "/webjars/**",
    "/api/auth/token",
    "/actuator/health",
    "/actuator/info"
  };

  @Value("${auth.introspect-uri}")
  String introspectionUri;

  @Autowired private CustomAuthenticationManagerResolver customauthenticationmanagerresolver;

  @Bean
  @Profile("dev")
  public SecurityFilterChain devSecurityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(AUTH_WHITELIST_DEV).permitAll().anyRequest().authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer(
            oauth2 ->
                oauth2
                    .authenticationManagerResolver(customauthenticationmanagerresolver)
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
        .exceptionHandling(
            exception -> exception.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

    return http.build();
  }

  @Bean
  @Profile("!dev")
  public SecurityFilterChain prodSecurityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(AUTH_WHITELIST_PROD).permitAll().anyRequest().authenticated())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .oauth2ResourceServer(
            oauth2 ->
                oauth2
                    .authenticationManagerResolver(customauthenticationmanagerresolver)
                    .authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
        .exceptionHandling(
            exception -> exception.authenticationEntryPoint(new CustomAuthenticationEntryPoint()));

    return http.build();
  }
}
