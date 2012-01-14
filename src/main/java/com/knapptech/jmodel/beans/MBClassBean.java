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
package com.knapptech.jmodel.beans;

import java.util.ArrayList;
import java.util.List;

import com.knapptech.jmodel.build.strategic.BuildStrategy;
import com.knapptech.jmodel.model.type.np.IClass;
import com.knapptech.jmodel.model.type.np.IEnum;
import com.knapptech.jmodel.model.type.np.IInterface;

public class MBClassBean extends MBNonPrimitiveBean {

	private BuildStrategy strategy;
	private List<IInterface> innerInterfaces = new ArrayList<IInterface>();
	private List<IClass> innerClasses = new ArrayList<IClass>();
	private List<IEnum> innerEnums = new ArrayList<IEnum>();
	
	public MBClassBean() {
		
	}

	public BuildStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(BuildStrategy strategy) {
		this.strategy = strategy;
	}

	public List<IInterface> getInnerInterfaces() {
		return innerInterfaces;
	}

	public void setInnerInterfaces(List<IInterface> innerInterfaces) {
		this.innerInterfaces = innerInterfaces;
	}

	public List<IClass> getInnerClasses() {
		return innerClasses;
	}

	public void setInnerClasses(List<IClass> innerClasses) {
		this.innerClasses = innerClasses;
	}

	public List<IEnum> getInnerEnums() {
		return innerEnums;
	}

	public void setInnerEnums(List<IEnum> innerEnums) {
		this.innerEnums = innerEnums;
	}
	
}
