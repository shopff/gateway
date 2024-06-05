package com.szmengran.gateway.security.filter;

import com.szmengran.gateway.security.service.JwtService;
import com.szmengran.gateway.security.converter.SmsAuthenticationConverter;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;

/**
 * @Description:
 * @Auther: Maoyuan.Li
 * @Date: 2024/05/23 21:10
 */
public class SmsAuthenticationWebFilter extends AuthenticationWebFilter {

    public static final String SPRING_SECURITY_FORM_PHONE_KEY = "phone";
    public static final String SPRING_SECURITY_FORM_CODE_KEY = "code";
    private final JwtService jwtService;

    public SmsAuthenticationWebFilter(ReactiveAuthenticationManager authenticationManager, JwtService jwtService) {
        super(authenticationManager);
        this.jwtService = jwtService;
    }

    @Override
    public void setServerAuthenticationConverter(ServerAuthenticationConverter authenticationConverter) {
        super.setServerAuthenticationConverter(new SmsAuthenticationConverter());
    }
}

