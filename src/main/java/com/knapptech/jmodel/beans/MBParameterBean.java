package com.knapptech.jmodel.beans;

import com.knapptech.jmodel.model.type.MBType;

public class MBParameterBean {
	private String name;
	private MBType type;
	private String typePath;
	private boolean _final;
	
	public MBParameterBean() {
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MBType getType() {
		return type;
	}
	public void setType(MBType type) {
		this.type = type;
	}
	public String getTypePath() {
		return typePath;
	}
	public void setTypePath(String typePath) {
		this.typePath = typePath;
	}
	public boolean is_final() {
		return _final;
	}
	public void set_final(boolean _final) {
		this._final = _final;
	}
}
