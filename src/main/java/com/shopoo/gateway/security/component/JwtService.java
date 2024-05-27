package com.shopoo.gateway.security.component;

import com.google.gson.Gson;
import com.shopoo.gateway.security.dto.bo.UserInfo;
import com.shopoo.gateway.security.config.SecurityConfigProperties;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @Description:
 * @Auther: Maoyuan.Li
 * @Date: 2024/05/23 21:08
 */
@Component
public class JwtService {

    @Autowired
    private SecurityConfigProperties securityConfigProperties;

    public String generateToken(UserInfo userInfo) {
        return Jwts.builder()
                .setSubject(new Gson().toJson(userInfo))
                .setExpiration(new Date(System.currentTimeMillis() + securityConfigProperties.getExpireTime()))
                .signWith(SignatureAlgorithm.HS256, securityConfigProperties.getKey())
                .compact();
    }

    public UserInfo getUsernameFromToken(String token) {
        String json = Jwts.parserBuilder()
                .setSigningKey(securityConfigProperties.getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        return new Gson().fromJson(json, UserInfo.class);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(securityConfigProperties.getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
