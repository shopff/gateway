package com.szmengran.gateway.security.password;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @Auther: Maoyuan.Li
 * @Date: 2024/05/23 21:11
 */
public class UsernamePasswordAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange.getFormData().flatMap(data -> {
            String username = data.getFirst(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_USERNAME_KEY);
            String password = data.getFirst(UsernamePasswordAuthenticationFilter.SPRING_SECURITY_FORM_PASSWORD_KEY);
            Assert.notNull(username, "username cannot be null");
            Assert.notNull(password, "password cannot be null");
            return Mono.just(new UsernamePasswordAuthenticationToken(username, password));
        });
    }
}
