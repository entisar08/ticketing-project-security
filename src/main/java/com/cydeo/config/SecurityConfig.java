package com.cydeo.config;


import com.cydeo.service.SecurityService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {

//    @Bean
//    public UserDetailsService userDetailsService (PasswordEncoder encoder){
//
//        List<UserDetails> userList=new ArrayList<>();
//        userList.add(new User("mike", encoder.encode("password"), Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")) ));
//        userList.add(new User("ozzy", encoder.encode("password"), Arrays.asList(new SimpleGrantedAuthority("ROLE_MANAGER")) ));
//        userList.add(new User("Tike", encoder.encode("password"), Arrays.asList(new SimpleGrantedAuthority("ROLE_EMPLOYEE")) ));
//        //userList.add(new User("rike", encoder.encode("password"), Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")) ));
//
//    return new InMemoryUserDetailsManager(userList);
//    }
//if you dont want pages to be behind authentication page so it is accessible by all user
    // make use of the security filter chain provided by spring security

    private final   AuthSuccessHandler authSuccessHandler;

    private final SecurityService securityService;

    public SecurityConfig(AuthSuccessHandler authSuccessHandler, SecurityService securityService) {
        this.authSuccessHandler = authSuccessHandler;
        this.securityService = securityService;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .authorizeRequests()// evrything should be authorized
                .antMatchers("/user/**") .hasAuthority("Admin")               //anything under user controller

                //antmatchers group pages and decide the access exclude by role(.has role) or in general
                .antMatchers("/project/**").hasAuthority("Manager")
                .antMatchers("/task/employee").hasAuthority("Employee")
                .antMatchers("/task/**").hasAuthority("Manager")
                //if we are dealing with database user we should use hasAutherity
               // .antMatchers("/task/**").hasAnyRole("EMPLOYEE","ADMIN")
              //  .antMatchers("/task/**").hasAuthority("ROLE_EMPLOYEE")//Same as hasrole only uacept format role_ before role
                .antMatchers("/", //allow these
                        "/login",//skipping  the httpbasic and spring default and allowed users to access the login page without security
                        "/fragments/**",//**  means all things in that folder
                        "/assets/**",
                        "/images/**"
                        ).permitAll()
                .anyRequest().authenticated()//authenticte all the other pages
                .and()
                //.httpBasic()spring popo up username and password
                .formLogin()
                .loginPage("/login")
         //         .defaultSuccessUrl("/welcome")//override login ..what if you want everyone login  to land on a different page
                //and use success handler
                .successHandler(authSuccessHandler)
                  .failureUrl("/login?error=true")
                .permitAll()

                .and()
                .logout()
                     .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))//this part :when someone request
                // logout lamd on login page you need to update Ui and provide the link of logout
                .logoutSuccessUrl("/login")
                .and()
                //add remember me
                .rememberMe()
                    .tokenValiditySeconds(120)
                    .key("cydeo")
                    .userDetailsService(securityService)
                .and()
                .build();


    }
}
