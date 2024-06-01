package com.shopff.gateway.security.filter;

import com.shopff.gateway.security.service.JwtService;
import com.shopff.gateway.security.converter.UsernamePasswordAuthenticationConverter;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @Auther: Maoyuan.Li
 * @Date: 2024/05/23 21:10
 */
public class UsernamePasswordAuthenticationWebFilter extends AuthenticationWebFilter {

    private final JwtService jwtService;

    public UsernamePasswordAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager, JwtService jwtService) {
        super(authenticationManager);
        this.jwtService = jwtService;
    }

    @Override
    public void setServerAuthenticationConverter(ServerAuthenticationConverter authenticationConverter) {
        super.setServerAuthenticationConverter(new UsernamePasswordAuthenticationConverter());
    }
}

