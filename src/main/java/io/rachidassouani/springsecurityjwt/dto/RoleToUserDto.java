package io.rachidassouani.springsecurityjwt.dto;

import lombok.Data;

@Data
public class RoleToUserDto {
    private String username;
    private String roleName;
}
