package com.szmengran;

import static org.junit.Assert.assertTrue;

import com.szmengran.gateway.security.dto.bo.UserInfo;
import org.junit.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue()
    {
        PathMatcher pathMatcher = new AntPathMatcher();
        boolean flag = pathMatcher.match("/**/system/users/{userId}", "/baas/bcb/system/users/1");
        assertTrue( flag );
    }

    @Test
    public void userInfo() {
        UserDetails userDetails = get();
        if (userDetails instanceof UserInfo) {
            System.out.println(((UserInfo) userDetails).getId());
        }
    }

    private UserDetails get () {
        return UserInfo.builder()
                .id(1L)
                .username("admin")
                .password("{bcrypt}"+new BCryptPasswordEncoder().encode("admin"))
                .enabled(true)
                .build().roles("USER");
    }
}
