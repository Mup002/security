package tmdtdemo.tmdt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tmdtdemo.tmdt.dto.response.RefreshTokenResponse;
import tmdtdemo.tmdt.entity.RefreshToken;
import tmdtdemo.tmdt.exception.ResourceNotFoundException;
import tmdtdemo.tmdt.exception.TokenExpirationException;
import tmdtdemo.tmdt.repository.RefreshTokenRepository;
import tmdtdemo.tmdt.service.RefreshTokenService;
import tmdtdemo.tmdt.utils.mapper;

@Slf4j
@RequiredArgsConstructor
@Service
public class RefreshTokenServiceImpl implements RefreshTokenService {
    private final RefreshTokenRepository tokenRepository;
    @Override
    public boolean checkTokenNoExpireByUser(Long userId) {
        RefreshToken rf = tokenRepository.findRefreshTokenByUserId(userId);
        if(!ObjectUtils.isEmpty(rf)) {
            if (rf.isStatus() == true) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String disableToken(Long userId) {
        RefreshToken rf = tokenRepository.findRefreshTokenByUserId(userId);
        rf.setStatus(false);
        return "token has disabled";
    }

    @Override
    public String removeToken(Long userId) {
        RefreshToken rf = tokenRepository.findRefreshTokenByUserId(userId);
        if(ObjectUtils.isEmpty(rf)){
            throw new ResourceNotFoundException("RefreshToken dont exists");
        }
        tokenRepository.delete(rf);
        return "token has deleted";
    }

    @Override
    public RefreshTokenResponse getRefreshTokenByUser(Long userId) {
        RefreshToken rf = tokenRepository.findRefreshTokenByUserId(userId);
        if(ObjectUtils.isEmpty(rf)){
            throw new ResourceNotFoundException("RefreshToken dont exists");
        }
        return mapper.refreshTokenToResponse(rf);
    }

    @Override
    public String savedRefreshToken(RefreshToken refreshToken) {
        RefreshToken rf = new RefreshToken();
        rf.setRefreshToken(refreshToken.getRefreshToken());
        rf.setRefreshExpiration(refreshToken.getRefreshExpiration());
        rf.setStatus(refreshToken.isStatus());
        rf.setUser(refreshToken.getUser());
        tokenRepository.save(rf);
        return "created!";
    }

    @Override
    public RefreshToken getRefreshToken(String refreshToken) {
        RefreshToken rf = tokenRepository.findRefreshTokenByRefreshToken(refreshToken);
        if(ObjectUtils.isEmpty(rf)){
            throw new ResourceNotFoundException("RefreshToken dont exists");
        }
        if(rf.isStatus() == false){
            throw new TokenExpirationException("Refresh Token has expired");
        }
        return rf;
    }
}
