package io.rachidassouani.springsecurityjwt.service;

import io.rachidassouani.springsecurityjwt.model.AppRole;
import io.rachidassouani.springsecurityjwt.model.AppUser;

import java.util.List;

public interface UserService {
    AppUser saveUser(AppUser appUser);
    AppRole saveRole(AppRole appRole);
    void addRoleToUser(String username, String roleName);
    AppUser findUserByUsername(String username);
    // in real world application, you will not need to load all users that you have in the database
    List<AppUser> findAllUsers();
}
