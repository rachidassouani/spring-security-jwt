package io.rachidassouani.springsecurityjwt.controller;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.rachidassouani.springsecurityjwt.dto.RoleToUserDto;
import io.rachidassouani.springsecurityjwt.model.AppRole;
import io.rachidassouani.springsecurityjwt.model.AppUser;
import io.rachidassouani.springsecurityjwt.service.UserService;
import io.rachidassouani.springsecurityjwt.util.Constant;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    private final UserService userService;

    @GetMapping("users")
    public ResponseEntity<List<AppUser>> listUsers() {
        return ResponseEntity.ok().body(userService.findAllUsers());
    }

    @PostMapping("users")
    public ResponseEntity<AppUser> saveUser(@RequestBody AppUser appUser) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/users/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveUser(appUser));
    }

    @PostMapping("roles")
    public ResponseEntity<AppRole> saveRole(@RequestBody AppRole appRole) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/roles/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(appRole));
    }

    @PostMapping("roles/addToUser")
    public ResponseEntity<?> addRoleToUser(@RequestBody RoleToUserDto roleToUserDto) {
        userService.addRoleToUser(roleToUserDto.getUsername(), roleToUserDto.getRoleName());
        return ResponseEntity.ok().build();
    }

    @PostMapping("tokens/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {

            try {
                String refreshToken = authorizationHeader.substring(7);
                Algorithm algorithm = Algorithm.HMAC256(Constant.SECRET.getBytes());

                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refreshToken);

                String username = decodedJWT.getSubject();
                AppUser user = userService.findUserByUsername(username);

                String accessToken = JWT.create()
                        .withSubject(user.getUsername())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream().map(AppRole::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                        Map<String, String> tokens = new HashMap<>();
                        tokens.put("accessToken", accessToken);
                        tokens.put("refreshToken", refreshToken);
                        response.setContentType(APPLICATION_JSON_VALUE);
                        new ObjectMapper().writeValue(response.getOutputStream(), tokens);

            } catch (Exception exception) {
                log.error("Error logging in", exception.getMessage());
                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("errorMessage", exception.getMessage());
                response.setContentType(APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }}

