package com.knapptech.jmodel.model;

public class NamingScheme {
	
	
	public static final boolean isAllowedForField(String name) {
		
		
		return true;
	}

	public static final boolean isAllowedForMethod(String name) {

		return true;
	}
	
	public static final boolean isAllowedForClass(String name) {

		return true;
	}

	public static final boolean isAllowedForPackage(String name) {

		return true;
	}
	
	private static final boolean isFirstLetterCapital(String name) {
		return Character.isUpperCase(name.charAt(0));
	}
}