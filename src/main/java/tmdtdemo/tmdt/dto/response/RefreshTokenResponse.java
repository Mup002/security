package tmdtdemo.tmdt.dto.response;

import lombok.Data;

@Data
public class RefreshTokenResponse {
    private String token;
    private String expiration;
}
