package com.szmengran.gateway.security.handler;

import com.google.gson.Gson;
import com.szmengran.gateway.security.service.JwtService;
import com.szmengran.gateway.security.dto.bo.UserInfo;
import com.szmengran.gateway.security.dto.co.TokenCO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @Auther: Maoyuan.Li
 * @Date: 2024/06/01 01:39
 */
@Component
@RequiredArgsConstructor
public class JwtServerAuthenticationSuccessHandler implements ServerAuthenticationSuccessHandler {

    private final JwtService jwtService;

    @Override
    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
        UserInfo user = (UserInfo)authentication.getPrincipal();
        user.setPassword(null);
        String token = jwtService.generateToken(user);
        ServerWebExchange exchange = webFilterExchange.getExchange();
        ServerHttpResponse response = exchange.getResponse();

        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        TokenCO tokenCO = TokenCO.builder().token(token).build();
        return response.writeWith(Mono.just(response.bufferFactory().wrap(new Gson().toJson(tokenCO).getBytes())));
    }
}
