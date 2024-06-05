package com.szmengran.gateway.security.dto.bo;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import java.util.*;

/**
 * @Description:
 * @Auther: Maoyuan.Li
 * @Date: 2024/05/23 19:32
 */
@Data
@Slf4j
@Builder
public class UserInfo implements UserDetails {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 昵称
     */
    private String nickname;

    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 用户状态
     */
    private Boolean enabled;
    /**
     * 权限数据
     */
    private Set<SimpleGrantedAuthority> authorities;


    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    public UserInfo roles(String... roles) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        for (String role : roles) {
            Assert.isTrue(!role.startsWith("ROLE_"),
                    () -> role + " cannot start with ROLE_ (it is automatically added)");
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role));
        }
        this.setAuthorities(authorities);
        return this;
    }

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public void preAuthenticationChecks() {
        log.debug("user info pre checks");
        if (!this.isAccountNonLocked()) {
            throw new LockedException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked",
                    "User account is locked"));
        }
        if (!this.isEnabled()) {
            throw new DisabledException(
                    this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.disabled", "User is disabled"));
        }
        if (!this.isAccountNonExpired()) {
            throw new AccountExpiredException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.expired", "User account has expired"));
        }
    }
}
