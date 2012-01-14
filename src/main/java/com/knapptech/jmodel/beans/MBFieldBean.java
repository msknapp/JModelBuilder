package com.knapptech.jmodel.beans;

import com.knapptech.jmodel.model.MBModifiers;
import com.knapptech.jmodel.model.type.IType;

public class MBFieldBean {
	
	private String name;
	private MBModifiers modifiers = new MBModifiers();
	private String comment;

	private String typePath = "java.lang.Object";
	private IType type;
	private boolean gettable = true;
	private boolean settable = true;
	private boolean addable = true;
	private boolean removable = true;
	private boolean incrementable = true;
	private boolean decrementable = true;
	private boolean unique = true;
	private boolean comparable = true;
	private boolean serializable = true;
	private boolean _overrides = false;
	private boolean _key = false;
	private boolean nullable = true;
	private int linkId = -1;
	private int id = 0;
	private boolean immutable = false;
	private MBClassBean ownerBean;
	private IType owner;
	private String initializer;
	
	private byte compareOrder = -1;
	
	public MBFieldBean() {}
	
	public boolean isGettable() {
		return gettable;
	}
	public void setGettable(boolean gettable) {
		this.gettable = gettable;
	}
	public boolean isSettable() {
		return settable;
	}
	public void setSettable(boolean settable) {
		this.settable = settable;
	}
	public boolean isAddable() {
		return addable;
	}
	public void setAddable(boolean addable) {
		this.addable = addable;
	}
	public boolean isRemovable() {
		return removable;
	}
	public void setRemovable(boolean removable) {
		this.removable = removable;
	}
	public boolean isIncrementable() {
		return incrementable;
	}
	public void setIncrementable(boolean incrementable) {
		this.incrementable = incrementable;
	}
	public boolean isDecrementable() {
		return decrementable;
	}
	public void setDecrementable(boolean decrementable) {
		this.decrementable = decrementable;
	}
	public boolean isUnique() {
		return unique;
	}
	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	public byte getCompareOrder() {
		return compareOrder;
	}
	public void setCompareOrder(byte compareOrder) {
		this.compareOrder = compareOrder;
	}

	public String getTypePath() {
		return typePath;
	}

	public void setTypePath(String typePath) {
		this.typePath = typePath;
	}

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

	public IType getType() {
		return type;
	}

	public void setType(IType type) {
		this.type = type;
	}

	public boolean isComparable() {
		return comparable;
	}

	public void setComparable(boolean comparable) {
		this.comparable = comparable;
	}

	public boolean isSerializable() {
		return serializable;
	}

	public void setSerializable(boolean serializable) {
		this.serializable = serializable;
	}

	public boolean isStatic() {
		return this.modifiers.isStatic();
	}

	public void setStatic(boolean _static) {
		this.modifiers.setStatic(_static);
	}

//	public boolean isStatic() {
//		return _static;
//	}
//
//	public void setStatic(boolean _static) {
//		this._static = _static;
//	}

	public boolean isSynchronized() {
		return modifiers.isSynchronized();
	}

	public void setSynchronized(boolean _synchronized) {
		this.modifiers.setSynchronized(_synchronized);
	}

	public boolean isOverrides() {
		return _overrides;
	}

	public void setOverrides(boolean _overrides) {
		this._overrides = _overrides;
	}

	public boolean isFinal() {
		return this.modifiers.isFinal();
	}

	public void setFinal(boolean _final) {
		this.getModifiers().setFinal(_final);
	}

	public boolean isKey() {
		return _key;
	}

	public void setKey(boolean _key) {
		this._key = _key;
	}

	public boolean isNullable() {
		return nullable;
	}

	public void setNullable(boolean nullable) {
		this.nullable = nullable;
	}

	public int getLinkId() {
		return linkId;
	}

	public void setLinkId(int linkId) {
		this.linkId = linkId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isImmutable() {
		return immutable;
	}

	public void setImmutable(boolean immutable) {
		this.immutable = immutable;
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

	public String getInitializer() {
		return initializer;
	}

	public void setInitializer(String initializer) {
		this.initializer = initializer;
	}
}
