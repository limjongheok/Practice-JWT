package com.example.Practice_Jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

// 403    처리를 위한 클래스
// Object to Json 을 위한 CmmnVar.GSON 은 공통 스태틱 클래스에 생성해 놓은 Gson
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(JwtAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        PrintWriter writer = response.getWriter();
        ErrorCode errorCode = CommonErrorCode.FORBIDDEN;
        ResVO res = ResVO.builder().status(errorCode.getResultCode())
                .message(errorCode.getResultMsg()).build();

        try{
            response.setContentType(MediaType.APPLICATION_CBOR_VALUE);
            writer.write(CmmnVar.GSON.toJson(res));
        }catch (NullPointerException e){
            LOGGER.error("응답 메시지 작성 에러",e);
        }finally {
            if(writer != null){
                writer.flush();
                writer.close();
            }
        }
        response.getWriter().write(CmmnVar.GSON.toJson(res));
    }
}
