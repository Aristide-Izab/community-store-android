package com.example.communitystore.core.access;

public enum UserRole {

    STUDENT("student", "Student"),
    FACULTY("faculty", "Faculty"),
    VENDOR("vendor", "Vendor"),
    RESIDENCE("residence", "Residence");

    private final String value;
    private final String label;

    UserRole(String value, String label) {
        this.value = value;
        this.label = label;
    }

    /** Value stored in Firestore (users/{uid}.role). */
    public String getValue() {
        return value;
    }

    /** Human readable name, e.g. "Vendor". */
    public String getLabel() {
        return label;
    }

    /**
     * Vendor and Residence accounts must submit a verification document
     * after confirming their email address.
     */
    public boolean requiresDocuments() {
        return this == VENDOR || this == RESIDENCE;
    }

    /** Vendor and Residence accounts can list products for sale. */
    public boolean isSeller() {
        return this == VENDOR || this == RESIDENCE;
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
