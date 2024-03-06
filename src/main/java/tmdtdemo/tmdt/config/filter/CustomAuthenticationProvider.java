package tmdtdemo.tmdt.config.filter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;
import tmdtdemo.tmdt.entity.Role;
import tmdtdemo.tmdt.entity.User;
import tmdtdemo.tmdt.exception.BaseException;
import tmdtdemo.tmdt.repository.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {
    private final UserRepository userRepository;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        log.info("Start authentication");
        final String username = authentication.getName();
        final String password = authentication.getCredentials().toString();
        User user ;
        try{
            user = userRepository.findUserByUsername(username);
        }catch (Exception e){
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()),"User not found");
        }
        final List<GrantedAuthority> authorities = getAuthorities(user.getRoles().stream().toList());
        final Authentication auth = new UsernamePasswordAuthenticationToken(username,password,authorities);
        return auth;
    }

    private List<GrantedAuthority> getAuthorities(List<Role> roles){
        List<GrantedAuthority> result = new ArrayList<>();
        Set<String> permissions = new HashSet<>();
        if(!ObjectUtils.isEmpty(roles)){
            roles.forEach(r-> {
                permissions.add(r.getName());
            });
        }
        permissions.forEach(p -> {
            result.add(new SimpleGrantedAuthority(p));
        });
        return result;
    }
    @Override
    public boolean supports(Class<?> authentication) {
        return false;
    }
}
