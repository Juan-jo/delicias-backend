package com.delivery.app.configs.constants;

public enum RoleType {
    STORE("ROLE_STORE"), ROOT("ROLE_ROOT");

    private final String roleName;

    RoleType(String contactType) {
        this.roleName = contactType;
    }

    public String value() {
        return roleName;
    }
}
