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
package com.knapptech.jmodel.model.element;

import java.io.IOException;
import java.io.Writer;

import com.knapptech.jmodel.model.GenericUsage;
import com.knapptech.jmodel.model.MBDocComment;
import com.knapptech.jmodel.model.MBFieldInitializer;
import com.knapptech.jmodel.model.MBImport;
import com.knapptech.jmodel.model.ProgrammingLanguage;
import com.knapptech.jmodel.model.Visibility;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.MBArrayType;
import com.knapptech.jmodel.model.type.delayed.IDelayedType;
import com.knapptech.jmodel.model.type.np.IHasGenericParameters;
import com.knapptech.jmodel.model.type.np.MBClass;
import com.knapptech.jmodel.model.type.np.MBInterface;
import com.knapptech.jmodel.model.type.np.MBNonPrimitiveType;

public class MBField extends MBElement {
	
	private IType type;
	private MBFieldInitializer initializer;
	private MBDocComment comment = null;
	
	public MBField(String name, MBNonPrimitiveType owner,IType type) {
		super(name, owner);
		if (type == null) {
			throw new NullPointerException("Must specify a type.");
		}
		this.type = type;
		owner.addField(this);
	}
	
	public IType getType() {
		return type;
	}
	
	public void addInitializer(MBFieldInitializer initializer) {
		if (this.initializer != null)
			throw new IllegalStateException("Already has an initializer.");
		this.initializer = initializer;
	}
	
	public MBFieldInitializer getInitializer() {
		return initializer;
	}
	
	public String getNameFieldCase() {
		String fgn = getName();
		if (Character.isUpperCase(fgn.charAt(0))) {
			fgn = fgn.substring(0,1).toLowerCase()+fgn.substring(1);
		}
		return fgn;
	}
	
	public String getNameClassCase() {
		String fgn = getName();
		if (Character.isLowerCase(fgn.charAt(0))) {
			fgn = fgn.substring(0,1).toUpperCase()+fgn.substring(1);
		}
		return fgn;
	}
	
	public String toString() {
		return name;
	}

	public void refreshType() {
		if (type instanceof IDelayedType) {
			IDelayedType t = (IDelayedType)type;
			if (t.isFound())
				this.type = t.getFoundType();
		}
		if (type instanceof IHasGenericParameters) {
			((IHasGenericParameters)type).refreshTypes();
		}
	}

	public int write(Writer writer, int currentIndent,ProgrammingLanguage language) throws IOException {
		if (language == ProgrammingLanguage.JAVA) {
			return writeAsJava(writer,currentIndent);
		} else if (language == ProgrammingLanguage.JAVASCRIPT) {
			throw new UnsupportedOperationException("Can only code in java presently.");
		} else if (language == ProgrammingLanguage.CSHARP) {
			throw new UnsupportedOperationException("Can only code in java presently.");
		} else if (language == ProgrammingLanguage.JAVA) {
			throw new UnsupportedOperationException("Can only code in java presently.");
		}
		return 0;
	}

	private int writeAsJava(Writer writer, int currentIndent) throws IOException {
		String txt = "";
		if (this.comment != null)
			this.comment.write(writer, currentIndent, ProgrammingLanguage.JAVA);
		for (int i = 0;i<currentIndent;i++) {
			txt+="\t";
		}
		if (this.owner instanceof MBClass) {
			if (this.modifiers.getVisibility()!=Visibility.PACKAGEPRIVATE)
				txt+=this.modifiers.getVisibility().toString()+" ";
			if (this.modifiers.isStatic())
				txt+="static ";
			if (this.modifiers.isFinal())
				txt+="final ";
			if (this.modifiers.isTransient()) 
				txt+="transient ";
			if (this.modifiers.isVolatile())
				txt+="volatile ";
		} else if (this.owner instanceof MBInterface) {
			// must be static and final.
			if (!this.modifiers.isStatic()) 
				return currentIndent; // not writing.
			if (!this.modifiers.isFinal())
				return currentIndent;
			if (this.modifiers.getVisibility()!=Visibility.PUBLIC)
				return currentIndent;
		}
		if (type instanceof IHasGenericParameters) {
			// there are generics already specified for this object.
			txt+=((IHasGenericParameters)type).getGenericName(GenericUsage.FIELD,this.getOwner());
		} else {
			txt+=type.getLocalName();
		}
		if (type.isArray()) {
			if (type instanceof IDelayedType && ((IDelayedType)type).isFound()) {
				type = ((IDelayedType)type).getFoundType();
			}
			MBArrayType art = (MBArrayType)type;
			for (int arin=0;arin<art.getDimensions();arin++) {
				txt+="[]";
			}
		}
		txt+=" "+name;
		if (this.initializer != null) {
			txt+=" = ";
			txt+=initializer.getText();
		}
		if (!txt.contains(";")) {
			txt+=";";
		}
		if (!txt.endsWith("\n")) {
			txt+="\n";
		}
		writer.write(txt);
		return currentIndent;
	}

	public void createImports() {
		MBNonPrimitiveType t = owner;
		while (t.getOwnerClass() != null)
			t = t.getOwnerClass();
		MBImport.create(this.getType().getPathMapping(), t);
		// must consider generic parameters necessary to import too.
		if (this.getType() instanceof IHasGenericParameters) {
			((IHasGenericParameters)this.getType()).createImports(t);
		}
		if (initializer != null)
			initializer.createImports(t);
	}
	
	public void setComment(String comment) {
		this.comment = new MBDocComment(comment);
	}
}