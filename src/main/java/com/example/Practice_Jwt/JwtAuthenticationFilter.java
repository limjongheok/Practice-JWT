package com.example.Practice_Jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

// 클라이언트 요청시 JWT 인증을 하기 위해 설치하는 커스텀 필터로 UsernamePasswordAuthenticationFilter 이전에 시랳ㅇ
// 이전에 실행된다는 뜻은 JWTAuthenticationFilter를 통과하면 UsernamePasswordAuthenticationFilter 이후 필터는 통과
// 쉽게 말해 Username + Password를 통한 인증을 Jwt 통해 수행

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        // 1. Request Header에서 JWT 토큰 추출
        String token = resolveToken((HttpServletRequest) request);
        if(token != null && jwtTokenProvider.validateToken(token)){
            // 토큰이 유효할 경우 토큰에서 Authenticatino객체를 가져와 SecurityContext에 저장
            Authentication authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request,response);
    }
    //Request Header 에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")){
            return bearerToken.substring(7);
        }
        return  null;
    }
}
