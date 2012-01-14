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
package com.knapptech.jmodel.model.type.np;

import java.io.IOException;
import java.io.Writer;

import com.knapptech.jmodel.model.MBPackage;
import com.knapptech.jmodel.model.type.MBType;

public class MBException extends MBClass {
	public static final MBException EXCEPTION = new MBException("Exception",THROWABLE,MBPackage.JAVALANG,null);
	
	static {
		EXCEPTION.addInterface(MBInterface.SERIALIZABLE);
	}

	protected MBException(String localName,MBClass superClass,MBPackage ownerPackage,
			MBClass ownerClass) {
		super(localName,superClass,ownerPackage,ownerClass);
	}
	
	public static MBException create(String localName,MBPackage owner) {
		if (owner.hasItemWithName(localName))
			throw new IllegalArgumentException("Cannot create this class, the package already has something with that name.");
		MBException clz = new MBException(localName,EXCEPTION,owner,null);
		owner.addClass(clz);
		return clz;
	}
	
	public static MBException create(String localName,MBException superException,MBPackage owner) {
		if (owner.hasItemWithName(localName))
			throw new IllegalArgumentException("Cannot create this class, the package already has something with that name.");
		MBException clz = new MBException(localName,superException,owner,null);
		owner.addClass(clz);
		return clz;
	}
	
	@Override
	public boolean isIterable() {
		return false;
	}
	
	@Override
	public boolean isFinal() {
		return false;
	}
	
	@Override
	public boolean isException() {
		return true;
	}

	@Override
	public boolean isComparable() {
		return false;
	}
	
	@Override
	public boolean isSerializable() {
		return true;
	}

	@Override
	public MBType comparesTo() {
		return this;
	}

	public int write(Writer writer, int currentIndent) throws IOException {
		return 0;
	}
}