package tmdtdemo.tmdt.jwt.impl;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import tmdtdemo.tmdt.auth.UserDetailsCustom;
import tmdtdemo.tmdt.config.filter.CustomAuthenticationProvider;
import tmdtdemo.tmdt.dto.request.RenewTokenRequest;
import tmdtdemo.tmdt.dto.response.RefreshTokenResponse;
import tmdtdemo.tmdt.dto.response.RenewTokenResponse;
import tmdtdemo.tmdt.entity.RefreshToken;
import tmdtdemo.tmdt.entity.User;
import tmdtdemo.tmdt.exception.BaseException;
import tmdtdemo.tmdt.exception.ResourceNotFoundException;
import tmdtdemo.tmdt.jwt.JwtConfig;
import tmdtdemo.tmdt.jwt.JwtService;
import tmdtdemo.tmdt.repository.UserRepository;
import tmdtdemo.tmdt.service.BaseRedisService;
import tmdtdemo.tmdt.service.RefreshTokenService;
import tmdtdemo.tmdt.service.UserService;
import tmdtdemo.tmdt.utils.DateFormat;
import tmdtdemo.tmdt.utils.HelperUtils;
import tmdtdemo.tmdt.utils.mapper;

import java.security.Key;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JwtServiceImpl  implements JwtService {
    private final JwtConfig jwtConfig;
    private final UserDetailsService userDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final BaseRedisService baseRedisService;

    @Override
    public String keyString() {
        Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String encodedKey = Encoders.BASE64URL.encode(key.getEncoded());
        return encodedKey;
    }


    @Override
    public Key accessKey(String keyString) {
        keyString = keyString.replace('-', '+').replace('_', '/');
        byte[] key = Decoders.BASE64.decode(keyString);
        return Keys.hmacShaKeyFor(key);
    }

    @Override
    public Key refreshKey(String keyString) {
        keyString = keyString.replace('-', '+').replace('_', '/');
        byte[] key = Decoders.BASE64.decode(keyString);
        return Keys.hmacShaKeyFor(key);
    }
    @Override
    public String generateAccessToken(UserDetailsCustom userDetailsCustom, String key) {
        Instant now = Instant.now();

        List<String> roles = new ArrayList<>();
        userDetailsCustom.getAuthorities().forEach(role -> roles.add(role.getAuthority()));
        log.info("Roles: {}",roles);
        return Jwts.builder()
                .setSubject(userDetailsCustom.getUsername())
                .claim("authorities",userDetailsCustom.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
                .claim("roles",roles)
                .claim("isEnable",userDetailsCustom.isEnabled())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(jwtConfig.getAccessExpiration())))
                .signWith(accessKey(key),SignatureAlgorithm.HS256)
                .compact();
    }

    @Override
    public RefreshTokenResponse generateRefreshToken(UserDetailsCustom userDetailsCustom) {
        // lay thong tin user qua userdetailcustom
        User user = userService.findUserByUsername(userDetailsCustom.getUsername());
        // check user co token nao con hạn ko?
        if(refreshTokenService.checkTokenNoExpireByUser(user.getId()) ==  true){
            // trả về refreshToken cũ
            log.info("refresh token of userid: {}",user.getId() + " still valid");
            return refreshTokenService.getRefreshTokenByUser(user.getId());
        }

        // ko có refreshToken nào còn hạn thì tạo 1 rf mới
        Instant now = Instant.now();
        String newToken = Jwts.builder()
                .setSubject(userDetailsCustom.getUsername())
                .setIssuedAt(Date.from(now.plusSeconds(jwtConfig.getRefreshExpiration())))
                .signWith(refreshKey(keyString()),SignatureAlgorithm.HS256)
                .compact();
        RefreshToken rf = new RefreshToken();
        rf.setRefreshToken(newToken);
        rf.setStatus(true);
        rf.setRefreshExpiration(DateFormat.DateFormatWithLocate(String.valueOf(Date.from(now.plusSeconds(jwtConfig.getRefreshExpiration())))));
        rf.setUser(userService.findUserByUsername(userDetailsCustom.getUsername()));
        refreshTokenService.savedRefreshToken(rf);
        log.info("created new refreshToken done");
        return mapper.refreshTokenToResponse(rf);
    }

    @Override
    public Claims extractClaims(String token, String key) {
        return Jwts.parserBuilder()
                .setSigningKey(accessKey(key))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    private <T> T extractClaims(String token, String key, Function<Claims, T> claimsTFunction){
        final Claims claims = extracAllClaims(token, key);
        return claimsTFunction.apply(claims);
    }
    private Claims extracAllClaims(String token, String key){
        Claims claims = null;
        try{
            claims = Jwts.parserBuilder()
                    .setSigningKey(accessKey(key))
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        }catch (ExpiredJwtException e){
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),"Token expiration");
        }catch (UnsupportedJwtException e){
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Token is not support");
        }catch (MalformedJwtException e){
            throw  new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "invalid format 3 part of token");
        }catch (SignatureException e){
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "Invalid token");
        }catch (Exception e){
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), e.getLocalizedMessage());
        }
        return claims;
    }
    private String extractUsername(String token, String key){
        return extractClaims(token,key,Claims::getSubject);
    }
    private Date extractExpiration(String token, String key){
        return extractClaims(token,key, Claims::getExpiration);
    }

    @Override
    public boolean invalidToken(String token, String key) {
        final String username = extractUsername(token,key);
        log.info("key1:{}",key);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        return !ObjectUtils.isEmpty(userDetails);
    }
    @Override
    public RenewTokenResponse renewAccessToken(RenewTokenRequest request) {
        RefreshToken rf = refreshTokenService.getRefreshToken(request.getRefreshToken());
        User user = userService.findUserByUsername(rf.getUser().getUsername());
        String redisKey = HelperUtils.StringBuilderRedisKey(user.getUsername());
        if(baseRedisService.get(redisKey) != null){
            // xóa accessToken cũ nếu còn tồn tại
            baseRedisService.delete(redisKey);

        }else{
            throw new ResourceNotFoundException("user not found");
        }
        // tạo accessToken mới
        UserDetailsCustom userDetailsCustom = (UserDetailsCustom) userDetailsService.loadUserByUsername(user.getUsername());
        String accessKeyString = keyString();
        String newAccessToken = generateAccessToken(userDetailsCustom,accessKeyString);
        baseRedisService.set(redisKey,newAccessToken);

        // tra ve
        RenewTokenResponse response = new RenewTokenResponse();
        response.setAccessToken(newAccessToken);
        response.setAccessExpiration(DateFormat.DateFormatWithLocate(extractExpiration(newAccessToken,accessKeyString).toString()));
        response.setRefreshToken(rf.getRefreshToken());
        response.setRefreshExpiration(rf.getRefreshExpiration());

        return response;
    }

}
