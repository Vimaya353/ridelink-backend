package com.ridelink.account.config;

import com.ridelink.account.security.JwtAuthFilter;
import com.ridelink.account.security.JwtUtil;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    // Register the JWT filter for incoming requests so protected routes can check
    // tokens.
    @Bean
    public FilterRegistrationBean<JwtAuthFilter> jwtFilter(JwtUtil jwtUtil) {
        FilterRegistrationBean<JwtAuthFilter> reg = new FilterRegistrationBean<>(new JwtAuthFilter(jwtUtil));
        reg.addUrlPatterns("/*");

        // Set the filter's position in the servlet filter chain.
        reg.setOrder(1);
        return reg;
    }
}