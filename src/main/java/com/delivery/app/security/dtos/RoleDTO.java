package com.delivery.app.security.dtos;

import lombok.Builder;

@Builder
public record RoleDTO(
        String roleName,
        String description
) { }
