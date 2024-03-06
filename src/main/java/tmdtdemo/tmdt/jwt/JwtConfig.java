package tmdtdemo.tmdt.jwt;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;


@Data
public class JwtConfig {
    @Value("${jwt.url:/api/v1/account/login}")
    private String url;
    @Value("${jwt.header:Authorization}")
    private String header;
    @Value("${jwt.prefix:Bearer}")
    private String prefix;
    @Value("${jwt.accessExpiration:#{5*60*60}}")
    private int accessExpiration;
    @Value("${jwt.refreshExpiration:#{60*60*24}}")
    private int refreshExpiration;
    @Value("${jwt.secret:7dEd4w4xXq071fEbNL6TjUPqz3qjtr1GbYHfZ177tBRMIoB7fG3m2c7a5eYfOqyX}")
    private String secret;
}
