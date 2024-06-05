package com.szmengran.gateway.security.config;

import com.szmengran.gateway.security.converter.UsernamePasswordAuthenticationConverter;
import com.szmengran.gateway.security.filter.JwtAuthorizationFilter;
import com.szmengran.gateway.security.filter.SmsAuthenticationWebFilter;
import com.szmengran.gateway.security.filter.UsernamePasswordAuthenticationWebFilter;
import com.szmengran.gateway.security.manager.SmsReactiveAuthenticationManager;
import com.szmengran.gateway.security.service.JwtService;
import com.szmengran.gateway.security.service.SmsReactiveUserDetailsService;
import com.szmengran.gateway.security.service.UsernamePasswordReactiveUserDetailsService;
import com.szmengran.gateway.security.utils.Constants;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;

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
    // 认证成功处理服务
    private final ServerAuthenticationSuccessHandler serverAuthenticationSuccessHandler;
    // 认证失败处理服务
    private final ServerAuthenticationFailureHandler serverAuthenticationFailureHandler;

    @Bean
    public ReactiveUserDetailsService userDetailsService() {
        return new UsernamePasswordReactiveUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
    }

    public ReactiveAuthenticationManager smsAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
        return new SmsReactiveAuthenticationManager(userDetailsService());
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        //用户名和密码登陆方式
        AuthenticationWebFilter usernamePasswordAuthenticationWebFilter = new UsernamePasswordAuthenticationWebFilter(authenticationManager(userDetailsService()), jwtService);
        usernamePasswordAuthenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(Constants.JWT_LOGIN_PATH));
        usernamePasswordAuthenticationWebFilter.setServerAuthenticationConverter(new UsernamePasswordAuthenticationConverter());
        usernamePasswordAuthenticationWebFilter.setAuthenticationSuccessHandler(serverAuthenticationSuccessHandler);
        usernamePasswordAuthenticationWebFilter.setAuthenticationFailureHandler(serverAuthenticationFailureHandler);

        AuthenticationWebFilter smsAuthenticationWebFilter = new SmsAuthenticationWebFilter(smsAuthenticationManager(new SmsReactiveUserDetailsService()), jwtService);
        smsAuthenticationWebFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(Constants.SMS_LOGIN_PATH));
        smsAuthenticationWebFilter.setServerAuthenticationConverter(new UsernamePasswordAuthenticationConverter());
        smsAuthenticationWebFilter.setAuthenticationSuccessHandler(serverAuthenticationSuccessHandler);
        smsAuthenticationWebFilter.setAuthenticationFailureHandler(serverAuthenticationFailureHandler);


        http.csrf(csrfSpec -> csrfSpec.disable())
                .authorizeExchange(authorizeExchangeSpec -> {
                    authorizeExchangeSpec
                            .pathMatchers(Constants.JWT_LOGIN_PATH, Constants.SMS_LOGIN_PATH).permitAll()
                            .anyExchange().authenticated();
                })
                .addFilterBefore(usernamePasswordAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .addFilterBefore(smsAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .securityContextRepository(new JwtAuthorizationFilter(jwtService));

        return http.build();
    }
}
