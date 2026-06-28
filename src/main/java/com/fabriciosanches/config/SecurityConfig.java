package com.fabriciosanches.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtIssuerValidator;
import org.springframework.security.oauth2.jwt.JwtTimestampValidator;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.web.servlet.HandlerExceptionResolver;

import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.stream.Collectors;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http,
                                            AuthenticationEntryPoint authenticationEntryPoint,
                                            AccessDeniedHandler accessDeniedHandler) throws Exception {
        return http
                // CSRF disabled intentionally: this is a stateless REST API that authenticates
                // exclusively via JWT ****** (Authorization header). CSRF attacks rely on
                // the browser automatically sending session cookies, which this API does not use.
                // See Spring Security docs: "When to use CSRF protection" — stateless APIs are exempt.
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
                        .requestMatchers("/actuator/health", "/actuator/info", "/actuator/prometheus").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/orders/**").hasAuthority("SCOPE_orders:write")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/orders/**").hasAuthority("SCOPE_orders:write")
                        .requestMatchers(HttpMethod.GET, "/api/v1/orders/**").hasAuthority("SCOPE_orders:read")
                        .requestMatchers(HttpMethod.POST, "/api/v1/payments/**").hasAuthority("SCOPE_payments:write")
                        .requestMatchers(HttpMethod.GET, "/api/v1/payments/**").hasAuthority("SCOPE_payments:read")
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .build();
    }

     @Bean
     JwtDecoder jwtDecoder(@Value("${security.jwt.secret}") String secret,
                          @Value("${security.jwt.issuer:order-service}") String issuer,
                          @Value("${security.jwt.audience:order-service-api}") String audience) {
         if (secret == null || secret.getBytes(StandardCharsets.UTF_8).length < 32) {
             throw new IllegalStateException("security.jwt.secret must be at least 32 bytes for HS256");
         }
         SecretKeySpec key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
         NimbusJwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key)
                 .macAlgorithm(MacAlgorithm.HS256)
                 .build();

         // Validadores: timestamp e issuer
         decoder.setJwtValidator(new org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator<>(
                 new JwtTimestampValidator(),
                 new JwtIssuerValidator(issuer)
         ));

         return decoder;
     }

    @Bean
    JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter delegate = new JwtGrantedAuthoritiesConverter();
        delegate.setAuthoritiesClaimName("scope");
        delegate.setAuthorityPrefix("SCOPE_");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> normalizeAuthorities(delegate.convert(jwt)));
        return converter;
    }

    private Collection<GrantedAuthority> normalizeAuthorities(Collection<GrantedAuthority> authorities) {
        return authorities.stream()
                .map(GrantedAuthority::getAuthority)
                .map(this::normalizeScopeAuthority)
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    private String normalizeScopeAuthority(String authority) {
        if (authority.startsWith("SCOPE_SCOPE_")) {
            return authority.substring("SCOPE_".length());
        }
        return authority;
    }

    @Bean
    AuthenticationEntryPoint authenticationEntryPoint(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        return (request, response, ex) -> resolver.resolveException(
                request,
                response,
                null,
                new InsufficientAuthenticationException("Token JWT ausente ou inválido.", ex)
        );
    }

    @Bean
    AccessDeniedHandler accessDeniedHandler(
            @Qualifier("handlerExceptionResolver") HandlerExceptionResolver resolver) {
        return (request, response, ex) -> resolver.resolveException(request, response, null, ex);
    }
}
