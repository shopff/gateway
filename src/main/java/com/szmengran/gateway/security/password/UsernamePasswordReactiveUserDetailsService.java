package com.szmengran.gateway.security.password;

import com.szmengran.gateway.security.dto.bo.UserInfo;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * @Description:
 * @Auther: Maoyuan.Liø
 * @Date: 2024/05/26 11:44
 */
@Primary
@Component
public class UsernamePasswordReactiveUserDetailsService implements ReactiveUserDetailsService {

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
