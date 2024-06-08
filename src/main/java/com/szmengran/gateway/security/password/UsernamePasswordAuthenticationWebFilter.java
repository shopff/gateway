package com.szmengran.gateway.security.password;

import com.szmengran.gateway.security.service.LoginPathService;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
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
public class UsernamePasswordAuthenticationWebFilter extends AuthenticationWebFilter implements LoginPathService {

    public UsernamePasswordAuthenticationWebFilter(ServerAuthenticationSuccessHandler successHandler, ServerAuthenticationFailureHandler failureHandler) {
        super(new UsernamePasswordReactiveAuthenticationManager(new UsernamePasswordReactiveUserDetailsService()));
        this.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(getLoginPath()));
        this.setAuthenticationSuccessHandler(successHandler);
        this.setAuthenticationFailureHandler(failureHandler);
        this.setServerAuthenticationConverter(new UsernamePasswordAuthenticationConverter());
    }

    @Override
    public String getLoginPath() {
        return "/login";
    }

}

