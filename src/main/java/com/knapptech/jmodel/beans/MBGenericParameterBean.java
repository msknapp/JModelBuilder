package com.knapptech.jmodel.beans;

import java.util.ArrayList;
import java.util.List;

import com.knapptech.jmodel.model.type.IType;

public class MBGenericParameterBean {
	private String representation;
	private List<IType> upperBounds = new ArrayList<IType>();
	private List<IType> lowerBounds = new ArrayList<IType>();
	public String getRepresentation() {
		return representation;
	}
	public void setRepresentation(String representation) {
		this.representation = representation;
	}
	public List<IType> getUpperBounds() {
		return upperBounds;
	}
	public void setUpperBounds(List<IType> upperBounds) {
		this.upperBounds = upperBounds;
	}
	public List<IType> getLowerBounds() {
		return lowerBounds;
	}
	public void setLowerBounds(List<IType> lowerBounds) {
		this.lowerBounds = lowerBounds;
	}
}
