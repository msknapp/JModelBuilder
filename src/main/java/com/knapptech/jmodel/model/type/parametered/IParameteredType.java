package com.knapptech.jmodel.model.type.parametered;

import java.util.List;

import com.knapptech.jmodel.model.GenericUsage;
import com.knapptech.jmodel.model.MBGenericParameter;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.np.IHasGenericParameters;

public interface IParameteredType extends IType {
	IType getType(String representation);
	void setType(String representation,IType type);
	void setType(int index,IType type);
	IHasGenericParameters getTypeWithGenericParameters();
	int sizeParameters();
	List<MBGenericParameter> getParameters();
	MBGenericParameter getParameter(String representation);
	boolean hasGenericParameter(String representation);
	String getGenericName(GenericUsage usage,IType typeUsing);
	String getGenericPart(GenericUsage usage,IType typeUsing);
	MBGenericParameter getParameter(int i);
}