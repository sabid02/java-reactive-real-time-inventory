package com.example.auth.config;

import com.example.auth.dto.response.ApiResponse;
import com.example.auth.security.JwtAuthenticationManager;
import com.example.auth.security.SecurityContextRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import reactor.core.publisher.Mono;

/**
 * Reactive Spring Security 6 Configuration for WebFlux.
 * Configures stateless JWT authentication filter chain, BCrypt password encoder, CORS, and endpoint rules.
 */
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ServerAuthenticationEntryPoint authenticationEntryPoint() {
        return (exchange, ex) -> {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            String errorMsg = (ex != null && ex.getMessage() != null && !ex.getMessage().isBlank())
                    ? ex.getMessage()
                    : "Invalid or expired token";

            ApiResponse<Void> apiResponse = ApiResponse.error(
                    "Authentication Failed",
                    HttpStatus.UNAUTHORIZED.value(),
                    errorMsg,
                    "Invalid or expired token"
            );

            try {
                byte[] bytes = objectMapper.writeValueAsBytes(apiResponse);
                DataBuffer buffer = response.bufferFactory().wrap(bytes);
                return response.writeWith(Mono.just(buffer));
            } catch (JsonProcessingException e) {
                return Mono.error(e);
            }
        };
    }

    @Bean
    public ServerAccessDeniedHandler accessDeniedHandler() {
        return (exchange, ex) -> {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.FORBIDDEN);
            response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

            ApiResponse<Void> apiResponse = ApiResponse.error(
                    "Access Denied",
                    HttpStatus.FORBIDDEN.value(),
                    "You do not have permission to access this resource",
                    "Forbidden"
            );

            try {
                byte[] bytes = objectMapper.writeValueAsBytes(apiResponse);
                DataBuffer buffer = response.bufferFactory().wrap(bytes);
                return response.writeWith(Mono.just(buffer));
            } catch (JsonProcessingException e) {
                return Mono.error(e);
            }
        };
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint())
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/auth/**").permitAll()
                        .pathMatchers("/api/v1/products/**").permitAll()
                        .pathMatchers("/api/v1/test/public").permitAll()
                        .pathMatchers("/api/v1/test/user").hasAnyRole("USER", "ADMIN")
                        .pathMatchers("/api/v1/test/admin").hasRole("ADMIN")
                        .anyExchange().authenticated()
                )
                .build();
    }
}
