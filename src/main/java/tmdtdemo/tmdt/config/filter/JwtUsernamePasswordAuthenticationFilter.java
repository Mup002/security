package tmdtdemo.tmdt.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import tmdtdemo.tmdt.auth.UserDetailsCustom;
import tmdtdemo.tmdt.auth.dto.LoginRequest;
import tmdtdemo.tmdt.auth.dto.LoginResponse;
import tmdtdemo.tmdt.dto.response.RefreshTokenResponse;
import tmdtdemo.tmdt.jwt.JwtConfig;
import tmdtdemo.tmdt.jwt.JwtService;
import tmdtdemo.tmdt.service.BaseRedisService;
import tmdtdemo.tmdt.utils.DateFormat;
import tmdtdemo.tmdt.utils.HelperUtils;

import java.io.IOException;
import java.util.Collections;


@Slf4j

public class JwtUsernamePasswordAuthenticationFilter  extends AbstractAuthenticationProcessingFilter {
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;
    private final BaseRedisService baseRedisService;
    public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
    JwtConfig jwtConfig,
    JwtService jwtService,
                                                   BaseRedisService baseRedisService){
        super(new AntPathRequestMatcher(jwtConfig.getUrl(),"POST"));
        this.baseRedisService = baseRedisService;
        setAuthenticationManager(authenticationManager);
        this.objectMapper = new ObjectMapper();
        this.jwtService = jwtService;
    }
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.info("Attempt to authen");
        LoginRequest loginRequest = objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        log.info("end attempt to authen");
        return getAuthenticationManager()
                .authenticate(new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword(),
                        Collections.emptyList()
                ));
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        UserDetailsCustom userDetailsCustom = (UserDetailsCustom) authResult.getPrincipal();
        log.info(userDetailsCustom.getUsername());

        // tạo redis_key = "accesKey_+ username
        String redisKey = HelperUtils.StringBuilderRedisKey(userDetailsCustom.getUsername());
        String keyString = jwtService.keyString();
        log.info("KeyString: {}",keyString);
        // set cặp key-value
        baseRedisService.set(redisKey, keyString);
        baseRedisService.setTimeToLive(redisKey,5);

        // lấy ra accessToken và thời hạn tồn tại
        String accessToken = jwtService.generateAccessToken(userDetailsCustom,keyString);
        Claims claims = jwtService.extractClaims(accessToken,keyString);
        String expiration = String.valueOf(claims.getExpiration());

        RefreshTokenResponse rfResponse = jwtService.generateRefreshToken(userDetailsCustom);
        LoginResponse access = new LoginResponse();
        access.setAccessToken(accessToken);
        access.setAccessExpiration(DateFormat.DateFormatWithLocate(expiration));
        access.setRefreshToken(rfResponse.getToken());
        access.setRefreshExpiration(rfResponse.getExpiration());

        String json = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(access);
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(json);
        log.info("end authen: {}",accessToken);

    }

    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        super.unsuccessfulAuthentication(request, response, failed);
    }

}
