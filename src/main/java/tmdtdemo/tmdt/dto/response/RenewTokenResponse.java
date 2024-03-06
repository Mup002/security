package tmdtdemo.tmdt.dto.response;

import lombok.Data;

@Data
public class RenewTokenResponse {
    private String accessToken;
    private String accessExpiration;
    private String refreshToken;
    private String refreshExpiration;
}
