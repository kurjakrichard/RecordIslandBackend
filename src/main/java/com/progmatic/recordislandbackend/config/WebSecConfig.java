/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.progmatic.recordislandbackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 *
 * @author balza
 */
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .formLogin()
                .loginPage("/login")
                //.permitAll()
                .defaultSuccessUrl("/recordisland", true)
                .and()
                .logout()
                .and()
                .authorizeRequests()
                .antMatchers("/recordisland", "/register", "/login").permitAll()
                //.antMatchers("/recordisland/create").access("hasRole('ADMIN')")
                .antMatchers("/css/*", "/js/*", "/images/*", "/favicon.ico").permitAll()
                .anyRequest().authenticated();

    }

//    @Bean
//    @Override
//    public UserDetailsService userDetailsService() {
//        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
////        manager.createUser(User.withUsername("user").password("password").roles("USER").build());
////        manager.createUser(User.withUsername("admin").password("password").roles("ADMIN").build());
//        manager.createUser(new User("user", "password", "test@email.com", LocalDate.of(1986, Month.MARCH,15), "ROLE_USER"));
//        manager.createUser(new User("admin", "password", "admin@email.com", LocalDate.of(1986, Month.MARCH,15), "ROLE_ADMIN"));
//        return manager;
//    }
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
