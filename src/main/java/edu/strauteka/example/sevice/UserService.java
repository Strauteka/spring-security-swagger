package edu.strauteka.example.sevice;

import edu.strauteka.example.domain.Role;
import edu.strauteka.example.domain.User;

import java.util.List;

public interface UserService {
    User saveUser(User user);
    Role saveRole(Role role);
    List<Role> defaultRoles();
    void addRoleToUser(String username, String roleName);
    List<User> getUsers();

}
