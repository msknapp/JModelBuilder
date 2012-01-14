package com.knapptech.jmodel.model.type;

import java.util.Collection;

import com.knapptech.jmodel.model.MBImport;
import com.knapptech.jmodel.model.MBPathMapping;
import com.knapptech.jmodel.model.type.delayed.IDelayedType;

public class MBArrayType extends MBType {
	private byte dimensions = 1;
	private IType type;
	
	public MBArrayType(IType type,byte dimensions) {
		if (type == null)
			throw new NullPointerException("Must define a type the array is of.");
		if (type instanceof MBArrayType) {
			throw new IllegalArgumentException("Array's sub-type cannot be another array type.");
		}
		if (dimensions <1 || dimensions>4) {
			throw new IllegalArgumentException("Arrays of size "+dimensions+" are not supported.");
		}
		this.dimensions = dimensions;
		this.type = type;
	}
	
	public IType getType() {
		return type;
	}
	
	public byte getDimensions() {
		return dimensions;
	}
	
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this) 
			return true;
		if (!(o instanceof MBArrayType) )
			return false;
		MBArrayType t = (MBArrayType) o;
		return t.dimensions == dimensions && t.type.equals(type);
	}
	
	public int hashCode() {
		return dimensions * type.hashCode();
	}

	@Override
	public String getLocalName() {
		return type.getLocalName();
	}

	@Override
	public String getFullyQualifiedName() {
		return type.getFullyQualifiedName();
	}

	@Override
	public boolean isPrimitive() {
		return type.isPrimitive();
	}

	@Override
	public boolean isPrimitiveWrapper() {
		return type.isPrimitiveWrapper();
	}

	@Override
	public boolean isArray() {
		return true;
	}

	@Override
	public boolean isIterable() {
		return true;
	}

	@Override
	public boolean isAbstract() {
		return type.isAbstract();
	}

	@Override
	public boolean isInterface() {
		return type.isInterface();
	}

	@Override
	public boolean isEnum() {
		return type.isEnum();
	}

	@Override
	public boolean isFinal() {
		return type.isFinal();
	}

	@Override
	public boolean isComparable() {
		return type.isComparable();
	}

	@Override
	public MBType comparesTo() {
		return this;
	}

	@Override
	public Collection<MBImport> getRequiredImports() {
		return type.getRequiredImports();
	}

	@Override
	public MBPathMapping getPathMapping() {
		return type.getPathMapping();
	}

	@Override
	public boolean isSerializable() {
		return type.isSerializable();
	}

	@Override
	public boolean isClonable() {
		return false;
	}

	public void refreshTypes() {
		if (type instanceof IDelayedType) {
			IDelayedType t = (IDelayedType)type;
			if (t.isFound())
				this.type = t.getFoundType();
		}
	}
}