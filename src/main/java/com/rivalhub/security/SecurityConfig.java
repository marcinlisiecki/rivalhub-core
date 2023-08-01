package com.rivalhub.security;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.cors(cors -> cors.disable());

        http.authorizeHttpRequests(requests -> requests
                .requestMatchers(new AntPathRequestMatcher("/h2-console/**")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/organization")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/register")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/login")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/users/**")).permitAll()
                .anyRequest().authenticated());


//        http.authorizeHttpRequests(request -> request
//                .requestMatchers("/h2-console/**").permitAll()
//                .requestMatchers("/register").permitAll()
//                .requestMatchers("/login").permitAll()
//                .anyRequest().permitAll());

//        http.formLogin(login -> login.loginPage("/login").permitAll());
//        http.csrf(csrf -> csrf.ignoringRequestMatchers(PathRequest.toH2Console()));
        //http.headers().frameOptions().sameOrigin();

        return http.build();
   }
}
