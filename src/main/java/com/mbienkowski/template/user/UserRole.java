package com.mbienkowski.template.user;

import static lombok.AccessLevel.PRIVATE;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
public enum UserRole {
    ADMIN(RoleNames.ADMIN),
    CLIENT(RoleNames.CLIENT);

    private final String label;

    @Override
    public String toString() {
        return this.label;
    }

    @NoArgsConstructor(access = PRIVATE)
    public static class RoleNames {

        public static final String ADMIN = "ADMIN";
        public static final String CLIENT = "CLIENT";
    }
}
