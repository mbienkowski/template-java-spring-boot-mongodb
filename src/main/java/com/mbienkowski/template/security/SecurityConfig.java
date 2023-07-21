package com.mbienkowski.template.security;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON_VALUE;
import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mbienkowski.template.user.security.JwtRequestFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(jsr250Enabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public ObjectMapper objectMapper() {
        var mapper = new ObjectMapper();
        mapper.setSerializationInclusion(NON_NULL);
        return mapper;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        var delPasswordEncoder = (DelegatingPasswordEncoder) PasswordEncoderFactories.createDelegatingPasswordEncoder();
        var bcryptPasswordEncoder = new BCryptPasswordEncoder();
        delPasswordEncoder.setDefaultPasswordEncoderForMatches(bcryptPasswordEncoder);
        return delPasswordEncoder;
    }

    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(JwtRequestFilter jwtRequestFilter, HttpSecurity http, ObjectMapper mapper) throws Exception {
        // Enable CORS and disable CSRF
        http.cors().and().csrf().disable()
            .sessionManagement().sessionCreationPolicy(STATELESS);

        // Set permissions on endpoints
        http.authorizeHttpRequests()
            .requestMatchers("/health", "/api/v1/user/authenticate").permitAll()
            .anyRequest().authenticated();

        // Set filter that will authenticate the users via auth header
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        // Return 401 instead of 403 for unauthenticated requests
        http.exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint(mapper));

        return http.build();
    }

    @RequiredArgsConstructor
    public static class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

        final ObjectMapper objectMapper;

        @Override
        public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
            var problem = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), "Cannot access specified resource without authorization");
            problem.setInstance(URI.create(request.getRequestURI()));
            problem.setType(URI.create("/errors/unauthorized"));

            response.addHeader(CONTENT_TYPE, APPLICATION_PROBLEM_JSON_VALUE);
            response.setStatus(401);

            var responseStream = response.getOutputStream();
            objectMapper.writeValue(responseStream, problem);
            responseStream.flush();
        }
    }
}
