package com.szmengran.gateway.security.dto.co;

import com.szmengran.cola.dto.ClientObject;
import lombok.Builder;
import lombok.Data;

/**
 * @Description:
 * @Auther: Maoyuan.Li
 * @Date: 2024/05/17 00:59
 */
@Data
@Builder
public class TokenCO extends ClientObject {
    private String token;
}
