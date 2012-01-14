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