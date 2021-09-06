package edu.strauteka.example.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

import static edu.strauteka.example.security.SecurityConfiguration.unconditionalAllow;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class CustomAuthorizationFilter extends OncePerRequestFilter {

    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest req,
                                    @NonNull HttpServletResponse resp,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {

        if (Arrays
                .stream(unconditionalAllow())
                .map(val -> val.replace("/**", ""))
                .noneMatch(val -> req.getServletPath().startsWith(val))
        ) {
            jwtTokenUtils
                    .createUserDetailsFromRequestTokenPrepareResponse(req, resp, user -> {
                        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
                                user.getUsername(),
                                user.getPassword(),
                                user.getAuthorities()));
                        try {
                            filterChain.doFilter(req, resp);
                        } catch (IOException | ServletException e) {
                            e.printStackTrace();
                        }
                    });
        } else {
            filterChain.doFilter(req, resp);
        }
    }
}
