package tmdtdemo.tmdt.config.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import tmdtdemo.tmdt.jwt.JwtConfig;
import tmdtdemo.tmdt.jwt.JwtService;
import tmdtdemo.tmdt.service.BaseRedisService;
import tmdtdemo.tmdt.utils.BaseResponse;
import tmdtdemo.tmdt.utils.HelperUtils;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class JwtTokenAuthenticationFilter extends OncePerRequestFilter {
    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final BaseRedisService redisService;
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String accessToken = request.getHeader(jwtConfig.getHeader());
        String userKey = request.getHeader("x-api-key");
// check loi soon....
        // tao rediskey
        String rediskey = HelperUtils.StringBuilderRedisKey(userKey);

        log.info("Rediskey: {}", rediskey);
        log.info("Token: {} ", accessToken);

        String redisValue = null;
        if(userKey!=null){
            redisValue = redisService.get(rediskey).toString();
        }

        log.info("Start do filter one per request: {}",request.getRequestURI());
        if(!ObjectUtils.isEmpty(accessToken)
                && accessToken.startsWith(jwtConfig.getPrefix() + " ")){
            accessToken = accessToken.substring((jwtConfig.getPrefix() + " ").length());
            try{
                if(jwtService.invalidToken(accessToken,redisValue)){
                    Claims claims = jwtService.extractClaims(accessToken,redisValue);
                    String username = claims.getSubject();
                    List<String> authorities = claims.get("authorities", List.class);

                    if(!ObjectUtils.isEmpty(username)){
                        UsernamePasswordAuthenticationToken auth =
                                new UsernamePasswordAuthenticationToken(username, null, authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList()));

                        SecurityContextHolder.getContext().setAuthentication(auth);
                    }
                }
            }catch (Exception e){
                log.error("Error on filter once per request, path {}", request.getRequestURI());
                BaseResponse responseDTO = new BaseResponse();
                responseDTO.setCode(String.valueOf(HttpStatus.UNAUTHORIZED.value()));
                responseDTO.setMessage(e.getLocalizedMessage());

                String json = new ObjectMapper().writer().withDefaultPrettyPrinter().writeValueAsString(responseDTO);
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json; charset=UTF-8");
                response.getWriter().write(json);
                return;
            }
        }
        log.info("end do filter{} ", request.getRequestURI());
        filterChain.doFilter(request,response);
    }
}
