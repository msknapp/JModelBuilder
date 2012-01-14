package com.knapptech.jmodel.beans;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import com.knapptech.jmodel.model.MBModifiers;
import com.knapptech.jmodel.model.Visibility;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.np.IInterface;
import com.knapptech.jmodel.model.type.np.MBClass;

public class MBNonPrimitiveBean {
	private String localName;
	private MBModifiers modifiers = new MBModifiers();
	private List<MBGenericParameterBean> genericParameters = new ArrayList<MBGenericParameterBean>();
	private TreeMap<String,MBFieldBean> fields = new TreeMap<String, MBFieldBean>();
	private TreeMap<String,MBMethodBean> methods = new TreeMap<String, MBMethodBean>();
	private IType _extends = MBClass.OBJECT;
	private List<IInterface> _implements = new ArrayList<IInterface>();
	private boolean comparable = false;
	private String comment = "";
	private MBPackageBean ownerPackage;
	private MBClassBean ownerClass;
	
	public MBNonPrimitiveBean() {
		
	}

	public String getLocalName() {
		return localName;
	}

	public void setLocalName(String localName) {
		this.localName = localName;
	}

	public MBModifiers getModifiers() {
		return modifiers;
	}

	public void setModifiers(MBModifiers modifiers) {
		this.modifiers = modifiers;
	}

	public List<MBGenericParameterBean> getGenericParameters() {
		return genericParameters;
	}

	public void setGenericParameters(List<MBGenericParameterBean> genericParameters) {
		this.genericParameters = genericParameters;
	}

	public TreeMap<String, MBFieldBean> getFields() {
		return fields;
	}

	public void setFields(TreeMap<String, MBFieldBean> fields) {
		this.fields = fields;
	}

	public TreeMap<String, MBMethodBean> getMethods() {
		return methods;
	}

	public void setMethods(TreeMap<String, MBMethodBean> methods) {
		this.methods = methods;
	}

	public IType getExtends() {
		return _extends;
	}

	public void setExtends(IType _extends) {
		this._extends = _extends;
	}

	public List<IInterface> getImplements() {
		return _implements;
	}

	public void setImplements(List<IInterface> _implements) {
		this._implements = _implements;
	}

	public boolean isComparable() {
		return comparable;
	}

	public void setComparable(boolean comparable) {
		this.comparable = comparable;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public MBPackageBean getOwnerPackage() {
		return ownerPackage;
	}

	public void setOwnerPackage(MBPackageBean ownerPackage) {
		this.ownerPackage = ownerPackage;
	}

	public MBClassBean getOwnerClass() {
		return ownerClass;
	}

	public void setOwnerClass(MBClassBean ownerClass) {
		this.ownerClass = ownerClass;
	}
	
	public void setAbstract(boolean _abstract) {
		this.modifiers.setAbstract(_abstract);
	}
	public boolean isAbstract() {
		return modifiers.isAbstract();
	}
	public void setVisibility(Visibility vis) {
		this.modifiers.setVisibility(vis);
	}
	public Visibility getVisibility() {
		return this.modifiers.getVisibility();
	}

}
