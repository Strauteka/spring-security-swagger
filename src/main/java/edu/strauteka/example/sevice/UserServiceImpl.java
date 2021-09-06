package edu.strauteka.example.sevice;

import edu.strauteka.example.domain.Role;
import edu.strauteka.example.domain.User;
import edu.strauteka.example.repo.RoleRepo;
import edu.strauteka.example.repo.UserRepo;
import edu.strauteka.example.security.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final UserRepo userRepo;
    private final RoleRepo roleRepo;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final User byUsername = userRepo.findByUsername(username);
        Objects.requireNonNull(byUsername, "User not found in database! User:" + byUsername);
        log.info("User found: {}", username);
        final List<SimpleGrantedAuthority> authorities = byUsername
                .getRoles()
                .stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
        return JwtTokenUtils.createUserDetails(
                byUsername.getUsername(),
                byUsername.getPassword(),
                authorities).toUser();
    }

    @Override
    public User saveUser(User user) {
        log.info("saving User: {}", user);
        user.setPassword(this.passwordEncoder.encode(user.getPassword()));
        return this.userRepo.save(user);
    }

    @Override
    public Role saveRole(Role role) {
        log.info("Saving Role: {}", role);
        return roleRepo.save(role);
    }

    @Override
    public List<Role> defaultRoles() {
        return roleRepo.findAll().stream().filter(Role::getDefaultRole).collect(Collectors.toList());
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        log.info("Add role: {} to user: {}", roleName, username);
        User userByName = userRepo.findByUsername(username);
        final Role repoByName = roleRepo.findByName(roleName);
        userByName.getRoles().add(repoByName);
    }

    @Override
    public List<User> getUsers() {
        log.info("Fetch all users!");
        return userRepo.findAll();
    }
}
