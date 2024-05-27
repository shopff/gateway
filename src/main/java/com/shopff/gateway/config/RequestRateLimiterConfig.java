package com.shopff.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

import java.security.Principal;

/**
 * @Description 限流规则配置
 * @Date 2020/12/14 4:26 下午
 * @Author <a href="mailto:android_li@sina.cn">Joe</a>
 **/
@Configuration
public class RequestRateLimiterConfig {

    /**
     * @Description 根据IP请求进行限流
     *
     * @Param []
     * @Return org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
     * @Date 4:37 下午 2020/12/14
     * @Author <a href="mailto:android_li@sina.cn">Joe</a>
     */
    @Bean
    @Primary
    KeyResolver ipKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostString());
    }

    /**
     * @Description 根据Principal请求限流
     *
     * @Param []
     * @Return org.springframework.cloud.gateway.filter.ratelimit.KeyResolver
     * @Date 4:24 下午 2021/1/3
     * @Author <a href="mailto:android_li@sina.cn">Joe</a>
     */
    @Bean
    KeyResolver principalKeyResolver() {
        return exchange -> exchange.getPrincipal().map(Principal::getName).switchIfEmpty(Mono.empty());
    }

}
