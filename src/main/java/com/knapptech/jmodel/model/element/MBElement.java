package com.knapptech.jmodel.model.element;

import java.io.IOException;
import java.io.Writer;

import com.knapptech.jmodel.model.IWritable;
import com.knapptech.jmodel.model.MBDocComment;
import com.knapptech.jmodel.model.MBModifiers;
import com.knapptech.jmodel.model.ProgrammingLanguage;
import com.knapptech.jmodel.model.type.np.MBNonPrimitiveType;

public abstract class MBElement implements Comparable<MBElement>, IWritable {
	protected String name;
	protected MBModifiers modifiers = new MBModifiers();
	protected MBNonPrimitiveType owner = null;
	protected MBDocComment comment = new MBDocComment();
	
	MBElement(String name,MBNonPrimitiveType owner) {
		if (name == null)
			throw new NullPointerException("Must specify an element name.");
		if (owner == null)
			throw new NullPointerException("Must specify an element owner.");
		this.name = name;
		this.owner = owner;
	}
	
	public String toString() {
		return name;
	}
	
	public String getName() {
		return name;
	}
	
	public MBModifiers getModifiers() {
		return modifiers;
	}
	
	public void setModifiers(MBModifiers modifiers) {
		if (modifiers == null)
			throw new NullPointerException("Cannot set modifiers to null.");
		this.modifiers = modifiers;
	}
	
	public int compareTo(MBElement o) {
		if (o == null)
			return -1;
		int i = getTypeNumber(this);
		int i2 = getTypeNumber(o);
		if (i-i2 != 0) 
			return i-i2;
		return this.name.compareTo(o.name);
	}
	
	private int getTypeNumber(MBElement m) {
		if (m instanceof MBField)
			return 1;
		if (m instanceof MBConstructor)
			return 2;
		return 3;
	}

	public MBNonPrimitiveType getOwner() {
		return owner;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
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
		MBElement other = (MBElement) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		return true;
	}


	public abstract int write(Writer writer, int currentIndent,ProgrammingLanguage language) throws IOException;
}