package com.knapptech.jmodel.model.type.np;

import java.io.IOException;
import java.io.Writer;

import com.knapptech.jmodel.model.MBPackage;
import com.knapptech.jmodel.model.type.MBType;

public class MBException extends MBClass {
	public static final MBException EXCEPTION = new MBException("Exception",THROWABLE,MBPackage.JAVALANG,null);
	
	static {
		EXCEPTION.addInterface(MBInterface.SERIALIZABLE);
	}

	protected MBException(String localName,MBClass superClass,MBPackage ownerPackage,
			MBClass ownerClass) {
		super(localName,superClass,ownerPackage,ownerClass);
	}
	
	public static MBException create(String localName,MBPackage owner) {
		if (owner.hasItemWithName(localName))
			throw new IllegalArgumentException("Cannot create this class, the package already has something with that name.");
		MBException clz = new MBException(localName,EXCEPTION,owner,null);
		owner.addClass(clz);
		return clz;
	}
	
	public static MBException create(String localName,MBException superException,MBPackage owner) {
		if (owner.hasItemWithName(localName))
			throw new IllegalArgumentException("Cannot create this class, the package already has something with that name.");
		MBException clz = new MBException(localName,superException,owner,null);
		owner.addClass(clz);
		return clz;
	}
	
	@Override
	public boolean isIterable() {
		return false;
	}
	
	@Override
	public boolean isFinal() {
		return false;
	}
	
	@Override
	public boolean isException() {
		return true;
	}

	@Override
	public boolean isComparable() {
		return false;
	}
	
	@Override
	public boolean isSerializable() {
		return true;
	}

	@Override
	public MBType comparesTo() {
		return this;
	}

	public int write(Writer writer, int currentIndent) throws IOException {
		return 0;
	}
}