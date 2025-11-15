package com.szmengran.gateway.security.wechat;

import com.szmengran.gateway.security.service.LoginPathService;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationFailureHandler;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers;
import org.springframework.stereotype.Component;

/**
 * @Description: 微信小程序授权登陆
 * @Auther: Maoyuan.Li
 * @Date: 2024/06/06 00:10
 */
@Component
public class AppletAuthenticationWebFilter extends AuthenticationWebFilter implements LoginPathService {

    public AppletAuthenticationWebFilter(AppletReactiveAuthenticationManager appletReactiveAuthenticationManager,
                                         AppletAuthenticationConverter appletAuthenticationConverter,
                                         ServerAuthenticationSuccessHandler successHandler, ServerAuthenticationFailureHandler failureHandler) {
        super(appletReactiveAuthenticationManager);
        this.setRequiresAuthenticationMatcher(ServerWebExchangeMatchers.pathMatchers(getLoginPath()));
        this.setAuthenticationSuccessHandler(successHandler);
        this.setAuthenticationFailureHandler(failureHandler);
        this.setServerAuthenticationConverter(appletAuthenticationConverter);
    }

    @Override
    public String getLoginPath() {
        return "/applet/login";
    }

}
