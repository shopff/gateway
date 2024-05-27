package com.shopff.gateway.fallback;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description 降级回调
 * @Date 2020/12/15 1:28 下午
 * @Author <a href="mailto:android_li@sina.cn">Joe</a>
 **/
@RestController
@RequestMapping("/fallback")
public class FallbackController {
    @RequestMapping("")
    public String fallback(){
        return "error";
    }
}
