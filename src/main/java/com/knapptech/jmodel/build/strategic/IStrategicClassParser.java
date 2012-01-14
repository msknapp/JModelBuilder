package com.knapptech.jmodel.build.strategic;

import com.knapptech.jmodel.beans.MBClassBean;
import com.knapptech.jmodel.model.MBPackage;

public interface IStrategicClassParser {
	public MBClassBean getClassBean();
	public void setClassBean(MBClassBean clazz);
	public MBPackage getOwner();
	public void setOwner(MBPackage owner);
	public abstract void buildTypes();
	public abstract void buildElements();
}