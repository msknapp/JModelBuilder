package com.knapptech.jmodel.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.knapptech.jmodel.model.element.MBMethod;
import com.knapptech.jmodel.model.type.IType;

public class MBMethodSignature implements Comparable<MBMethodSignature> {
	private String name;
	private List<IType> parameterTypes = new ArrayList<IType>();
	
	public MBMethodSignature(MBMethod method) {
		if (method == null)
			throw new NullPointerException("Must provide a method.");
		if (method.getName() == null || method.getName().length()<1)
			throw new IllegalArgumentException("The method must have a name.");
		this.name = method.getName();
		for (MBParameter prm : method.getParameters()) {
			IType t = prm.getType();
			if (t == null) 
				throw new NullPointerException("Parameters cannot have a null type.");
			this.parameterTypes.add(t);
		}
	}
	
	public String getName() {
		return name;
	}
	
	public List<IType> getParameterTypes() {
		return Collections.unmodifiableList(this.parameterTypes);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result
				+ ((parameterTypes == null) ? 0 : parameterTypes.hashCode());
		return result;
	}
	
	private String getNameWithoutGetSet() {
		if (name.startsWith("get") || name.startsWith("set"))
			return name.substring(3);
		return name;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MBMethodSignature other = (MBMethodSignature) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (parameterTypes == null) {
			if (other.parameterTypes != null)
				return false;
		} else if (!parameterTypes.equals(other.parameterTypes))
			return false;
		return true;
	}

	public int compareTo(MBMethodSignature o) {
		int i = getNameWithoutGetSet().compareTo(o.getNameWithoutGetSet());
		if (i!=0)
			return i;
		i = getName().compareTo(o.getName());
		if (i!=0)
			return i;
		int mx = Math.min(this.parameterTypes.size(),o.parameterTypes.size());
		for (int j = 0;j<mx;j++) {
			i = parameterTypes.get(j).compareTo(o.parameterTypes.get(j));
			if (i!=0)
				return i;
		}
		return parameterTypes.size()-o.parameterTypes.size();
	}
	
	
	
}