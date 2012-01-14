package com.knapptech.jmodel.beans;

import java.util.ArrayList;

public class MBPackageBean {
	private MBPackageBean owner = null;
	private ArrayList<MBPackageBean> subPackages = new ArrayList<MBPackageBean>();
	private ArrayList<MBClassBean> classes = new ArrayList<MBClassBean>();
	private ArrayList<MBInterfaceBean> interfaces = new ArrayList<MBInterfaceBean>();
	private ArrayList<MBEnumBean> enums = new ArrayList<MBEnumBean>();
	
	public MBPackageBean() {}
	
	public ArrayList<MBPackageBean> getSubPackages() {
		return subPackages;
	}
	public void setSubPackages(ArrayList<MBPackageBean> subPackages) {
		this.subPackages = subPackages;
	}
	public ArrayList<MBClassBean> getClasses() {
		return classes;
	}
	public void setClasses(ArrayList<MBClassBean> classes) {
		this.classes = classes;
	}
	public ArrayList<MBInterfaceBean> getInterfaces() {
		return interfaces;
	}
	public void setInterfaces(ArrayList<MBInterfaceBean> interfaces) {
		this.interfaces = interfaces;
	}
	public ArrayList<MBEnumBean> getEnums() {
		return enums;
	}
	public void setEnums(ArrayList<MBEnumBean> enums) {
		this.enums = enums;
	}

	public MBPackageBean getOwner() {
		return owner;
	}

	public void setOwner(MBPackageBean owner) {
		this.owner = owner;
	}
	
	
}
