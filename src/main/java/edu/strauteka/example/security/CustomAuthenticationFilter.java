package edu.strauteka.example.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (Objects.isNull(username) && Objects.isNull(password)) {
            final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
            if (authHeader != null && authHeader.startsWith("Basic ")) {
                final String userPassword = authHeader.substring("Basic ".length());
                final String userPasswordDecoded =
                        new String(Base64.getDecoder().decode(userPassword), StandardCharsets.UTF_8);
                final String[] values = userPasswordDecoded.split(":", 2);
                username = values[0];
                password = values[1];
            }
        }
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        return authenticationManager.authenticate(authentication);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain chain,
                                            Authentication authResult) throws IOException {
        User user = (User) authResult.getPrincipal();
        String accessToken = jwtTokenUtils.createAccessToken(user, request);
        String refreshToken = jwtTokenUtils.createRefreshToken(user, request);
        Map<String, String> tokens = new HashMap<>();
        response.setHeader(JwtTokenUtils.ACCESS_TOKEN_KEY, accessToken);
        response.setHeader(JwtTokenUtils.REFRESH_TOKEN_KEY, refreshToken);
        tokens.put(JwtTokenUtils.ACCESS_TOKEN_KEY, accessToken);
        tokens.put(JwtTokenUtils.REFRESH_TOKEN_KEY, refreshToken);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        new ObjectMapper().writeValue(response.getOutputStream(), tokens);
    }
}
