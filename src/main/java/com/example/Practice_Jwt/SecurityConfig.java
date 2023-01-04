package com.example.Practice_Jwt;


import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {
    private  final TokenProvider tokenPovider;
    private  final JwtAuthenticationEtryPoint jwtAuthenticationEtryPoint;

    // 403 fobidden Exception처리를 위한 클래스
    private  final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer(){
        // 필터 적용 예외
        return (web) -> web.ignoring().antMatchers("/favicon.ico");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http.csrf().disable()
                // 401 403 exception 핸들링
                .exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEtryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                // httpServletRequest 를 사용하는 요청들에 대한 접근 제한 설정
                .and()
                .authorizeRequests()
                .antMatchers("/authenticate").permitAll()

                // jwtSecurityConfig 적용
                .and()
                .apply(new JwtSecurityConfig(tokenPovider))

                .and().build();


    }

}
