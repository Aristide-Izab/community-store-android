package com.example.communitystore.core.access;

public enum UserRole {

    STUDENT("student"),
    FACULTY("faculty"),
    VENDOR("vendor"),
    RESIDENCE("residence");

    private final String value;

    UserRole(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static UserRole fromString(String role) {

        if (role == null) {
            return null;
        }

        for (UserRole userRole : values()) {

            if (userRole.value.equalsIgnoreCase(role.trim())) {
                return userRole;
            }
        }

        return null;
    }
}
