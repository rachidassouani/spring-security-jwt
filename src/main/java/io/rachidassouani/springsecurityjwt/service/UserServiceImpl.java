package io.rachidassouani.springsecurityjwt.service;

import io.rachidassouani.springsecurityjwt.model.AppRole;
import io.rachidassouani.springsecurityjwt.model.AppUser;
import io.rachidassouani.springsecurityjwt.dao.AppRoleRepository;
import io.rachidassouani.springsecurityjwt.dao.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    private final AppUserRepository appUserRepository;
    private final AppRoleRepository appRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public AppUser saveUser(AppUser appUser) {
        log.info("Saving new user {} to the database", appUser.getUsername());
        appUser.setPassword(passwordEncoder.encode(appUser.getPassword()));
        return appUserRepository.save(appUser);
    }

    @Override
    public AppRole saveRole(AppRole appRole) {
        log.info("Saving new role {} to the database", appRole.getName());
        return appRoleRepository.save(appRole);
    }

    @Override
    public void addRoleToUser(String username, String roleName) {
        AppUser appUser = appUserRepository.findByUsername(username);
        AppRole appRole = appRoleRepository.findByName(roleName);
        appUser.getRoles().add(appRole);
    }

    @Override
    public AppUser findUserByUsername(String username) {
        return appUserRepository.findByUsername(username);
    }

    @Override
    public List<AppUser> findAllUsers() {
        return appUserRepository.findAll();
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findByUsername(username);
        if (appUser == null) {
            log.error("user not found in the database");
            throw new UsernameNotFoundException("user not found in the database");
        }

        log.info("user founded");
        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        appUser.getRoles().forEach(appRole -> {
            authorities.add(new SimpleGrantedAuthority(appRole.getName()));
        });

        return new User(appUser.getUsername(), appUser.getPassword(), authorities);
    }

}
