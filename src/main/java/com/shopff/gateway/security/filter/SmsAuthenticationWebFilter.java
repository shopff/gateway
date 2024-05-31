package com.shopff.gateway.security.filter;

import com.shopff.gateway.security.service.JwtService;
import com.shopff.gateway.security.converter.SmsAuthenticationConverter;
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
public class SmsAuthenticationWebFilter extends AuthenticationWebFilter implements ServerAuthenticationFailureHandler {

    public static final String SPRING_SECURITY_FORM_PHONE_KEY = "phone";
    public static final String SPRING_SECURITY_FORM_CODE_KEY = "code";
    private final JwtService jwtService;

    public SmsAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager, JwtService jwtService) {
        super(authenticationManager);
        this.jwtService = jwtService;
    }
//
//    @Override
//    protected Mono<Void> onAuthenticationSuccess(Authentication authentication, WebFilterExchange webFilterExchange) {
//        UserInfo user = (UserInfo)authentication.getPrincipal();
//        user.setPassword(null);
//        String token = jwtService.generateToken(user);
//        ServerWebExchange exchange = webFilterExchange.getExchange();
//        ServerHttpResponse response = exchange.getResponse();
//
//        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
//        TokenCO tokenCO = TokenCO.builder().token(token).build();
//        return response.writeWith(Mono.just(response.bufferFactory().wrap(new Gson().toJson(tokenCO).getBytes())));
//
//    }

    @Override
    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
        webFilterExchange.getExchange().getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return webFilterExchange.getExchange().getResponse().setComplete();
    }

    @Override
    public void setServerAuthenticationConverter(ServerAuthenticationConverter authenticationConverter) {
        super.setServerAuthenticationConverter(new SmsAuthenticationConverter());
    }
}

