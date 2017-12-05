package com.sberbank.cms.backend;

public class Role {
	public static final String OFFICER = "officer";
	public static final String ADMIN = "admin";

	private Role() {
		// Static methods and fields only
	}

	public static String[] getAllRoles() {
		return new String[] {OFFICER, ADMIN };
	}

}