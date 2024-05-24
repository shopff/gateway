package com.szmengran;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
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
}
