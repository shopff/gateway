package com.shopff.gateway.security.service;

import com.shopff.gateway.security.dto.bo.UserInfo;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;

/**
 * @Description:
 * @Auther: Maoyuan.Liø
 * @Date: 2024/05/26 11:44
 */
public class SmsReactiveUserDetailsService implements ReactiveUserDetailsService {

    @Override
    public Mono<UserDetails> findByUsername(String phone) {
        UserInfo user = UserInfo.builder()
                .id(1L)
                .username("admin")
                .password("{bcrypt}"+new BCryptPasswordEncoder().encode("admin"))
                .enabled(true)
                .build();

        user.roles("user");
        return Mono.just(user);
    }
}
