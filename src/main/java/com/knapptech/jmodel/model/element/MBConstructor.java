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
import com.knapptech.jmodel.model.MBImport;
import com.knapptech.jmodel.model.MBParameter;
import com.knapptech.jmodel.model.MBSourceCode;
import com.knapptech.jmodel.model.ProgrammingLanguage;
import com.knapptech.jmodel.model.Visibility;
import com.knapptech.jmodel.model.type.MBArrayType;
import com.knapptech.jmodel.model.type.np.IHasGenericParameters;
import com.knapptech.jmodel.model.type.np.MBClass;
import com.knapptech.jmodel.model.type.np.MBException;
import com.knapptech.jmodel.model.type.np.MBInterface;
import com.knapptech.jmodel.model.type.np.MBNonPrimitiveType;

public class MBConstructor extends MBElement {
	private List<MBParameter> parameters = new ArrayList<MBParameter>();
	private MBSourceCode sourceCode = new MBSourceCode();
	
	private MBConstructor(MBClass owner) {
		super(owner.getLocalName(), owner);
	}
	
	private MBConstructor(MBClass owner,MBParameter... parameters) {
		super(owner.getLocalName(), owner);
		for (MBParameter p : parameters) {
			this.parameters.add(p);
		}
	}

	public static MBConstructor getOrCreate(MBClass clazz) {
		MBConstructor c = clazz.getConstructor();
		if (c != null) 
			return c;
		c = new MBConstructor(clazz);
		clazz.addConstructor(c);
		return c;
	}

	public static MBConstructor getOrCreate(MBClass clazz, MBParameter... parms) {
		MBConstructor c = clazz.getConstructor(parms);
		if (c != null) 
			return c;
		c = new MBConstructor(clazz,parms);
		clazz.addConstructor(c);
		return c;
	}
	
	public List<MBParameter> getParameters() {
		return parameters;
	}
	
	public MBSourceCode getSourceCode() {
		return sourceCode;
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
		if (!(obj instanceof MBConstructor))
			return false;
		MBConstructor other = (MBConstructor) obj;
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
		int ind = 0;
		String tb = "";
		while (ind++<currentIndent)
			tb+="\t";
		if (this.owner instanceof MBInterface) {
			return currentIndent;
		} else {
			writer.write(tb);
			if (this.modifiers.getVisibility()!=Visibility.PACKAGEPRIVATE)
				writer.write(this.modifiers.getVisibility().toString()+" ");
		}
		writer.write(this.getName()+"(");
		
		// parameters
		for (int prmi = 0;prmi<this.getParameters().size();prmi++) {
			MBParameter prm = this.getParameters().get(prmi);
			if (prm.isFinal())
				writer.write("final ");
			if (prm.getType() instanceof IHasGenericParameters) {
				IHasGenericParameters hgp = (IHasGenericParameters)prm.getType();
				writer.write(hgp.getGenericName(GenericUsage.METHODPARAMETER, owner));
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
		writer.write("{\n");
		this.getSourceCode().write(writer, currentIndent+1, ProgrammingLanguage.JAVA);
		writer.write(tb+"}\n");
		return currentIndent;
	}

	public void createImports() {
		MBNonPrimitiveType t = owner;
		while (t.getOwnerClass() != null)
			t = t.getOwnerClass();
		for (MBParameter prm : this.getParameters()) {
			MBImport.create(prm.getType().getPathMapping(), t);
		}
		this.getSourceCode().createImports(t);
	}
}