package com.example.registrationlogindemo.config;

import com.example.registrationlogindemo.security.LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class SpringSecurity {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public static PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Autowired
    private LoginSuccessHandler loginSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests((authorize) ->
                {

                    try {
                        authorize.requestMatchers("/register/**").permitAll()
                                .requestMatchers("/index").permitAll()
                                .requestMatchers("/users").hasAnyAuthority("ADMIN","DOCTOR")
                                .requestMatchers("/login").permitAll()
                                .requestMatchers("/booking_form").hasAuthority("PATIENT")
                                .requestMatchers("/booking_form/**").hasAuthority("PATIENT")
                                .requestMatchers("/custom-403").permitAll()
                                .requestMatchers("/users/**").hasAuthority("ADMIN")
                                .requestMatchers("/users/edit/**").hasAuthority("ADMIN")
                                .requestMatchers("/delete/**").hasAuthority("ADMIN")
                                .requestMatchers("/doctors").permitAll()
                                .requestMatchers("/appointment/**").hasAuthority("PATIENT")
                                .requestMatchers("/doctor/appointments").permitAll()//hasAuthority("DOCTOR")
                                .requestMatchers("/diagnoses/by-doctor").hasAuthority("DOCTOR")
                                .anyRequest().authenticated()
                                .and()
                                .exceptionHandling((exceptionHandling) ->
                                        exceptionHandling
                                                .accessDeniedPage("/custom-403")
                ).formLogin(
                        form -> form
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                //.defaultSuccessUrl("/index")
                                .successHandler(loginSuccessHandler)
                                .permitAll()
                ).logout(
                        logout -> logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .permitAll()
                );
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                });
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring()
                .requestMatchers("/css/**","/js/**");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }
}
