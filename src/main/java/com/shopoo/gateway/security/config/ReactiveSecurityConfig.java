package com.shopoo.gateway.security.config;

import com.shopoo.gateway.security.converter.JwtAuthenticationConverter;
import com.shopoo.gateway.security.filter.JwtAuthenticationWebFilter;
import com.shopoo.gateway.security.filter.JwtAuthorizationFilter;
import com.shopoo.gateway.security.component.JwtService;
import com.shopoo.gateway.security.utils.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
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
        UserDetails user = User.withDefaultPasswordEncoder()
                .username("admin")
                .password("admin")
                .roles("USER")
                .build();
        return new MapReactiveUserDetailsService(user);
    }
    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService());
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        AuthenticationWebFilter jwtAuthenticationFilter = new JwtAuthenticationWebFilter(authenticationManager(userDetailsService()), jwtService);
        jwtAuthenticationFilter.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(Constants.JWT_LOGIN_PATH));
        jwtAuthenticationFilter.setServerAuthenticationConverter(new JwtAuthenticationConverter());

        http
                .csrf().disable()
                .authorizeExchange()
                .pathMatchers(Constants.JWT_LOGIN_PATH, "/login").permitAll()
                .anyExchange().authenticated()
                .and()
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .securityContextRepository(new JwtAuthorizationFilter(jwtService));

        return http.build();
    }
}
