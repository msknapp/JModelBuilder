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

import com.knapptech.jmodel.model.MBModifiers;
import com.knapptech.jmodel.model.MBParameter;
import com.knapptech.jmodel.model.MBSourceCode;
import com.knapptech.jmodel.model.type.IType;

public class MBMethodBean {

	private String name;
	private MBModifiers modifiers = new MBModifiers();
	private String comment;

	private String typePath = "java.lang.Object";
	private IType type;
	private boolean modifiesClass=true;
	private MBSourceCode code = new MBSourceCode();
	private List<MBParameter> parameters = new ArrayList<MBParameter>();
	private MBClassBean ownerBean;
	private IType owner;
	
	public MBMethodBean() {}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MBModifiers getModifiers() {
		return modifiers;
	}
	public void setModifiers(MBModifiers modifiers) {
		this.modifiers = modifiers;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public String getTypePath() {
		return typePath;
	}
	public void setTypePath(String typePath) {
		this.typePath = typePath;
	}
	public IType getType() {
		return type;
	}
	public void setType(IType type) {
		this.type = type;
	}

	public boolean isModifiesClass() {
		return modifiesClass;
	}

	public void setModifiesClass(boolean modifiesClass) {
		this.modifiesClass = modifiesClass;
	}

	public MBSourceCode getCode() {
		return code;
	}

	public MBSourceCode getSourceCode() {
		return code;
	}

	public void setCode(MBSourceCode code) {
		this.code = code;
	}

	public List<MBParameter> getParameters() {
		return parameters;
	}

	public void setParameters(List<MBParameter> parameters) {
		this.parameters = parameters;
	}

	public void addParameter(MBParameter mbParameter) {
		this.parameters.add(mbParameter);
	}

	public MBClassBean getOwnerBean() {
		return ownerBean;
	}

	public void setOwnerBean(MBClassBean ownerBean) {
		this.ownerBean = ownerBean;
	}

	public IType getOwner() {
		return owner;
	}

	public void setOwner(IType owner) {
		this.owner = owner;
	}
}
