package com.szmengran.gateway.security.config;

import com.szmengran.gateway.security.filter.JwtAuthorizationFilter;
import com.szmengran.gateway.security.service.JwtService;
import com.szmengran.gateway.security.service.LoginPathService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

import java.util.List;

/**
 * @Description:
 * @Auther: Maoyuan.Li
 * @Date: 2024/05/23 21:06
 */
@Configuration
@EnableWebFluxSecurity
@RequiredArgsConstructor
public class ReactiveSecurityConfig {

    // token服务
    private final JwtService jwtService;
    private final List<LoginPathService> loginPathService;
    private final List<AuthenticationWebFilter> authenticationWebFilterList;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(csrfSpec -> csrfSpec.disable());

        http.authorizeExchange(
                authorizeExchangeSpec -> {
                    loginPathService.forEach(service -> {
                        authorizeExchangeSpec.pathMatchers(service.getLoginPath()).permitAll();
                    });
                }
        );
        http.authorizeExchange(authorizeExchangeSpec -> {
            authorizeExchangeSpec.anyExchange().authenticated();
        });
        authenticationWebFilterList.forEach(authenticationWebFilter -> {
            http.addFilterBefore(authenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION);
        });
        http.securityContextRepository(new JwtAuthorizationFilter(jwtService));
        return http.build();
    }
}
