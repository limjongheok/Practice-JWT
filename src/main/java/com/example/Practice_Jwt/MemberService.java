package com.example.Practice_Jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


//로그인 과정은 크게 3단계이다.
//
//1. 로그인 요청으로 들어온 memberId, password를 기반으로 Authentication 객체를 생성한다.
//
//2. authenticate() 메서드를 통해 요청된 Member에 대한 검증이 진행된다.
//
//3. 검증이 정상적으로 통과되었다면 인증된 Authentication 객체를 기반으로 JWT 토큰을 생성한다.
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MemberService {
    private  final MemberRepository memberRepository;
    private  final AuthenticationManagerBuilder authenticationManagerBuilder;
    private  final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public TokenInfo login(String memberId, String password){
        //1.Login ID/PW 기반으로 Authentication 객체 생성
        // 이때 authentication는 인증 여부를 확인하는 authenticated 값이 false
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberId, password);


        // 2. 실제 검증 (사용자 비밀번호 체크) 이루어지는 부분
        // authenticate 메서드가 실행 될때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);


        // 3. 인증 정보를 기반으로 JWT토큰 생성
        TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        return  tokenInfo;
    }


}
