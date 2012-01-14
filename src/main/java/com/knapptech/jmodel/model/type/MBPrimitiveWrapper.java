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
package com.knapptech.jmodel.model.type;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;

import com.knapptech.jmodel.model.MBImport;
import com.knapptech.jmodel.model.MBPackage;
import com.knapptech.jmodel.model.MBPath;
import com.knapptech.jmodel.model.MBPathMapping;
import com.knapptech.jmodel.model.ProgrammingLanguage;
import com.knapptech.jmodel.model.type.np.MBClass;
import com.knapptech.jmodel.model.type.np.MBNonPrimitiveType;

public class MBPrimitiveWrapper extends MBNonPrimitiveType {
	public static final MBPrimitiveWrapper BOOLEAN = new MBPrimitiveWrapper("Boolean",false,false);
	public static final MBPrimitiveWrapper BYTE = new MBPrimitiveWrapper("Byte",false,true);
	public static final MBPrimitiveWrapper INTEGER = new MBPrimitiveWrapper("Integer",false,true);
	public static final MBPrimitiveWrapper LONG = new MBPrimitiveWrapper("Long",false,true);
	public static final MBPrimitiveWrapper FLOAT = new MBPrimitiveWrapper("Float",true,false);
	public static final MBPrimitiveWrapper DOUBLE = new MBPrimitiveWrapper("Double",true,false);
	public static final MBPrimitiveWrapper CHARACTER = new MBPrimitiveWrapper("Character",false,false);
	
	static {
		MBPathMapping.reserve(BOOLEAN, MBPath.createItemPath("java","lang","Boolean"));
		MBPathMapping.reserve(BYTE, MBPath.createItemPath("java","lang","Byte"));
		MBPathMapping.reserve(INTEGER, MBPath.createItemPath("java","lang","Integer"));
		MBPathMapping.reserve(LONG, MBPath.createItemPath("java","lang","Long"));
		MBPathMapping.reserve(FLOAT, MBPath.createItemPath("java","lang","Float"));
		MBPathMapping.reserve(DOUBLE, MBPath.createItemPath("java","lang","Double"));
		MBPathMapping.reserve(CHARACTER, MBPath.createItemPath("java","lang","Character"));
		MBPackage.JAVALANG.addWrapper(BOOLEAN);
		MBPackage.JAVALANG.addWrapper(BYTE);
		MBPackage.JAVALANG.addWrapper(INTEGER);
		MBPackage.JAVALANG.addWrapper(LONG);
		MBPackage.JAVALANG.addWrapper(FLOAT);
		MBPackage.JAVALANG.addWrapper(DOUBLE);
		MBPackage.JAVALANG.addWrapper(CHARACTER);
	}
	
	private boolean integerType = false;
	private boolean floatingPointType = false;
	private boolean numberType = false;
	private MBPrimitiveType primitive;
	
	private MBPrimitiveWrapper(String localName,boolean floatingPointType,boolean integerType) {
		super(localName,MBClass.OBJECT,MBPackage.JAVALANG,null);
		this.integerType =integerType;
		this.floatingPointType = floatingPointType;
		this.numberType = integerType || floatingPointType;
	}


	public static void init() {
		// do not delete, here to ensure the class is statically initialized so path mappings are created.
	}
	
	public MBPrimitiveType getPrimitive() {
		if (primitive == null) {
			if (this == BOOLEAN)
				primitive = MBPrimitiveType.BOOLEAN;
			if (this == BYTE)
				primitive = MBPrimitiveType.BYTE;
			if (this == INTEGER)
				primitive = MBPrimitiveType.INT;
			if (this == LONG)
				primitive = MBPrimitiveType.LONG;
			if (this == FLOAT)
				primitive = MBPrimitiveType.FLOAT;
			if (this == DOUBLE)
				primitive = MBPrimitiveType.DOUBLE;
			if (this == CHARACTER)
				primitive = MBPrimitiveType.CHAR;
		}
		return primitive;
	}

	@Override
	public String getLocalName() {
		return localName;
	}

	@Override
	public String getFullyQualifiedName() {
		return "java.lang."+localName;
	}

	@Override
	public boolean isPrimitive() {
		return false;
	}

	@Override
	public boolean isPrimitiveWrapper() {
		return true;
	}

	@Override
	public boolean isArray() {
		return false;
	}

	@Override
	public boolean isIterable() {
		return false;
	}

	@Override
	public boolean isAbstract() {
		return false;
	}

	@Override
	public boolean isInterface() {
		return false;
	}

	@Override
	public boolean isEnum() {
		return false;
	}

	@Override
	public boolean isFinal() {
		return true;
	}

	@Override
	public boolean isComparable() {
		return true;
	}

	@Override
	public MBType comparesTo() {
		return this;
	}

	@Override
	public Collection<MBImport> getRequiredImports() {
		return new ArrayList<MBImport>();
	}

	@Override
	public MBPathMapping getPathMapping() {
		return path;
	}


	public boolean isIntegerType() {
		return integerType;
	}


	public boolean isFloatingPointType() {
		return floatingPointType;
	}


	public boolean isNumberType() {
		return numberType;
	}

	@Override
	public boolean isSerializable() {
		return true;
	}

	@Override
	public boolean isClonable() {
		return false;
	}

	public int write(Writer writer, int currentIndent,ProgrammingLanguage language) throws IOException {
		return 0;
	}
}