package com.szmengran.gateway.security.config;

import io.jsonwebtoken.security.Keys;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.List;

/**
 * @Description: 安全配置信息
 * @Auther: Maoyuan.Li
 * @Date: 2024/05/22 10:53
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Configuration
@ConfigurationProperties(prefix="secure")
public class SecurityConfigProperties {

    //jwt密钥
    private String key = "5Vtq4Qf3XeThWmZq4t7w9zxCW3A1CNcR9zxCW3A1CNcR";

    //过期时间
    private Integer expireTime = 1000 * 60 * 60 * 24 * 7;

    //发行者
    private String issuer = "szmengran";

    //白名单配置
    private White white = new White();

    //黑名单配置
    private Black black = new Black();

    public Key getKey() {
        return Keys.hmacShaKeyFor(this.key.getBytes(StandardCharsets.UTF_8));
    }

    @Data
    public static class White {

        //白名单IP列表
        private List<String> ips;

        //白名单URL列表
        private List<String> urls;
    }

    @Data
    private static class Black {

        //黑名单IP列表
        private List<String> ips;
    }
}
