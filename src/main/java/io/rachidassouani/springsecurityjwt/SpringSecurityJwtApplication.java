package io.rachidassouani.springsecurityjwt;

import io.rachidassouani.springsecurityjwt.model.AppRole;
import io.rachidassouani.springsecurityjwt.model.AppUser;
import io.rachidassouani.springsecurityjwt.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.HashSet;

@SpringBootApplication
public class SpringSecurityJwtApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringSecurityJwtApplication.class, args);
	}

	@Bean
	CommandLineRunner run(UserService userService) {
		return args -> {
			userService.saveRole(new AppRole(null, "ROLE_USER"));
			userService.saveRole(new AppRole(null, "ROLE_ADMIN"));
			userService.saveRole(new AppRole(null, "ROLE_SUPER_ADMIN"));
			userService.saveRole(new AppRole(null, "ROLE_MANAGER"));


			userService.saveUser(new AppUser(null, "rachid", "rachid", new HashSet<>()));
			userService.saveUser(new AppUser(null, "hanan", "hanan", new HashSet<>()));
			userService.saveUser(new AppUser(null, "hasnae", "hasnae", new HashSet<>()));
			userService.saveUser(new AppUser(null, "admin", "admin", new HashSet<>()));


			userService.addRoleToUser("admin", "ROLE_ADMIN");
			userService.addRoleToUser("rachid", "ROLE_USER");
			userService.addRoleToUser("hanan", "ROLE_ADMIN");
		};
	}
}
