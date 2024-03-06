package tmdtdemo.tmdt.config.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.io.IOException;

@Configuration
public class CorsFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        final HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
        httpServletResponse.setHeader("Access-Control-Allow-Origin","*");
        httpServletResponse.setHeader("Access-Control-Allow-Methods","*");
        httpServletResponse.setHeader("Access-Control-Allow-Headers","Authorization , Content-Type");
        httpServletResponse.setHeader("Access-Control-Max-Age", "3600");
        if(HttpMethod.OPTIONS.name().equalsIgnoreCase(((HttpServletRequest) servletRequest).getMethod())){
            httpServletResponse.setStatus(HttpServletResponse.SC_OK);

        }
        filterChain.doFilter(servletRequest,servletResponse);
    }
}
