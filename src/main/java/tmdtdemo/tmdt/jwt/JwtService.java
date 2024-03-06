package tmdtdemo.tmdt.jwt;

import io.jsonwebtoken.Claims;
import tmdtdemo.tmdt.auth.UserDetailsCustom;
import tmdtdemo.tmdt.dto.request.RenewTokenRequest;
import tmdtdemo.tmdt.dto.response.RefreshTokenResponse;
import tmdtdemo.tmdt.dto.response.RenewTokenResponse;

import java.security.Key;

public interface JwtService {
    Claims extractClaims(String token, String key);
    Key accessKey(String keyString);
    Key refreshKey(String keyString);
    String generateAccessToken(UserDetailsCustom userDetailsCustom, String key);
    RefreshTokenResponse generateRefreshToken(UserDetailsCustom userDetailsCustom);
    boolean invalidToken(String token, String key);
    String keyString();
    RenewTokenResponse renewAccessToken(RenewTokenRequest request);
}
