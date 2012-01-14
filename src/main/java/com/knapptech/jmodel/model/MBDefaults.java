package com.knapptech.jmodel.model;

public final class MBDefaults {
	
	private static ProgrammingLanguage language = ProgrammingLanguage.JAVA;
	private static boolean includeJavadocs = true;
	
	private MBDefaults () {}

	public static ProgrammingLanguage getLanguage() {
		return language;
	}

	public static void setLanguage(ProgrammingLanguage language) {
		MBDefaults.language = language;
	}

	public static boolean isIncludeJavadocs() {
		return includeJavadocs;
	}
	
	public static void setIncludeJavadocs(boolean includeJavadocs) {
		MBDefaults.includeJavadocs = includeJavadocs;
	}
	
	
}