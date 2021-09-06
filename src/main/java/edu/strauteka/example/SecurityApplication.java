package edu.strauteka.example;

import edu.strauteka.example.domain.Role;
import edu.strauteka.example.domain.User;
import edu.strauteka.example.sevice.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;

@SpringBootApplication
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UserService userService) {
		return args -> {
			userService.saveRole(new Role(null, "ROLE_USER", true));
			userService.saveRole(new Role(null, "ROLE_MANAGER", false));
			userService.saveRole(new Role(null, "ROLE_ADMIN", false));
			userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN", false));

			userService.saveUser(new User(null, "John", "john", "1234", new ArrayList<>()));
			userService.saveUser(new User(null, "Linda", "linda", "2345", new ArrayList<>()));
			userService.saveUser(new User(null, "Baraka", "baraka", "3456", new ArrayList<>()));

			userService.addRoleToUser("john", "ROLE_MANAGER");
			userService.addRoleToUser("john", "ROLE_SUPER_ADMIN");
			userService.addRoleToUser("linda", "ROLE_USER");
			userService.addRoleToUser("baraka", "ROLE_ADMIN");
		};
	}

}
