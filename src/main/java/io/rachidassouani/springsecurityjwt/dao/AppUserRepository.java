package io.rachidassouani.springsecurityjwt.dao;

import io.rachidassouani.springsecurityjwt.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppUserRepository extends JpaRepository<AppUser, Long> {
    AppUser findByUsername(String username);
}
