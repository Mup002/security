package tmdtdemo.tmdt.utils;

import tmdtdemo.tmdt.dto.response.RefreshTokenResponse;
import tmdtdemo.tmdt.entity.RefreshToken;

public class mapper {
    public static RefreshTokenResponse refreshTokenToResponse(RefreshToken rf){
        RefreshTokenResponse response = new RefreshTokenResponse();
        response.setToken(rf.getRefreshToken());
        response.setExpiration(rf.getRefreshExpiration());
        return response;
    }
}
