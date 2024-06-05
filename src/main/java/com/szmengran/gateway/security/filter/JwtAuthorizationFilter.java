package com.szmengran.gateway.security.filter;

import com.szmengran.gateway.security.dto.bo.UserInfo;
import com.szmengran.gateway.security.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.web.server.context.ServerSecurityContextRepository;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @Auther: Maoyuan.Li
 * @Date: 2024/05/23 21:13
 */

public class JwtAuthorizationFilter implements ServerSecurityContextRepository {

    private final JwtService jwtService;

    public JwtAuthorizationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Void> save(ServerWebExchange exchange, SecurityContext context) {
        return Mono.empty();
    }

    @Override
    public Mono<SecurityContext> load(ServerWebExchange exchange) {
        String authHeader = exchange.getRequest().getHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String authToken = authHeader.substring(7);
            UserInfo userInfo = jwtService.getUsernameFromToken(authToken);
            if (userInfo != null && jwtService.validateToken(authToken)) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userInfo.getUsername(), userInfo.getPassword(), userInfo.getAuthorities());
                return Mono.just(new SecurityContextImpl(auth));
            }
        }
        return Mono.empty();
    }
}


