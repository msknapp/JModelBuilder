package com.knapptech.jmodel.model.type.np;

import com.knapptech.jmodel.model.IWritable;
import com.knapptech.jmodel.model.type.IType;

public interface IInterface extends IType, IWritable {

	void refreshImports();

	void print(int i);

}
