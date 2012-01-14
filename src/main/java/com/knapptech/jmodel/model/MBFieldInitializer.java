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
package com.knapptech.jmodel.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.knapptech.jmodel.model.element.MBField;
import com.knapptech.jmodel.model.type.np.MBNonPrimitiveType;

public class MBFieldInitializer {
	private String text;
	private List<MBPathMapping> requiredImports = new ArrayList<MBPathMapping>();
	private MBField field;
	
	private MBFieldInitializer(String text,MBField field) {
		if (text == null || text.length()<1)
			throw new IllegalArgumentException("Must define input string.");
		if (field== null || field.getInitializer() != null)
			throw new IllegalStateException("Field is null, or already has an initializer.");
		this.field = field;
		this.text = text;
	}
	
	public static final MBFieldInitializer createInitializer(String text,MBField field) {
		MBFieldInitializer fi = new MBFieldInitializer(text,field);
		field.addInitializer(fi);
		return fi;
	}
	
	public MBField getField() {
		return field;
	}
	
	public String getText() {
		return text;
	}
	
	public void addRequiredImport(MBPathMapping path) {
		this.requiredImports.add(path);
	}
	
	public Collection<MBPathMapping> getRequiredImports() {
		return Collections.unmodifiableCollection(requiredImports);
	}

	public void createImports(MBNonPrimitiveType owner) {
		for (MBPathMapping mpng : this.requiredImports) {
			MBImport.create(mpng, owner);
		}
	}
}