package com.szmengran.gateway.security.service;

import com.szmengran.gateway.security.dto.bo.UserInfo;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * @Description:
 * @Auther: Joe
 * @Date: 2025/11/15 11:44
 */
public abstract class AbstractReactiveUserDetailsService implements ReactiveUserDetailsService {

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        UserInfo user = UserInfo.builder()
                .id(1L)
                .username("admin")
                .password("{bcrypt}"+new BCryptPasswordEncoder().encode("admin"))
                .enabled(true)
                .build();

        user.roles("user");
        Optional.ofNullable(user).orElseThrow(() -> new UsernameNotFoundException(String.format("username [%s] no found", username)));
        return Mono.just(user);
    }

}
