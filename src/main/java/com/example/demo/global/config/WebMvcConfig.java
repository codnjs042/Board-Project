package com.example.demo.global.config;

import com.example.demo.global.interceptor.PostViewInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {
    private final PostViewInterceptor postViewInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(postViewInterceptor)
                .addPathPatterns("/post/*")
                .excludePathPatterns("/post/write");
    }
}
