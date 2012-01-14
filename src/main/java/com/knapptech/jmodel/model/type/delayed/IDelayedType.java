package com.knapptech.jmodel.model.type.delayed;

import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.MBType;


public interface IDelayedType extends IType {
	MBType getFoundType();
	boolean isFound();
}