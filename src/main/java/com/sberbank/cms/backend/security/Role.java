package com.sberbank.cms.backend.security;

public class Role {
    private static final String AUTH_PREFIX = "ROLE_";
    public static final String OFFICER = "officer";
    public static final String ADMIN = "admin";
    public static final String ROLE_ADMIN = AUTH_PREFIX + ADMIN;
    public static final String ROLE_OFFICER = AUTH_PREFIX + OFFICER;
    public static final String[] ALL = {OFFICER, ADMIN};

    private Role() {
    }
}