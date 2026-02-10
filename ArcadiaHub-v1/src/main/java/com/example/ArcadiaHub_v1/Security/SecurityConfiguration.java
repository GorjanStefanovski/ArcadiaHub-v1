package com.example.ArcadiaHub_v1.Security;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,OAuth2SuccessHandler handler) throws Exception {
        http
                .authorizeHttpRequests(auth->auth
                        .requestMatchers("/login","/userLogin").permitAll()
                        .requestMatchers("/images/**", "/js/**", "/css/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2->
                        oauth2.successHandler(handler));

        return http.build();
    }
}
