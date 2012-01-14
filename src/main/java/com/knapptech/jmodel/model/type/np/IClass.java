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