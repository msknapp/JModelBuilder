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
package com.knapptech.jmodel.model.type.delayed;

import java.io.IOException;
import java.io.Writer;
import java.util.Collection;

import com.knapptech.jmodel.model.MBImport;
import com.knapptech.jmodel.model.MBPath;
import com.knapptech.jmodel.model.MBPathMapping;
import com.knapptech.jmodel.model.ProgrammingLanguage;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.np.IInterface;
import com.knapptech.jmodel.model.type.np.MBInterface;

public class MBDelayedInterfaceType implements IInterface, IDelayedType {
	
	private MBPathMapping mapping;
	
	public MBDelayedInterfaceType(MBPathMapping mapping) {
		if (mapping == null)
			throw new NullPointerException("Must define a mapping.");
		this.mapping = mapping;
	}
	
	public MBInterface getFoundType() {
		return (MBInterface)mapping.getElement();
	}
	
	public boolean isFound() {
		return mapping.getElement()!=null;
	}

	public boolean isIterable() {
		if (isFound()) {
			return getFoundType().isIterable();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	public boolean isAbstract() {
		if (isFound()) {
			return getFoundType().isAbstract();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	public boolean isFinal() {
		if (isFound()) {
			return getFoundType().isFinal();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	public boolean isComparable() {
		if (isFound()) {
			return getFoundType().isComparable();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	public boolean isClonable() {
		if (isFound()) {
			return getFoundType().isClonable();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	public boolean isSerializable() {
		if (isFound()) {
			return getFoundType().isSerializable();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	public IType comparesTo() {
		if (isFound()) {
			return getFoundType().comparesTo();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}
	
	public Collection<MBImport> getRequiredImports() {
		if (isFound()) {
			return getFoundType().getRequiredImports();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	public String getNonCapitalName() {
		String s = mapping.getLocalName();
		s = s.substring(0,1).toUpperCase()+s.substring(1);
		return s;
	}

	public boolean isArray() {
		return false;
	}

	public boolean isException() {
		if (isFound()) {
			return getFoundType().isException();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	public boolean isInterface() {
		return false;
	}

	public boolean isPrimitive() {
		return false;
	}

	public boolean isPrimitiveWrapper() {
		return false;
	}

	public boolean isEnum() {
		if (isFound()) {
			return getFoundType().isEnum();
		}
		throw new IllegalStateException("Cannot determine this for a delayed type.");
	}

	public MBPathMapping getPathMapping() {
		return mapping;
	}

	public String getLocalName() {
		return mapping.getLocalName();
	}

	public String getFullyQualifiedName() {
		return mapping.getFullyQualifiedName();
	}

	public boolean isPackage() {
		return false;
	}

	public void refreshTypes() {}

//	public void addInterface(MBInterface mbi) {
//		if (isFound()) {
//			getFoundType().addInterface(mbi);
//		}
//		throw new IllegalStateException("Cannot do this for a delayed type.");
//	}
//
//	public boolean hasInnerClass(String localName) {
//		if (isFound()) {
//			getFoundType().hasInnerClass(localName);
//		}
//		throw new IllegalStateException("Cannot do this for a delayed type.");
//	}
//
//	public MBInterface getInnerInterface(String localName) {
//		if (isFound()) {
//			getFoundType().getInnerInterface(localName);
//		}
//		throw new IllegalStateException("Cannot do this for a delayed type.");
//	}

	public int write(Writer writer, int currentIndent,
			ProgrammingLanguage language) throws IOException {
		if (isFound()) {
			return getFoundType().write(writer,currentIndent,language);
		}
		throw new IllegalStateException("Cannot do this for a delayed type.");
	}

	public void refreshImports() {
		if (isFound()) {
			getFoundType().refreshImports();
		}
		throw new IllegalStateException("Cannot do this for a delayed type.");
	}

	public int compareTo(IType o) {
		return getPathMapping().compareTo(o.getPathMapping());
	}

	public MBPath getPath() {
		return mapping.getPath();
	}

	public void print(int i) {
		String tb = "";
		for (int j = 0;j<i;j++) {
			tb+="\t";
		}
		if (isFound()) {
			System.out.println("Delayed Interface (Found already): \""+this.getLocalName()+"\": ");
			System.out.println(tb+"  Path: "+this.getPath().getFullyQualifiedName()+"");
		} else {
			System.out.println("Delayed Interface (Not Found already)");
			System.out.println(tb+"  Path: "+this.getPath().getFullyQualifiedName());
		}
	}
}
