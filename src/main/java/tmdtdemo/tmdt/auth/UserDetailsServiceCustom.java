package tmdtdemo.tmdt.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.ObjectUtils;
import tmdtdemo.tmdt.entity.User;
import tmdtdemo.tmdt.exception.BaseException;
import tmdtdemo.tmdt.repository.UserRepository;

import java.util.stream.Collectors;

@RequiredArgsConstructor
public class UserDetailsServiceCustom implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserDetailsCustom userDetailsCustom = getUserDetails(username);
        if(ObjectUtils.isEmpty(userDetailsCustom)){
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()),"Username or password invalid");
        }
        return userDetailsCustom;
    }
    private UserDetailsCustom getUserDetails(String username){
        User user = userRepository.findUserByUsername(username);
        if(ObjectUtils.isEmpty(user)){
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "User not found");
        }
        return new UserDetailsCustom(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream().map(r -> new SimpleGrantedAuthority(r.getName())).collect(Collectors.toList())
        );
    }
}
