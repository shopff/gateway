package com.shopoo.gateway.security.config;

import com.shopoo.gateway.security.component.JwtReactiveUserDetailsService;
import com.shopoo.gateway.security.component.JwtUserDetailsRepositoryReactiveAuthenticationManager;
import com.shopoo.gateway.security.converter.JwtAuthenticationConverter;
import com.shopoo.gateway.security.dto.bo.UserInfo;
import com.shopoo.gateway.security.filter.JwtUsernamePasswordAuthenticationWebFilter;
import com.shopoo.gateway.security.filter.JwtAuthorizationFilter;
import com.shopoo.gateway.security.component.JwtService;
import com.shopoo.gateway.security.utils.Constants;
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

        http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers(Constants.JWT_LOGIN_PATH, "/login").permitAll()
                .anyExchange().authenticated()
                .and()
                .addFilterBefore(usernamePasswordAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .securityContextRepository(new JwtAuthorizationFilter(jwtService));

        return http.build();
    }
}
