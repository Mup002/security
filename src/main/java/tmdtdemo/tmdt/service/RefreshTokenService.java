package tmdtdemo.tmdt.service;

import org.springframework.stereotype.Service;
import tmdtdemo.tmdt.dto.response.RefreshTokenResponse;
import tmdtdemo.tmdt.entity.RefreshToken;

@Service
public interface RefreshTokenService {
    boolean checkTokenNoExpireByUser(Long userId);
    String disableToken(Long userId);
    String removeToken(Long userId);
    RefreshTokenResponse getRefreshTokenByUser(Long userId);
    String savedRefreshToken(RefreshToken refreshToken);
    RefreshToken getRefreshToken(String refreshToken);
}
