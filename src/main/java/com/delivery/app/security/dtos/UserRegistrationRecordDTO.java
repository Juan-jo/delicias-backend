package com.delivery.app.security.dtos;

public record UserRegistrationRecord(String username,
                                     String email,
                                     String firstName,
                                     String lastName,
                                     String password,
                                     String roleName,
                                     Integer restaurantId
) { }
