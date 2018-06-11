package com.ruubypay.framework.configx.web.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * 登录控制层
 * @author chenhaiyang
 */
@Controller
public class LoginController {

    /**
     * 登录跳转
     * @return 登录页
     */
    @GetMapping(value = "/login")
    public ModelAndView login() {
        return new ModelAndView("login");
    }
}
