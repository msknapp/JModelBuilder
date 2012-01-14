package com.knapptech.jmodel.model.type.np;

import com.knapptech.jmodel.model.IWritable;
import com.knapptech.jmodel.model.element.MBConstructor;
import com.knapptech.jmodel.model.type.IType;

public interface IClass extends IType, IWritable {
	void addInterface(IInterface mbi);
	boolean hasInnerClass(String localName);
	IInterface getInnerInterface(String localName);
	void refreshImports();
	void addConstructor(MBConstructor mbConstructor);
	MBEnum getEnum(String localName);
	void addEnum(MBEnum clz);
	void print(int i);
}