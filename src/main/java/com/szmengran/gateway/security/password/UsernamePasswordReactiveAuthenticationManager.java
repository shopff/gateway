package com.szmengran.gateway.security.password;

import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AbstractUserDetailsReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @Auther: Maoyuan.Li
 * @Date: 2024/05/25 10:58
 */
public class UsernamePasswordReactiveAuthenticationManager extends AbstractUserDetailsReactiveAuthenticationManager {

    private ReactiveUserDetailsService userDetailsService;

    public UsernamePasswordReactiveAuthenticationManager(UsernamePasswordReactiveUserDetailsService userDetailsService) {
        Assert.notNull(userDetailsService, "userDetailsService cannot be null");
        this.userDetailsService = userDetailsService;
    }
    @Override
    protected Mono<UserDetails> retrieveUser(String username) {
        return this.userDetailsService.findByUsername(username);
    }
}
