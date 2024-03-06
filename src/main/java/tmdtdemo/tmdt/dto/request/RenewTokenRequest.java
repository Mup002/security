package tmdtdemo.tmdt.dto.request;

import lombok.Data;

@Data
public class RenewTokenRequest {
    private String refreshToken;
}
