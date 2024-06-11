package com.delivery.app.security.dtos;

import lombok.Builder;

@Builder
public record UserRowDTO(
        String id,
        String username,
        String email,
        String firstName,
        String lastName
) {
}
