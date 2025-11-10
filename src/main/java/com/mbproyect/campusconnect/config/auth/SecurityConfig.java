package com.mbproyect.campusconnect.config.auth;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;


import org.springframework.context.annotation.Bean;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    public SecurityConfig(JwtFilter jwtFilter, OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.jwtFilter = jwtFilter;
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    /**
     * Modifies the security filters
     * Allows public access to "login", "register", etc.
     * Disables the csrf in order to make a stateless request server
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity
                .csrf(customizer -> customizer.disable())
                .authorizeHttpRequests(request -> request
                        .requestMatchers(
                                "/auth/login",
                                "/auth/register",
                                "/auth/validate-code",
                                "/auth/activate-account",
                                "/auth/refresh-token",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/webjars/**"
                        ) // Authorized endpoints
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(oAuth2SuccessHandler)
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                ).
                exceptionHandling(ex -> ex
                        .authenticationEntryPoint(unauthorizedEntryPoint())
                )
                // Adds the JWT filter before the authentication
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> null;
    }

    @Bean
    public AuthenticationEntryPoint unauthorizedEntryPoint() {
        return (request, response, authException) -> {
            response.setContentType("application/json");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"Unauthorized or invalid token\"}");
        };
    }

}

