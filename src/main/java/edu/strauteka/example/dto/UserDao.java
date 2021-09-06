package edu.strauteka.example.dto;

import edu.strauteka.example.domain.Role;
import edu.strauteka.example.domain.User;
import lombok.Data;

import java.util.List;

@Data
public class UserDao {
    private String name;
    private String username;
    private String password;

    //TODO: mapstruct
    public User toUser(List<Role> roles) {
        return new User(null, name, username, password, roles);
    }
}
