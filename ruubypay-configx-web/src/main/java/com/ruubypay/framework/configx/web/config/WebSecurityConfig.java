package com.ruubypay.framework.configx.web.config;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.hash.Hashing;
import com.ruubypay.framework.configx.web.service.INodeService;
import com.ruubypay.framework.configx.web.service.IRootNodeRecorder;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * webSecurity验证类
 * @author chenhaiyang
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Resource
    private IRootNodeRecorder iRootNodeRecorder;
    @Resource
    private INodeService nodeService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/css/**", "/js/**", "/image/**","/tips").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login?error=true")
                .permitAll()
                .and()
                .logout()
                .permitAll();
        http.csrf().disable();
    }

    @Bean
    public UserDetailsService getUserDetailsService() {
        return username -> {
            final String pass = nodeService.getValue(username);
            if (Strings.isNullOrEmpty(pass)) {
                throw new UsernameNotFoundException(username);
            }
            return new User(username, pass, Lists.newArrayList(new SimpleGrantedAuthority("ADMIN")));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new PasswordEncoder() {
            @Override
            public String encode(CharSequence rawPassword) {
                return Hashing.sha1().hashString(rawPassword, Charsets.UTF_8).toString();
            }

            @Override
            public boolean matches(CharSequence rawPassword, String encodedPassword) {
                return Objects.equals(encodedPassword, encode(rawPassword));
            }
        };
    }

    @Bean
    public ApplicationListener listenLoginSuccess(){
        return (event)-> {
            if (event instanceof AuthenticationSuccessEvent) {
                AuthenticationSuccessEvent authEvent = (AuthenticationSuccessEvent) event;
                UserDetails user = (UserDetails) authEvent.getAuthentication().getPrincipal();
                iRootNodeRecorder.saveNode(user.getUsername());
            }
        };
    }

}