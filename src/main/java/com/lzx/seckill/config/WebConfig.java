package com.lzx.seckill.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * 自定义web配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private UserArgumentResolver userArgumentResolver;

    /**
     * 添加自定义的参数解析器
     * @param list
     */
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> list) {
        list.add(userArgumentResolver);
    }
}
