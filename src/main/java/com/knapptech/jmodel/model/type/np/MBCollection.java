package com.knapptech.jmodel.model.type.np;

import com.knapptech.jmodel.model.MBPackage;

public class MBCollection extends MBClass {

	protected MBCollection(String localName, MBClass superType,
			MBPackage ownerPackage, MBClass ownerClass) {
		super(localName, superType, ownerPackage, ownerClass);
	}
	
	@Override
	public boolean isIterable() {
		return true;
	}

}
