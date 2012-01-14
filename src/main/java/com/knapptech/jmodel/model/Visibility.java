package com.knapptech.jmodel.model;

public enum Visibility {
	PUBLIC ("public"),
	PROTECTED ("protected"),
	PACKAGEPRIVATE (""),
	PRIVATE ("private");
	private String text = "";
	Visibility(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public static Visibility parse(String s) {
		if (s==null)
			return PACKAGEPRIVATE;
		if (s.toLowerCase().contains("public"))
			return PUBLIC;
		if (s.toLowerCase().contains("protect"))
			return PROTECTED;
		if (s.toLowerCase().contains("package"))
			return PACKAGEPRIVATE;
		if (s.trim().length()<1)
			return PACKAGEPRIVATE;
		return PRIVATE;
	}
	
	public String toString() {
		return this.name().toLowerCase();
	}
}
