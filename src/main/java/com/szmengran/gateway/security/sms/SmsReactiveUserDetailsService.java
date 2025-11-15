package com.szmengran.gateway.security.sms;

import com.szmengran.gateway.security.dto.bo.UserInfo;
import com.szmengran.gateway.security.service.AbstractReactiveUserDetailsService;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * @Description:
 * @Auther: Maoyuan.Liø
 * @Date: 2024/05/26 11:44
 */
public class SmsReactiveUserDetailsService extends AbstractReactiveUserDetailsService implements ReactiveUserDetailsService {

    @Override
    public Mono<UserDetails> findByUsername(String phone) {
        return super.findByUsername(phone);
    }
}
