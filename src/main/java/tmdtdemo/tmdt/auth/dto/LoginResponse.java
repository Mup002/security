package tmdtdemo.tmdt.auth.dto;

import lombok.Data;

@Data
public class LoginResponse {
    private String accessToken;
    private String accessExpiration;
    private String refreshToken;
    private String refreshExpiration;
}
