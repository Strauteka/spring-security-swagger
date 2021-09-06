package edu.strauteka.example.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import edu.strauteka.example.security.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/token")
public class TokenController {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsService userDetailsService;

    @GetMapping
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) {
       jwtTokenUtils.createUserDetailsFromRequestTokenPrepareResponse(request, response, user -> {
           final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
           String accessToken = jwtTokenUtils.createAccessToken((User) userDetails, request);
           String refreshToken = jwtTokenUtils.createRefreshToken((User) userDetails, request);
           Map<String, String> tokens = new HashMap<>();
           tokens.put(JwtTokenUtils.ACCESS_TOKEN_KEY, accessToken);
           tokens.put(JwtTokenUtils.REFRESH_TOKEN_KEY, refreshToken);
           response.setContentType(MediaType.APPLICATION_JSON_VALUE);
           try {
               new ObjectMapper().writeValue(response.getOutputStream(), tokens);
           } catch (IOException e) {
                log.error("Write Access token Error", e);
           }
       });
    }

}
