package com.knapptech.jmodel.model.type;

import java.util.ArrayList;
import java.util.Collection;

import com.knapptech.jmodel.model.MBImport;

public class MBPrimitiveType extends MBType {
	public static final MBPrimitiveType BOOLEAN = new MBPrimitiveType("boolean",false,false);
	public static final MBPrimitiveType BYTE = new MBPrimitiveType("byte",false,true);
	public static final MBPrimitiveType INT = new MBPrimitiveType("int",false,true);
	public static final MBPrimitiveType LONG = new MBPrimitiveType("long",false,true);
	public static final MBPrimitiveType FLOAT = new MBPrimitiveType("float",true,false);
	public static final MBPrimitiveType DOUBLE = new MBPrimitiveType("double",true,false);
	public static final MBPrimitiveType CHAR = new MBPrimitiveType("char",false,false);
	
	private MBPrimitiveWrapper wrapper;
	private boolean integerType = false;
	private boolean floatingPointType = false;
	private boolean numberType = false;
	
	private MBPrimitiveType(String name,boolean floatingPointType,boolean integerType) {
		this.localName = name;
		this.integerType =integerType;
		this.floatingPointType = floatingPointType;
		this.numberType = integerType || floatingPointType;
	}

	public MBPrimitiveWrapper getWrapper() {
		if (wrapper == null) {
			if (this == BOOLEAN)
				wrapper = MBPrimitiveWrapper.BOOLEAN;
			if (this == BYTE)
				wrapper = MBPrimitiveWrapper.BYTE;
			if (this == INT)
				wrapper = MBPrimitiveWrapper.INTEGER;
			if (this == LONG)
				wrapper = MBPrimitiveWrapper.LONG;
			if (this == FLOAT)
				wrapper = MBPrimitiveWrapper.FLOAT;
			if (this == DOUBLE)
				wrapper = MBPrimitiveWrapper.DOUBLE;
			if (this == CHAR)
				wrapper = MBPrimitiveWrapper.CHARACTER;
		}
		return wrapper;
	}

	public boolean isIntegerType() {
		return integerType;
	}

	public boolean isFloatingPointType() {
		return floatingPointType;
	}

	public boolean isNumberType() {
		return numberType;
	}

	@Override
	public boolean isIterable() {
		return false;
	}

	@Override
	public boolean isAbstract() {
		return false;
	}

	@Override
	public boolean isFinal() {
		return true;
	}

	@Override
	public boolean isComparable() {
		return true;
	}

	@Override
	public MBType comparesTo() {
		return this;
	}

	@Override
	public Collection<MBImport> getRequiredImports() {
		return new ArrayList<MBImport>();
	}

	@Override
	public boolean isSerializable() {
		return true;
	}

	@Override
	public boolean isClonable() {
		return false;
	}
	
	public static final MBPrimitiveType parse(String s) {
		if (s == null || s.length()<3)
			return null;
		if (s.contains("."))
			return null;
		s = s.trim().toLowerCase();
		if (s.startsWith("boo")) {
			return BOOLEAN;
		} else if (s.startsWith("byt")) {
			return BYTE;
		} else if (s.startsWith("int")) {
			return INT;
		} else if (s.startsWith("lon")) {
			return LONG;
		} else if (s.startsWith("flo")) {
			return FLOAT;
		} else if (s.startsWith("dou")) {
			return DOUBLE;
		} else if (s.startsWith("cha")) {
			return CHAR;
		}
		return null;
	}

	public void refreshTypes() {}


	public static void init() {
		// do not delete, here to ensure the class is statically initialized so path mappings are created.
	}
	
	
	
}