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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.MBType;
import com.knapptech.jmodel.model.type.delayed.IDelayedType;
import com.knapptech.jmodel.model.type.np.IClass;
import com.knapptech.jmodel.model.type.np.IHasGenericParameters;
import com.knapptech.jmodel.model.type.np.MBClass;

public class MBGenericParameter implements IType {
	private String representation;
	private IType owner;
	private List<IType> upperBounds = new ArrayList<IType>();
	private List<IType> lowerBounds = new ArrayList<IType>();
	private MBPathMapping mapping;
	
	public static final MBGenericParameter getOrCreate(IHasGenericParameters owner,String representation,
			IType upperBounds) {
		MBPath p = owner.getPath().createSubPath(representation, false);
		MBPathMapping mpng = MBPathMapping.getOrCreate(p);
		if (mpng.getElement() != null) {
			return (MBGenericParameter)mpng.getElement();
		}
		return new MBGenericParameter(owner, representation, upperBounds);
	}
	
	private MBGenericParameter(IHasGenericParameters owner,String representation,
			IType upperBounds) {
		if (owner == null)
			throw new NullPointerException("Must define owner");
		if (representation == null || representation.length()<1)
			throw new IllegalArgumentException("Must define a representation.");
		this.owner = owner;
		this.representation = representation;
		if (upperBounds != null)
			this.upperBounds.add(upperBounds);
		if (this.upperBounds.size()<1)
			this.upperBounds.add(MBClass.OBJECT);
		MBPath path = this.owner.getPath().createSubPath(representation, false);
		this.mapping = MBPathMapping.reserve(this, path);
		owner.addGenericParameter(this);
	}
	
	public static final MBGenericParameter getOrCreate(IHasGenericParameters owner,String representation,
			Iterable<IType> upperBounds,Iterable<IType> lowerBounds) {
		MBPath p = owner.getPath().createSubPath(representation, false);
		MBPathMapping mpng = MBPathMapping.getOrCreate(p);
		if (mpng.getElement() != null) {
			return (MBGenericParameter)mpng.getElement();
		}
		return new MBGenericParameter(owner, representation, upperBounds,lowerBounds);
	}
	
	private MBGenericParameter(IHasGenericParameters owner,String representation,
			Iterable<IType> upperBounds,Iterable<IType> lowerBounds) {
		if (owner == null)
			throw new NullPointerException("Must define owner");
		if (representation == null || representation.length()<1)
			throw new IllegalArgumentException("Must define a representation.");
		this.owner = owner;
		this.representation = representation;
		if (upperBounds != null)
			for (IType t : upperBounds)
				this.upperBounds.add(t);
		if (this.upperBounds.isEmpty())
			this.upperBounds.add(MBClass.OBJECT);
		if (lowerBounds != null) 
			for (IType t : lowerBounds)
				this.lowerBounds.add(t);
		MBPath path = this.owner.getPath().createSubPath(representation, false);
		this.mapping = MBPathMapping.reserve(this, path);
		owner.addGenericParameter(this);
	}
	
	public String getRepresentation() {
		return representation;
	}
	
	public List<IType> getUpperBounds() {
		return Collections.unmodifiableList(this.upperBounds);
	}
	
	public List<IType> getLowerBounds() {
		return Collections.unmodifiableList(this.lowerBounds);
	}
	
	public IType getOwner() {
		return owner;
	}
	
	public MBPath getPath() {
		return mapping.getPath();
	}
	
	public MBPathMapping getPathMapping() {
		return mapping;
	}
	
	public String getLocalName() {
		return getPathMapping().getLocalName();
	}
	
	public String getFullyQualifiedName() {
		return getPathMapping().getLocalName();
	}
	
	public boolean isPackage() {
		return false;
	}
	public int compareTo(IType o) {
		if (o==null)
			return -1;
		return this.mapping.compareTo(o.getPathMapping());
	}
	public boolean isIterable() {
		for (IType t : this.upperBounds) {
			if (t.isIterable())
				return true;
		}
		for (IType t : this.lowerBounds) {
			if (!t.isIterable())
				return false;
		}
		throw new IllegalStateException("We cannot know this about a generic parameter.");
	}
	public boolean isAbstract() {
		throw new IllegalStateException("We cannot know this about a generic parameter.");
	}
	public boolean isFinal() {
		throw new IllegalStateException("We cannot know this about a generic parameter.");
	}
	public boolean isComparable() {
		for (IType t : this.upperBounds) {
			if (t.isComparable())
				return true;
		}
		for (IType t : this.lowerBounds) {
			if (!t.isComparable())
				return false;
		}
		throw new IllegalStateException("We cannot know this about a generic parameter.");
	}
	
	public boolean isClonable() {
		// even if a super is serializable, that does not mean
		// the child is, and vice versa.
		throw new IllegalStateException("We cannot know this about a generic parameter.");
	}
	
	public boolean isSerializable() {
		// even if a super is serializable, that does not mean
		// the child is, and vice versa.
		throw new IllegalStateException("We cannot know this about a generic parameter.");
	}
	
	public MBType comparesTo() {
		return null;
	}
	
	public Collection<MBImport> getRequiredImports() {
		return new ArrayList<MBImport>();
	}
	
	public String getNonCapitalName() {
		return representation.substring(0,1).toLowerCase()+representation.substring(1);
	}
	
	public boolean isArray() {
		// in java, you cannot have a generic parameter that is an array.
		return false;
	}
	
	public boolean isException() {
		for (IType t : this.upperBounds) {
			if (t.isException())
				return true;
			if (t instanceof IClass)
				return false;
		}
		for (IType t : this.lowerBounds) {
			if (!t.isException())
				return false;
			if (!(t instanceof IClass))
				return true;
		}
		throw new IllegalStateException("We cannot know this about a generic parameter.");
	}
	public boolean isInterface() {
		for (IType t : this.upperBounds) {
			if (t instanceof IClass)
				return false;
		}
		for (IType t : this.lowerBounds) {
			if (!(t instanceof IClass))
				return true;
		}
		throw new IllegalStateException("We cannot know this about a generic parameter.");
	}
	
	public boolean isPrimitive() {
		return false;
	}
	
	public boolean isPrimitiveWrapper() {
		for (IType t : this.upperBounds) {
			if (t.isPrimitiveWrapper())
				return true;
		}
		for (IType t : this.lowerBounds) {
			if (!t.isPrimitiveWrapper())
				return false;
		}
		throw new IllegalStateException("We cannot know this about a generic parameter.");
	}
	
	public boolean isEnum() {
		for (IType t : this.upperBounds) {
			if (t.isEnum())
				return true;
		}
		for (IType t : this.lowerBounds) {
			if (!t.isEnum())
				return false;
		}
		throw new IllegalStateException("We cannot know this about a generic parameter.");
	}
	
	public void refreshTypes() {
		if (owner instanceof IDelayedType) {
			IDelayedType dto = (IDelayedType)owner;
			if (dto.isFound()) {
				this.owner = dto.getFoundType();
				owner.refreshTypes();
			}
		}
		for (int i = 0;i<upperBounds.size();i++) {
			IType ubt = upperBounds.get(i);
			if (ubt.getPath().getFullyQualifiedName().length()<1)
				continue;
			if (ubt instanceof IDelayedType) {
				IDelayedType dto = (IDelayedType)ubt;
				if (dto.isFound()) {
					this.upperBounds.set(i, dto.getFoundType());
					upperBounds.get(i).refreshTypes();
				}
			}
		}
		for (int i = 0;i<lowerBounds.size();i++) {
			IType ubt = lowerBounds.get(i);
			if (ubt.getPath().getFullyQualifiedName().length()<1)
				continue;
			if (ubt instanceof IDelayedType) {
				IDelayedType dto = (IDelayedType)ubt;
				if (dto.isFound()) {
					this.lowerBounds.set(i, dto.getFoundType());
					lowerBounds.get(i).refreshTypes();
				}
			}
		}
	}
}