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
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.knapptech.jmodel.model.GenericUsage;
import com.knapptech.jmodel.model.MBDocComment;
import com.knapptech.jmodel.model.MBImport;
import com.knapptech.jmodel.model.MBMethodSignature;
import com.knapptech.jmodel.model.MBModifiers;
import com.knapptech.jmodel.model.MBParameter;
import com.knapptech.jmodel.model.MBSourceCode;
import com.knapptech.jmodel.model.ProgrammingLanguage;
import com.knapptech.jmodel.model.Visibility;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.MBArrayType;
import com.knapptech.jmodel.model.type.delayed.IDelayedType;
import com.knapptech.jmodel.model.type.np.IHasGenericParameters;
import com.knapptech.jmodel.model.type.np.MBException;
import com.knapptech.jmodel.model.type.np.MBInterface;
import com.knapptech.jmodel.model.type.np.MBNonPrimitiveType;

public class MBMethod extends MBElement {
	private List<MBParameter> parameters = new ArrayList<MBParameter>();
	private IType returnType;
	private MBSourceCode sourceCode = new MBSourceCode();
	private MBDocComment comment = null;

	public MBMethod(String name, MBNonPrimitiveType owner,IType returnType) {
		super(name, owner);
		this.returnType = returnType;
		owner.addMethod(this);
	}

	public MBMethod(String name, MBNonPrimitiveType owner,IType returnType,MBParameter... parameters) {
		super(name, owner);
		this.returnType = returnType;
		for (MBParameter p : parameters) {
			this.parameters.add(p);
		}
		owner.addMethod(this);
	}

	public MBMethod(MBNonPrimitiveType owner,MBMethod method) {
		super(method.getName(), owner);
		for (MBParameter prm : method.getParameters()) {
			this.parameters.add(prm);
		}
		this.returnType = method.getReturnType();
		owner.addMethod(this);
		this.modifiers = new MBModifiers(method.getModifiers());
		if (!owner.getModifiers().isAbstract())
			this.modifiers.setAbstract(false);
		this.sourceCode = new MBSourceCode(method.getSourceCode());
	}
	
	public void setComment(String comment) {
		this.comment = new MBDocComment(comment);
	}
	
	public IType getReturnType() {
		return returnType;
	}
	public void setReturnType(IType returnType) {
		this.returnType = returnType;
	}
	
	public List<MBParameter> getParameters() {
		return parameters;
	}
	
	public MBSourceCode getSourceCode() {
		return sourceCode;
	}
	
	public void setSourceCode(MBSourceCode sourceCode) {
		if (sourceCode == null) 
			throw new NullPointerException("Null source code is not allowed.");
		this.sourceCode = sourceCode;
	}

	@Override
	public int hashCode() {
		int phc = 0;
		for (int i = 0;i<parameters.size();i++) {
			phc = parameters.get(i).getType().hashCode();
		}
		return owner.hashCode()+name.hashCode()+phc;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof MBMethod))
			return false;
		MBMethod other = (MBMethod) obj;
		if (!this.owner.equals(other.owner))
			return false;
		if (!this.name.equals(other.name))
			return false;
		if (other.parameters.size() != parameters.size())
			return false;
		for (int i = 0;i<parameters.size();i++) {
			if (!parameters.get(i).getType().equals(other.parameters.get(i).getType()))
				return false;
		}
		return true;
	}

	public void refreshTypes() {
		if (returnType instanceof IDelayedType) {
			IDelayedType t = (IDelayedType)returnType;
			if (t.isFound())
				this.returnType = t.getFoundType();
		}
		for (MBParameter prm : parameters) {
			prm.refreshType();
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
		refreshTypes();
		if (this.comment != null)
			this.comment.write(writer, currentIndent, ProgrammingLanguage.JAVA);
		int ind = 0;
		String tb = "";
		while (ind++<currentIndent)
			tb+="\t";
		if (this.owner instanceof MBInterface) {
			if (modifiers.getVisibility()!= Visibility.PUBLIC)
				return currentIndent;
			if (modifiers.isStatic())
				return currentIndent;
			if (modifiers.isFinal())
				return currentIndent;
			writer.write(tb);
		} else {
			writer.write(tb);
			if (this.modifiers.getVisibility()!=Visibility.PACKAGEPRIVATE)
				writer.write(this.modifiers.getVisibility().toString()+" ");
			if (this.modifiers.isStatic())
				writer.write("static ");
			if (this.modifiers.isFinal())
				writer.write("final ");
			if (this.modifiers.isAbstract() && this.owner.getModifiers().isAbstract())
				writer.write("abstract ");
			if (this.modifiers.isSynchronized())
				writer.write("synchronized ");
		}
		if (this.getReturnType() == null) {
			writer.write("void ");
		} else {
			if (this.getReturnType() instanceof IHasGenericParameters) {
				writer.write(((IHasGenericParameters)this.getReturnType()).getGenericName(GenericUsage.METHODRETURN, this.getOwner()));
			} else {
				writer.write(this.getReturnType().getLocalName());
			}
			if (this.getReturnType().isArray()) {
				if (this.getReturnType() instanceof IDelayedType && ((IDelayedType)this.getReturnType()).isFound()) {
					this.setReturnType(((IDelayedType)this.getReturnType()).getFoundType());
				}
				MBArrayType art = (MBArrayType)this.getReturnType();
				for (int arin=0;arin<art.getDimensions();arin++) {
					writer.write("[]");
				}
			}
			writer.write(" ");
		}
		writer.write(this.name+"(");
		// parameters
		for (int prmi = 0;prmi<this.getParameters().size();prmi++) {
			MBParameter prm = this.getParameters().get(prmi);
			if (prm.isFinal())
				writer.write("final ");
			if (prm.getType() instanceof IHasGenericParameters) {
				IHasGenericParameters hgp = (IHasGenericParameters)prm.getType();
				writer.write(hgp.getGenericName(GenericUsage.METHODPARAMETER,this.getOwner()));
			} else {
				writer.write(prm.getType().getLocalName());
			}
			if (prm.getType().isArray()) {
				MBArrayType art = (MBArrayType)prm.getType();
				for (int j = 0;j<art.getDimensions();j++) {
					writer.write("[]");
				}
			}
			if (prm.isVarargs())
				writer.write("...");
			writer.write(" "+prm.getName());
			if (prmi<this.getParameters().size()-1)
				writer.write(", ");
		}
		
		writer.write(") ");
		// exceptions 
		Set<MBException> xs = this.getSourceCode().getExceptions();
		if (xs.size()>0) {
			writer.write("throws ");
			int i  =0;
			int sz=xs.size();
			for (MBException x : xs) {
				writer.write(x.getLocalName());
				i++;
				if (i == sz)
					writer.write(", ");
				else 
					writer.write(" ");
			}
		}
		if (this.owner instanceof MBInterface || modifiers.isAbstract()) {
			writer.write(";\n");
			return currentIndent;
		}
		// must be a class.
		writer.write("{\n");
		this.getSourceCode().write(writer, currentIndent+1, ProgrammingLanguage.JAVA);
		writer.write(tb+"}\n");
		return currentIndent;
	}

	public void createImports() {
		MBNonPrimitiveType t = owner;
		while (t.getOwnerClass() != null)
			t = t.getOwnerClass();
		if (this.returnType != null) {
			if (this.returnType instanceof IHasGenericParameters) {
				((IHasGenericParameters)this.returnType).createImports(t);
			} else {
				MBImport.create(this.returnType.getPathMapping(), t);
			}
		}
		for (MBParameter prm : this.getParameters()) {
			if (prm.getType() instanceof IHasGenericParameters) {
				((IHasGenericParameters)prm.getType()).createImports(t);
			} else {
				MBImport.create(prm.getType().getPathMapping(),t);
			}
		}
		this.getSourceCode().createImports(t);
	}

	public Set<MBException> getExceptions() {
		return this.sourceCode.getExceptions();
	}
	
	public MBMethodSignature createSignature() {
		return new MBMethodSignature(this);
	}
}