package com.example.Practice_Jwt;

import lombok.Data;

@Data
public class MemberLoginRequestDto {
    private String memberId;
    private  String password;

}
