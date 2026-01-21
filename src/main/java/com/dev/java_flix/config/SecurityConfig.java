package com.dev.java_flix.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import com.dev.java_flix.security.JwtAuthenticationFilter;

@Component
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter){
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    private static final String[] PUBLIC_ENDPOINTS = {
        "/api/auth/login",
        "/api/auth/signup",
        "/api/auth/validate-email",
        "/api/auth/verify-email",
        "/api/auth/resend-verification",
        "/api/auth/forgot-password",
        "/api/auth/reset-password"
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{

        httpSecurity.csrf( csrf -> csrf.disable())
                    .cors(cors -> {})
                    .authorizeHttpRequests( auth -> auth.requestMatchers(PUBLIC_ENDPOINTS).permitAll().anyRequest().authenticated())
                    .sessionManagement( session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                    .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return httpSecurity.build();

    }
}
