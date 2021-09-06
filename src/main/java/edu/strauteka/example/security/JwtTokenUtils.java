package edu.strauteka.example.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import edu.strauteka.example.dto.UserDetailsDao;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtTokenUtils {
    public static final String ACCESS_TOKEN_KEY = "access_token";
    public static final String REFRESH_TOKEN_KEY = "refresh_token";
    public static final String ERROR_HEADER = "Warning";
    public static final String ERROR_BODY = "ERROR";
    public static final String ERROR_MESSAGE = "Missing or invalid token";
    //TODO: to config.
    public static final Integer REFRESH_EXPIRATION_TIME = 5 * 60 * 1000;
    public static final Integer ACCESS_EXPIRATION_TIME = 30 * 60 * 1000;


    private final Algorithm passwordEncoderAlgorithm;

    public String createAccessToken(User user, HttpServletRequest request) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .withIssuer(request.getRequestURL().toString())
                .withClaim("roles", user
                        .getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .collect(Collectors.toList()))
                .sign(passwordEncoderAlgorithm);
    }

    public String createRefreshToken(User user, HttpServletRequest request) {
        return JWT.create()
                .withSubject(user.getUsername())
                .withExpiresAt(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME))
                .withIssuer(request.getRequestURL().toString())
                .sign(passwordEncoderAlgorithm);
    }

    public static UserDetailsDao createUserDetails(String username,
                                                   String password,
                                                   Collection<SimpleGrantedAuthority> authorities) {
        return new UserDetailsDao(
                username,
                password,
                authorities);
    }

    public UserDetailsDao createUserDetailsFromRequestToken(HttpServletRequest req) {
        final String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring("Bearer ".length());
            JWTVerifier jwtVerifier = JWT.require(passwordEncoderAlgorithm).build();
            DecodedJWT decodedJWT = jwtVerifier.verify(token);
            final String username = decodedJWT.getSubject();
            String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
            final List<SimpleGrantedAuthority> simpleGrantedAuthorities =
                    Optional.ofNullable(roles)
                            .stream()
                            .filter(Objects::nonNull)
                            .flatMap(Arrays::stream)
                            .map(SimpleGrantedAuthority::new)
                            .collect(Collectors.toList());
            return new UserDetailsDao(username, null, simpleGrantedAuthorities);
        }
        return null;
    }

    @SneakyThrows
    public void createUserDetailsFromRequestTokenPrepareResponse(
            HttpServletRequest req,
            HttpServletResponse resp,
            Consumer<UserDetailsDao> onSuccess) {
        try {
            final UserDetailsDao userDetailsFromRequestToken = createUserDetailsFromRequestToken(req);
            if (Objects.nonNull(userDetailsFromRequestToken)) {
                onSuccess.accept(userDetailsFromRequestToken);
            } else {
                log.error("Error while validating token: {} ", ERROR_MESSAGE);
                resp.setHeader(ERROR_HEADER, ERROR_MESSAGE);
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                Map<String, String> error = new HashMap<>();
                error.put(ERROR_BODY, ERROR_MESSAGE);
                resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(resp.getOutputStream(), error);
            }
        } catch (Exception e) {
            log.error("Error while validating token", e);
            resp.setHeader(ERROR_HEADER, e.getMessage());
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            Map<String, String> error = new HashMap<>();
            error.put(ERROR_BODY, e.getMessage());
            resp.setContentType(MediaType.APPLICATION_JSON_VALUE);
            new ObjectMapper().writeValue(resp.getOutputStream(), error);
        }
    }
}
