package com.shopff.gateway.security.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

/**
 * @Description:
 * @Auther: Maoyuan.Li
 * @Date: 2024/06/01 12:12
 */
@Component
public class JwtServerAuthenticationFailureHandler implements ServerAuthenticationFailureHandler {

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        ServerHttpResponse response = webFilterExchange.getExchange().getResponse();
        response.setStatusCode(HttpStatus.UNAUTHORIZED);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String errorMessage = "{\"error\": \"Authentication failed\", \"message\": \"" + exception.getMessage() + "\"}";

        return response.writeWith(Mono.just(response.bufferFactory().wrap(errorMessage.getBytes(StandardCharsets.UTF_8))));
    }
}
