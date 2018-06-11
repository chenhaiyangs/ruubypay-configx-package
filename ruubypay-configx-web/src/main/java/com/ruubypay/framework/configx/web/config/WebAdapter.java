package com.ruubypay.framework.configx.web.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 处理404,405错误
 * @author chenhaiyang
 */
@Configuration
public class WebAdapter implements WebMvcConfigurer {

    private static final List<Integer> ERROR_STATUS= Stream.of(404,405).collect(Collectors.toList());

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override
            public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
                if(ERROR_STATUS.contains(response.getStatus()) && modelAndView!=null){
                    modelAndView.setViewName("redirect:/version");
                }
            }
        }).addPathPatterns("/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }

}