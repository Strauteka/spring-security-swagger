package edu.strauteka.example.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

/**
 * UserDetails: GrantedAuthority can't be empty or null, refresh token does not hold GrantedAuthority's.
 */
@Data
@AllArgsConstructor
public class UserDetailsDao {
    private String username;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public User toUser() {
        return new User(username, password, authorities);
    }
}
