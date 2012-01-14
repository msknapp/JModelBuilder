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
package com.knapptech.jmodel.model.type.delayed;

import java.util.Collection;

import com.knapptech.jmodel.model.MBImport;
import com.knapptech.jmodel.model.MBPathMapping;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.MBType;

public class MBDelayedType extends MBType implements IDelayedType {
	
	private MBPathMapping mapping;
	
	public MBDelayedType(MBPathMapping mapping) {
		if (mapping == null)
			throw new NullPointerException("Must define a mapping.");
		this.mapping = mapping;
	}
	
	public MBType getFoundType() {
		return (MBType)mapping.getElement();
	}
	
	public boolean isFound() {
		return mapping.getElement()!=null;
	}

	@Override
	public boolean isIterable() {
		if (isFound()) {
			return getFoundType().isIterable();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	@Override
	public boolean isAbstract() {
		if (isFound()) {
			return getFoundType().isAbstract();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	@Override
	public boolean isFinal() {
		if (isFound()) {
			return getFoundType().isFinal();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	@Override
	public boolean isComparable() {
		if (isFound()) {
			return getFoundType().isComparable();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	@Override
	public boolean isClonable() {
		if (isFound()) {
			return getFoundType().isClonable();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	@Override
	public boolean isSerializable() {
		if (isFound()) {
			return getFoundType().isSerializable();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	@Override
	public IType comparesTo() {
		if (isFound()) {
			return getFoundType().comparesTo();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	@Override
	public Collection<MBImport> getRequiredImports() {
		if (isFound()) {
			return getFoundType().getRequiredImports();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	public void refreshTypes() { }
}