package com.shopff.gateway.security.converter;

import com.shopff.gateway.security.filter.SmsAuthenticationWebFilter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.util.Assert;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @Auther: Maoyuan.Li
 * @Date: 2024/05/23 21:11
 */
public class SmsAuthenticationConverter implements ServerAuthenticationConverter {

    @Override
    public Mono<Authentication> convert(ServerWebExchange exchange) {
        return exchange.getFormData().flatMap(data -> {
            String phone = data.getFirst(SmsAuthenticationWebFilter.SPRING_SECURITY_FORM_PHONE_KEY);
            String code = data.getFirst(SmsAuthenticationWebFilter.SPRING_SECURITY_FORM_CODE_KEY);
            Assert.notNull(phone, "phone cannot be null");
            Assert.notNull(code, "code cannot be null");
            return Mono.just(new UsernamePasswordAuthenticationToken(phone, code));
        });
    }
}
