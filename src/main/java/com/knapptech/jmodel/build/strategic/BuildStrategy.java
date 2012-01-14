package com.knapptech.jmodel.build.strategic;

public enum BuildStrategy {
	BEAN,
	IMMUTABLE,
	BEANANDINTERFACE,
	IMMUTABLEANDINTERFACE,
	EXTENSIBLE,
	EXTENSIBLESLAVE,
	SINGLETON,
	SINGLETONANDINTERFACE,
	MANAGED,
	FACTORY,
	LOCKING,
	UNLOCKING;
	
	private BuildStrategy() {
		
	}
	
	public static BuildStrategy parse(String s) {
		s = s.toLowerCase();
		for (BuildStrategy b : values()) {
			if (b.name().toLowerCase().equals(s))
				return b;
		}
		return null;
	}
}
