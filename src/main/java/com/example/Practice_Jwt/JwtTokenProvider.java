package com.example.Practice_Jwt;

import com.example.Practice_Jwt.Redis.RedisDao;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtTokenProvider {
    private  final RedisDao redisDao;
    private Key key;

    @Value("${jwt.secret}")
    private  String secretKey;

    @PostConstruct
    protected  void init(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
    }



    //유저 정보를 가지고 AccessToken, RefreshToken을 생성하는 메소드
    public TokenInfo generateToken(Authentication authentication){
        //권한 정보 가져오기
        String authorities = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        long now = (new Date()).getTime();

        //Access Token 생성
        Date accessTokenExpireseIn = new Date(now+86400000); // 만료 시간
        Date refreshTokenExpireseIn = new Date(now+172800000); // refresh 만료 시간

        String accessToken = Jwts.builder().setSubject(authentication.getName()).claim("auth",authorities).setExpiration(accessTokenExpireseIn).signWith(key, SignatureAlgorithm.HS256).compact();

        // Refresh Token 생성
        String refreshToken = Jwts.builder().setExpiration(refreshTokenExpireseIn).signWith(key, SignatureAlgorithm.HS256).compact();


        // refresh 토큰 저장

        redisDao.setValues(authentication.getName(),refreshToken,refreshTokenExpireseIn);



        return TokenInfo.builder().grantType("Bearer").accessToken(accessToken).refreshToken(refreshToken).build();
    }


    // Jwt 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
    public Authentication getAuthentication(String accessToken){
        Claims claims = parseClaims(accessToken);

        if(claims.get("auth") == null){
            log.warn("JwtTokenProvider 52 번째","권한 정보가 없는 토큰");
            throw new RuntimeException("권한 정보가 없는 토큰입니다.");
        }

        // 클레임에서 권한 정보 가져오기  // 문법 모르겠음
        Collection<? extends  GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(",")).map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        // UserDetails 객체를 만들어 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(),"",authorities);
        return new UsernamePasswordAuthenticationToken(principal,"",authorities);
    }

    private Claims parseClaims(String accessToken) {
        try{
            return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
        }catch (ExpiredJwtException e){
            log.warn("JWT 복호화 에러 " , e, "JwtTokenProvider");
            return e.getClaims();
        }
    }


    // 토큰 정보 검증 메소드
    public boolean validateToken(String token, HttpServletRequest request){
        try{
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        }catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e){
            e.printStackTrace();
            request.setAttribute("exception", ErrorCode.UnauthorizedException.getErrorCode());
            System.out.println("1");
        }catch (ExpiredJwtException e){
            e.printStackTrace();
            request.setAttribute("exception",ErrorCode.ExpirationException.getErrorCode());
            log.info("Expired Jwt Token",e);

        }catch (UnsupportedJwtException e){
            log.info("Unsupported JWT token",e);
            System.out.println("2");
        }catch (IllegalArgumentException e){
            log.info("JWT claims string is empty", e);
            System.out.println("3");
        }return false;
    }


}
