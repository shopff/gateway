package com.szmengran.gateway.security.config;

import com.szmengran.gateway.security.filter.JwtAuthorizationFilter;
import com.szmengran.gateway.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Description: Simplified Gateway Security Config - JWT Validation Only
 * @Auther: Maoyuan.Li
 * @Date: 2024/05/23 21:06
 * @Modified: Joe - 2025/11/16 - Removed authentication filters, only JWT validation
 */
@Slf4j
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class ReactiveSecurityConfig {

    // token服务
    private final JwtService jwtService;
    private final List<AuthenticationWebFilter> authenticationWebFilterList;
    private final SecurityConfigProperties securityConfigProperties;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(ServerHttpSecurity.CsrfSpec::disable);

        http.authorizeExchange(
                authorizeExchangeSpec -> {
                    if (Objects.nonNull(securityConfigProperties.getWhite()) && !CollectionUtils.isEmpty(securityConfigProperties.getWhite().getUrls())) {
                        securityConfigProperties.getWhite().getUrls().forEach(url -> {
                            authorizeExchangeSpec.pathMatchers(url).permitAll();
                        });
                    }
                    authorizeExchangeSpec.anyExchange().authenticated();
                }
        );

        authenticationWebFilterList.forEach(authenticationWebFilter -> {
            http.addFilterBefore(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        });

        http.securityContextRepository(new JwtAuthorizationFilter(jwtService));

        return http.build();
    }

}
