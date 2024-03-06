package tmdtdemo.tmdt.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tmdtdemo.tmdt.auth.UserDetailsServiceCustom;
import tmdtdemo.tmdt.config.filter.CustomAuthenticationProvider;
import tmdtdemo.tmdt.config.filter.JwtTokenAuthenticationFilter;
import tmdtdemo.tmdt.config.filter.JwtUsernamePasswordAuthenticationFilter;
import tmdtdemo.tmdt.exception.CustomAccessDeniedHandler;
import tmdtdemo.tmdt.jwt.JwtConfig;
import tmdtdemo.tmdt.jwt.JwtService;
import tmdtdemo.tmdt.service.BaseRedisService;
import tmdtdemo.tmdt.service.EmailSenderService;

@Configuration
@EnableWebSecurity
public class AppConfig {
    @Autowired
    private CustomAuthenticationProvider customAuthenticationProvider;
    @Autowired
    JwtConfig jwtConfig;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private BaseRedisService baseRedisService;

    @Bean
    public JwtConfig jwtConfig(){
        return new JwtConfig();
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService(){
        return new UserDetailsServiceCustom();
    }
    @Autowired
    public void configGlobal(final AuthenticationManagerBuilder auth){
        auth.authenticationProvider(customAuthenticationProvider);
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);
        builder.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
        AuthenticationManager manager  = builder.build();
        http
                .cors().disable()
                .csrf().disable()
                .formLogin().disable()
                .authorizeHttpRequests()
                .requestMatchers("/api/v1/account/**").permitAll()
                .requestMatchers("/test/redis/**").permitAll()
                .requestMatchers("/test/token/**").permitAll()
                .requestMatchers("/api/v1/user/**").hasAnyAuthority("USER")
                .requestMatchers("/api/v1/admin/**").hasAnyAuthority("ADMIN")
                .anyRequest().authenticated()
                .and()
                .authenticationManager(manager)
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint(
                        (((request, response, authException) -> response.sendError(HttpServletResponse.SC_UNAUTHORIZED)))
                )
                .accessDeniedHandler(new CustomAccessDeniedHandler())
                .and()
                .addFilterBefore(new JwtUsernamePasswordAuthenticationFilter(manager,jwtConfig,jwtService, baseRedisService), UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(new JwtTokenAuthenticationFilter(jwtConfig,jwtService,baseRedisService), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}
