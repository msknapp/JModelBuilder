package com.knapptech.jmodel.model.type;

import java.util.Collection;

import com.knapptech.jmodel.model.MBImport;
import com.knapptech.jmodel.model.MBPath;
import com.knapptech.jmodel.model.MBPathMapping;
import com.knapptech.jmodel.model.type.np.MBEnum;
import com.knapptech.jmodel.model.type.np.MBException;
import com.knapptech.jmodel.model.type.np.MBInterface;

public abstract class MBType implements IType {
	
	public abstract boolean isIterable();
	public abstract boolean isAbstract();
	public abstract boolean isFinal();
	public abstract boolean isComparable();
	public abstract boolean isClonable();
	public abstract boolean isSerializable();
	public abstract IType comparesTo();
	public abstract Collection<MBImport> getRequiredImports();
	
	protected String localName;
	protected MBPathMapping path;
	
	public int compareTo(IType t) {
		return getFullyQualifiedName().compareTo(t.getFullyQualifiedName());
	}
	
	public String getCapitalName() {
		String s = getLocalName();
		return s.substring(0,1).toUpperCase()+s.substring(1);
	}
	
	public String getNonCapitalName() {
		String s = getLocalName();
		return s.substring(0,1).toLowerCase()+s.substring(1);
	}
	
	public String getLocalName() {
		return localName;
	}
	

	public String getFullyQualifiedName() {
		if (path != null)
			return path.toString();
		return localName;
	}
	
	public boolean isArray() {
		return (this instanceof MBArrayType);
	}
	
	public boolean isException() {
		return (this instanceof MBException);
	}
	
	public boolean isInterface() {
		return (this instanceof MBInterface);
	}
	
	public boolean isPrimitive() {
		return (this instanceof MBPrimitiveType);
	}
	
	public boolean isPrimitiveWrapper() {
		return (this instanceof MBPrimitiveWrapper);
	}
	
	public boolean isEnum() {
		return (this instanceof MBEnum);
	}
	
	public MBPath getPath() {
		return path.getPath();
	}
	
	public MBPathMapping getPathMapping() {
		return path;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((localName == null) ? 0 : localName.hashCode());
		result = prime * result + ((path == null) ? 0 : path.hashCode());
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
		MBType other = (MBType) obj;
		if (localName == null) {
			if (other.localName != null)
				return false;
		} else if (!localName.equals(other.localName))
			return false;
		if (path == null) {
			if (other.path != null)
				return false;
		} else if (!path.equals(other.path))
			return false;
		return true;
	}
	
	public String toString() {
		if (path != null)
			return path.toString();
		return localName;
	}
	
	public boolean isPackage() {
		return false;
	}
}