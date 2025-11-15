package com.szmengran.gateway.security.sms;

import com.szmengran.gateway.security.service.LoginPathService;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @Auther: Maoyuan.Li
 * @Date: 2024/05/23 21:10
 */
@Component
public class SmsAuthenticationWebFilter extends AuthenticationWebFilter implements LoginPathService {

    public SmsAuthenticationWebFilter(SmsReactiveAuthenticationManager smsReactiveAuthenticationManager,
                                      SmsAuthenticationConverter smsAuthenticationConverter,
                                      ServerAuthenticationSuccessHandler successHandler, ServerAuthenticationFailureHandler failureHandler) {
        super(smsReactiveAuthenticationManager);
        this.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(getLoginPath()));
        this.setAuthenticationSuccessHandler(successHandler);
        this.setAuthenticationFailureHandler(failureHandler);
        this.setServerAuthenticationConverter(smsAuthenticationConverter);
    }

    @Override
    public String getLoginPath() {
        return "/sms/login";
    }

}

