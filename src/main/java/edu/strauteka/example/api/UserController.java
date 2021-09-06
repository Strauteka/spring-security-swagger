package edu.strauteka.example.api;

import edu.strauteka.example.domain.Role;
import edu.strauteka.example.domain.User;
import edu.strauteka.example.dto.UserDao;
import edu.strauteka.example.sevice.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {
    private final UserService userService;

    @GetMapping("/users")
    public ResponseEntity<List<User>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @PostMapping("/user/save")
    public ResponseEntity<User> saveUser(@RequestBody UserDao user) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/user/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(user.toUser(userService.defaultRoles())));
    }

    @PostMapping("role/save")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("api/role/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }

    @PostMapping("role/addtouser")
    public ResponseEntity<?> roleToUser(@RequestBody RoleToUserForm role) {
        userService.addRoleToUser(role.getUsername(), role.getUsername());
        return ResponseEntity.ok().build();
    }

    @Data
    static class RoleToUserForm {
        private String username;
        private String roleName;
    }
}

