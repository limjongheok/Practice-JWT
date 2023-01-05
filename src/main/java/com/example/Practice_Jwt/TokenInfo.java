package com.example.Practice_Jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class TokenInfo {
    private  String grantType; // grantType 은 JWT대한 인증 타입으로 Bearer  이후 http 헤더에 prefix로 붙여주는 타입
    private  String accessToken;
    private  String refreshToken;

}
