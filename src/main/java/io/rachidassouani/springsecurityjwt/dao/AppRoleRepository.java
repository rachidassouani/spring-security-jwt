package io.rachidassouani.springsecurityjwt.dao;

import io.rachidassouani.springsecurityjwt.model.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
    AppRole findByName(String name);
}
