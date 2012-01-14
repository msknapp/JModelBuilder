/*
  Copyright 2011 Michael Scott Knapp
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
  	http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

*/
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