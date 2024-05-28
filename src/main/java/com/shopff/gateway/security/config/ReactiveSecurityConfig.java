package com.shopff.gateway.security.config;

import com.shopff.gateway.security.component.JwtReactiveUserDetailsService;
import com.shopff.gateway.security.component.JwtService;
import com.shopff.gateway.security.component.JwtUserDetailsRepositoryReactiveAuthenticationManager;
import com.shopff.gateway.security.converter.JwtAuthenticationConverter;
import com.shopff.gateway.security.filter.JwtAuthorizationFilter;
import com.shopff.gateway.security.filter.JwtUsernamePasswordAuthenticationWebFilter;
import com.shopff.gateway.security.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

/**
 * @Description:
 * @Auther: Maoyuan.Li
 * @Date: 2024/05/23 21:06
 */
@Configuration
@EnableWebFluxSecurity
public class ReactiveSecurityConfig {

    private final JwtService jwtService;

    public ReactiveSecurityConfig(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return new JwtReactiveUserDetailsService();
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService) {
        return new JwtUserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
    }

    @Bean
    public ReactiveAuthenticationManager smsAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
        return new JwtUserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        //用户名和密码登陆方式
        AuthenticationWebFilter usernamePasswordAuthenticationWebFilter = new JwtUsernamePasswordAuthenticationWebFilter(authenticationManager(userDetailsService()), jwtService);
        usernamePasswordAuthenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(Constants.JWT_LOGIN_PATH));
        usernamePasswordAuthenticationWebFilter.setServerAuthenticationConverter(new JwtAuthenticationConverter());

        http.csrf(csrfSpec -> csrfSpec.disable())
                .authorizeExchange(authorizeExchangeSpec -> {
                    authorizeExchangeSpec
                            .pathMatchers(Constants.JWT_LOGIN_PATH).permitAll()
                            .anyExchange().authenticated();
                })
                .addFilterBefore(usernamePasswordAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .securityContextRepository(new JwtAuthorizationFilter(jwtService));

        return http.build();
    }
}
