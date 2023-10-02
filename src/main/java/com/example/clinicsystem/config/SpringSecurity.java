package com.example.clinicsystem.config;

import com.example.clinicsystem.security.LoginSuccessHandler;
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
    public WebSecurityCustomizer webSecurityCustomizer(){
        return web -> web.ignoring()
                .requestMatchers("/css/**");
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeHttpRequests((authorize) ->
                {

                    try {
                        authorize.requestMatchers("/register/**", "/index", "/login", "/custom-403", "/doctors").permitAll()
                                .requestMatchers("/users", "/users/**", "/users/edit/**", "/delete/**","/admin/diagnoses","/admin_appointments","/pending-users","/admin/diagnoses/by-appointment/{appointmentId}","/admin/diagnoses/save","/welcome").hasAuthority("ADMIN")
                                .requestMatchers("/booking_form", "/booking_form/**", "/appointment/**").hasAnyAuthority("ADMIN", "PATIENT")
                                .requestMatchers("/doctors","/getDoctorsBySpecialty").hasAnyAuthority("ADMIN","DOCTOR")
                                .requestMatchers("/welcome/user","/profile/edit","/delete-appointment/{id}","/diagnoses/by-patient","/bmi_calculation","/ideal_weight","/fat_calculation","/first_aid").hasAuthority("PATIENT")
                                .requestMatchers("/welcome/doctor","/doctor/appointments","/doctor/search-history","/doctor/appointments/search","/diagnoses/save","/diagnoses/by-doctor","/diagnoses/by-appointment/{appointmentId}").hasAuthority("DOCTOR")
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
}
