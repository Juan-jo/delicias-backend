package com.delivery.app.security.services;

import com.delivery.app.security.dtos.RoleDTO;

import java.util.List;

public interface RoleService {
    void assignRole(String userId,String roleName);
    List<RoleDTO> getRoles();
}
