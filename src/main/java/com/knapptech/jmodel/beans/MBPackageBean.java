/*
  Copyright 2011 Michael Scott Knapp
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
  	http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

*/
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
