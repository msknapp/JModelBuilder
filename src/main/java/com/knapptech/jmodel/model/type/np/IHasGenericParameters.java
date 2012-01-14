package com.knapptech.jmodel.model.type.np;

import java.util.List;

import com.knapptech.jmodel.model.GenericUsage;
import com.knapptech.jmodel.model.IWritable;
import com.knapptech.jmodel.model.MBGenericParameter;
import com.knapptech.jmodel.model.type.IType;

public interface IHasGenericParameters extends IType, IWritable {
	int sizeParameters();
	List<MBGenericParameter> getParameters();
	MBGenericParameter getParameter(String representation);
	void addGenericParameter(MBGenericParameter parameter);
	boolean hasGenericParameter(String representation);
	String getGenericName(GenericUsage usage,IType typeUsing);
	String getGenericPart(GenericUsage usage,IType typeUsing);
	MBGenericParameter getParameter(int i);
	void createImports(MBNonPrimitiveType owner);
}
