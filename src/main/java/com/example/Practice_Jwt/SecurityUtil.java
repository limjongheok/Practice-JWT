package com.example.Practice_Jwt;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

//Spring Security Api 호출시 Member 정보가 헤더에 담겨 올텐데 어떤 Member가 API 를 요청했는지 조회하는 코드
public class SecurityUtil {
    public static  String getCurrentMmeberId(){
        final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new RuntimeException("No authentication information.");
        }
        return authentication.getName();
    }
}
