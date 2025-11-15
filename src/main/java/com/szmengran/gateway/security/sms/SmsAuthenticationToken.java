package com.szmengran.gateway.security.sms;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;

/**
 * @Description: 短信认证Token
 * @Author: Joe
 * @Date: 2025/11/15 20:29:45
 */
public class SmsAuthenticationToken extends AbstractAuthenticationToken implements Serializable {

    @Serial
    private static final long serialVersionUID = 620L;
    private final Object phone;
    private final String code;

    public SmsAuthenticationToken(Object phone, String code) {
        super(Collections.emptyList());
        this.phone = phone;
        this.code = code;
        this.setAuthenticated(false);
    }

    public SmsAuthenticationToken(Object phone, String code, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.phone = phone;
        this.code = code;
        super.setAuthenticated(true);
    }

    public static SmsAuthenticationToken unauthenticated(Object principal, String code) {
        return new SmsAuthenticationToken(principal, code);
    }

    public static SmsAuthenticationToken authenticated(Object principal, String code, Collection<? extends GrantedAuthority> authorities) {
        return new SmsAuthenticationToken(principal, code, authorities);
    }

    @Override
    public Object getCredentials() {
        return this.code;
    }

    @Override
    public Object getPrincipal() {
        return this.phone;
    }

    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        Assert.isTrue(!isAuthenticated, "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        super.setAuthenticated(false);
    }

}
