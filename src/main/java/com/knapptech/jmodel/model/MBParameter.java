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
package com.knapptech.jmodel.model;

import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.delayed.IDelayedType;

public class MBParameter implements Comparable<MBParameter> {
	private String name;
	private boolean _final = false;
	private IType type = null;
	private boolean varargs = false;
	
	public MBParameter(IType type,String name) {
		if (name == null)
			throw new NullPointerException("Must define a non-null name.");
		if (type == null)
			throw new NullPointerException("Must define a non-null type.");
		this.type = type;
		this.name = name;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		if (name == null)
			throw new NullPointerException("Must define a non-null name.");
		this.name = name;
	}

	public boolean isFinal() {
		return _final;
	}

	public void setFinal(boolean _final) {
		this._final = _final;
	}

	public IType getType() {
		return type;
	}

	public void setType(IType type) {
		if (type == null)
			throw new NullPointerException("Must define a non-null type.");
		this.type = type;
	}

	public int compareTo(MBParameter o) {
		if (o == null)
			return -1;
		int i = type.compareTo(o.type);
		if (i != 0)
			return i;
		return name.compareTo(o.name);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MBParameter other = (MBParameter) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (type == null) {
			if (other.type != null)
				return false;
		} else if (!type.equals(other.type))
			return false;
		return true;
	}

	public boolean isVarargs() {
		return varargs;
	}

	public void setVarargs(boolean varargs) {
		this.varargs = varargs;
	}

	public void refreshType() {
		if (type instanceof IDelayedType) {
			IDelayedType t = (IDelayedType)type;
			if (t.isFound())
				this.type = t.getFoundType();
		}
	}
}