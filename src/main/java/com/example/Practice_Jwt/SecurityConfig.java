package com.example.Practice_Jwt;

import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


/// security에서 사용할 보안 설정을 넣는 공간
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {
    private  final JwtTokenProvider jwtTokenProvider;

    @Bean
    public SecurityFilterChain filterChain (HttpSecurity http) throws Exception{
        http.httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()
                .authorizeRequests()
                .antMatchers("/members/login").permitAll()
                .antMatchers("/members/test").hasRole("USER")
                .anyRequest().authenticated()
                .and()
                .exceptionHandling().authenticationEntryPoint(new CustomAuthenticationEntryPoint())
                .and()
                // Jwt 인증을 위하여 직접 구현한 필터를 UsernamePasswordAuthenticationFilter 전에 실행 한다는 설정
                .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)

        ;
        return http.build();
    }
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
