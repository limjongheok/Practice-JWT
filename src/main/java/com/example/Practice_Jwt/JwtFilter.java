package com.example.Practice_Jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

//Jwt 인증정보를 SecurityContext에 저장하는 역활
// JwtFilter의 doFilter 메소드에서 request가 들어올때 securityContext 에 authenticatio
// 객체를 저장해 사용
public class JwtFilter extends GenericFilterBean {

    public static final String AUTHORIZATION_HEADER = "Authorization" ;
    private  static final Logger LOGGER = LoggerFactory.getLogger(JwtFilter.class);


    private  final TokenProvider tokenProvider;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String jwt = resolveToken(httpServletRequest);
        String requstURI = httpServletRequest.getRequestURI();

        if(StringUtils.hasText(jwt) && tokenProvider.validatedToken(jwt)){
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            LOGGER.info("Security Context에 '{}'인증 정보를 저장했습니다, uri: {}",authentication.getName(), requstURI);
        }else{
            LOGGER.info("유효한 jwt토큰이 없습니다. uri: {}", requstURI);
        }
        chain.doFilter(httpServletRequest,response);
    }
    // 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")){
            return bearerToken.substring(7); // ?? 의문점
        }
        return null;

    }
}




