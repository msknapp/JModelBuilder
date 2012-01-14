package com.knapptech.jmodel.model.type;

import java.util.Collection;

import com.knapptech.jmodel.model.IPathItem;
import com.knapptech.jmodel.model.MBImport;

public interface IType extends IPathItem, Comparable<IType> {
	boolean isIterable();
	boolean isAbstract();
	boolean isFinal();
	boolean isComparable();
	boolean isClonable();
	boolean isSerializable();
	IType comparesTo();
	Collection<MBImport> getRequiredImports();
	String getNonCapitalName() ;
	boolean isArray();
	boolean isException();
	boolean isInterface();
	boolean isPrimitive();
	boolean isPrimitiveWrapper();
	boolean isEnum();
	void refreshTypes();
}