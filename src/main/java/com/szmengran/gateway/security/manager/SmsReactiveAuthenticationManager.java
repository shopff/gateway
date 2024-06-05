package com.szmengran.gateway.security.manager;

import com.szmengran.gateway.security.dto.bo.UserInfo;
import org.springframework.security.authentication.AbstractUserDetailsReactiveAuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @Auther: Maoyuan.Li
 * @Date: 2024/05/25 10:58
 */
public class SmsReactiveAuthenticationManager extends AbstractUserDetailsReactiveAuthenticationManager {

    private ReactiveUserDetailsService userDetailsService;

    public SmsReactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService) {
        Assert.notNull(userDetailsService, "userDetailsService cannot be null");
        this.userDetailsService = userDetailsService;
    }

    protected Mono<UserDetails> retrieveUser(String username) {
        return this.userDetailsService.findByUsername(username);
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String phone = authentication.getName();
        String code = (String) authentication.getCredentials();
        // @formatter:off
        return isValidSmsCode(phone, code)
                .flatMap(isValid -> {
                    if (isValid) {
                        return retrieveUser(phone)
                          .switchIfEmpty(Mono.defer(() -> Mono.error(new UsernameNotFoundException("User not found"))))
                          .flatMap(userDetails -> {
                            if (userDetails instanceof UserInfo) {
                                ((UserInfo) userDetails).preAuthenticationChecks();
                            }
                            return Mono.just(new UsernamePasswordAuthenticationToken(userDetails, code, userDetails.getAuthorities()));
                          }
                        );
                    }
                    return Mono.error(new BadCredentialsException("Invalid SMS code"));
                });
        // @formatter:on
    }

    private Mono<Boolean> isValidSmsCode(String phone, String code) {
        return Mono.just(true);
    }

}
