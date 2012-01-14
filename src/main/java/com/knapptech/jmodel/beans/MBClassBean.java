package com.knapptech.jmodel.beans;

import java.util.ArrayList;
import java.util.List;

import com.knapptech.jmodel.build.strategic.BuildStrategy;
import com.knapptech.jmodel.model.type.np.IClass;
import com.knapptech.jmodel.model.type.np.IEnum;
import com.knapptech.jmodel.model.type.np.IInterface;

public class MBClassBean extends MBNonPrimitiveBean {

	private BuildStrategy strategy;
	private List<IInterface> innerInterfaces = new ArrayList<IInterface>();
	private List<IClass> innerClasses = new ArrayList<IClass>();
	private List<IEnum> innerEnums = new ArrayList<IEnum>();
	
	public MBClassBean() {
		
	}

	public BuildStrategy getStrategy() {
		return strategy;
	}

	public void setStrategy(BuildStrategy strategy) {
		this.strategy = strategy;
	}

	public List<IInterface> getInnerInterfaces() {
		return innerInterfaces;
	}

	public void setInnerInterfaces(List<IInterface> innerInterfaces) {
		this.innerInterfaces = innerInterfaces;
	}

	public List<IClass> getInnerClasses() {
		return innerClasses;
	}

	public void setInnerClasses(List<IClass> innerClasses) {
		this.innerClasses = innerClasses;
	}

	public List<IEnum> getInnerEnums() {
		return innerEnums;
	}

	public void setInnerEnums(List<IEnum> innerEnums) {
		this.innerEnums = innerEnums;
	}
	
}
