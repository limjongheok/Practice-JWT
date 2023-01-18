package com.example.Practice_Jwt.Redis;

import com.example.Practice_Jwt.JwtTokenProvider;
import com.example.Practice_Jwt.TokenInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@RestController
public class RefreshTokenController {
    private  final RedisDao redisDao;
    private  final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(Authentication authentication, String refreshToken, HttpServletRequest httpServletRequest){

        // redis에서 refeshtoken 꺼내기
        String saveToken = redisDao.getValues(authentication.getName());

        if(!jwtTokenProvider.validateToken(refreshToken,httpServletRequest)){
            return ResponseEntity.badRequest().body("refresh 토큰 이증 실패 ");
        }
        if(saveToken.equals(refreshToken)){
            // 토큰 재발급
            TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
            HttpHeaders headers = new HttpHeaders();
            headers.add("Authorization","Bearer "+tokenInfo.getAccessToken());


            return ResponseEntity.ok().headers(headers).body("추가 완료" +" "+ "refresh :"+  tokenInfo.getRefreshToken());
        }else{
            return ResponseEntity.badRequest().body("refresh 토큰 인증 실패 ");
        }
        // refreshtoken 동일 비교






    }


}
