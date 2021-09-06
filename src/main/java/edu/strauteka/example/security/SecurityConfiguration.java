package edu.strauteka.example.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;

@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    public static final String APP_LOGIN = "/api/login";
    public static final String APP_TOKEN = "/api/token";
    public static final String APP_SWAGGER = "/swagger-ui";
    public static final String APP_SWAGGER_RESOURCES = "/swagger-resources";
    public static final String APP_API_DOCS = "/v3/api-docs";


    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomAuthorizationFilter customAuthorizationFilter;

    private final JwtTokenUtils jwtTokenUtils;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    public static String[] unconditionalAllow() {
        return new String[]{
                APP_LOGIN + "/**",
                APP_TOKEN + "/**",
                APP_SWAGGER + "/**",
                APP_SWAGGER_RESOURCES + "/**",
                APP_API_DOCS + "/**",
                "/favicon.ico"};
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers(unconditionalAllow()).permitAll();
        http.authorizeRequests().antMatchers(GET, "/api/user/**").hasAuthority("ROLE_USER");
        http.authorizeRequests().antMatchers(POST, "/api/user/save/**")
                .hasAnyAuthority("ROLE_ADMIN", "ROLE_SUPER_ADMIN");
        http.authorizeRequests().anyRequest().authenticated().and().httpBasic();
        http.addFilter(createCustomFilter(jwtTokenUtils));
        http.addFilterBefore(customAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    public CustomAuthenticationFilter createCustomFilter(JwtTokenUtils jwtTokenUtils) throws Exception {
        final CustomAuthenticationFilter filter = new CustomAuthenticationFilter(
                authenticationManagerBean(),
                jwtTokenUtils);
        filter.setFilterProcessesUrl(APP_LOGIN);
        return filter;
    }
}
