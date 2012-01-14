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
package com.knapptech.jmodel.model.type.parametered;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.knapptech.jmodel.model.GenericUsage;
import com.knapptech.jmodel.model.MBGenericParameter;
import com.knapptech.jmodel.model.MBImport;
import com.knapptech.jmodel.model.MBPath;
import com.knapptech.jmodel.model.MBPathMapping;
import com.knapptech.jmodel.model.ProgrammingLanguage;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.delayed.IDelayedType;
import com.knapptech.jmodel.model.type.np.IHasGenericParameters;
import com.knapptech.jmodel.model.type.np.MBClassWithParameters;
import com.knapptech.jmodel.model.type.np.MBInterfaceWithParameters;
import com.knapptech.jmodel.model.type.np.MBNonPrimitiveType;

public abstract class MBParameteredType implements IParameteredType {
	protected List<TypePair> types = new ArrayList<TypePair>();
	protected IHasGenericParameters type;
	
	protected void init() {
		if (type == null)
			throw new IllegalStateException("Calling init when type is not set.");
		for (MBGenericParameter gp : type.getParameters()) {
			TypePair tp = new TypePair();
			tp.setRepresentation(gp.getRepresentation());
			this.types.add(tp);
		}
	}
	
	public IType getType(String representation) {
		TypePair tp = getPair(representation);
		if (tp == null)
			return null;
		return tp.getType();
	}
	
	public void setType(String representation,IType type) {
		TypePair tp = getPair(representation);
		if (tp == null){
			tp = new TypePair();
			tp.setRepresentation(representation);
		}
		tp.setType(type);
	}
	
	public void setType(int index,IType type) {
		if (types.size()<1) {
			init();
			if (types.size()>0)
				System.err.println("Types was not initialized first.");
		}
		if (index<0)
			throw new IndexOutOfBoundsException("Index can be zero or greater.");
		if (index>=types.size()) {
			throw new IndexOutOfBoundsException("Max index "+types.size()+", given "+index);
		}
		TypePair tp = getPair(index);
		if (tp == null)
			throw new IndexOutOfBoundsException("The index "+index+" does not have a generic parameter.");
		tp.setType(type);
	}
	
	private TypePair getPair(String representation) {
		if (representation == null || representation.length()<1)
			return null;
		for (TypePair tp : types) {
			if (tp.getRepresentation().equals(representation))
				return tp;
		}
		return null;
	}
	
	private TypePair getPair(int index) {
		return types.get(index);
	}
	
	public boolean isIterable() {
		return type.isIterable();
	}
	public boolean isAbstract() {
		return type.isAbstract();
	}
	public boolean isFinal() {
		return type.isFinal();
	}
	public boolean isComparable() {
		return type.isComparable();
	}
	public boolean isClonable() {
		return type.isClonable();
	}
	public boolean isSerializable() {
		return type.isSerializable();
	}
	public IType comparesTo() {
		return type.comparesTo();
	}
	public Collection<MBImport> getRequiredImports() {
		return type.getRequiredImports();
	}
	public String getNonCapitalName() {
		return type.getNonCapitalName();
	}
	public boolean isArray() {
		return type.isArray();
	}
	public boolean isException() {
		return type.isException();
	}
	public boolean isInterface() {
		return type.isInterface();
	}
	public boolean isPrimitive() {
		return type.isPrimitive();
	}
	public boolean isPrimitiveWrapper() {
		return type.isPrimitiveWrapper();
	}
	public boolean isEnum() {
		return type.isEnum();
	}
	public void refreshTypes() {
		if (type instanceof IDelayedType) {
			IDelayedType dt = (IDelayedType)type;
			if (dt.isFound()) {
				this.type=(IHasGenericParameters) dt.getFoundType();
			}
		}
		for (TypePair tp : this.types) {
			IType tpp = tp.getType();
			if (tpp instanceof IDelayedType) {
				IDelayedType dt = (IDelayedType)tpp;
				if (dt.isFound()) {
					tp.setType((IHasGenericParameters) dt.getFoundType());
				}
			}
		}
		type.refreshTypes();
	}
	public MBPath getPath() {
		return type.getPath();
	}
	public MBPathMapping getPathMapping() {
		return type.getPathMapping();
	}
	public String getLocalName() {
		return type.getLocalName();
	}
	public String getFullyQualifiedName() {
		return type.getFullyQualifiedName();
	}
	public boolean isPackage() {
		return false;
	}
	public int compareTo(IType o) {
		return type.compareTo(o);
	}
	public int write(Writer writer, int currentIndent,
			ProgrammingLanguage language) throws IOException {
		return type.write(writer, currentIndent, language);
	}
	public int sizeParameters() {
		return type.sizeParameters();
	}
	public List<MBGenericParameter> getParameters() {
		return type.getParameters();
	}
	public MBGenericParameter getParameter(String representation) {
		return type.getParameter(representation);
	}
	public MBGenericParameter getParameter(int index) {
		return type.getParameter(index);
	}
	public void addGenericParameter(MBGenericParameter parameter) {
		type.addGenericParameter(parameter);
	}
	public boolean hasGenericParameter(String representation) {
		return type.hasGenericParameter(representation);
	}
	public String getGenericName(GenericUsage usage,IType typeUsing) {
		return getLocalName()+getGenericPart(usage,typeUsing);
	}
	
	public String getGenericPart(GenericUsage usage,IType typeUsing) {
		if (types.size()<1)
			return "";
		String s = "<";
		for (int i = 0;i<types.size();i++) {
			TypePair typePair = types.get(i);
			MBGenericParameter parm = this.type.getParameter(i);
			IType t = typePair.getType();
			String r = typePair.getRepresentation();
			List<IType> ub = parm.getUpperBounds();
			if (parm.getOwner().equals(typeUsing)) {
				// local usage.
				if (t instanceof IHasGenericParameters) {
					s+=((IHasGenericParameters)t).getGenericName(usage, typeUsing);
				} else {
					if (usage != GenericUsage.CLASSDECLARATION) {
						if (t.isAbstract() || t.isInterface()) {
							s+="? extends ";
						}
					}
					s+=t.getLocalName();
				}
			} else {
				if (t instanceof IHasGenericParameters) {
					s+=((IHasGenericParameters)t).getGenericName(usage, typeUsing);
				} else {
					if (usage != GenericUsage.CLASSDECLARATION) {
						if (t.isAbstract() || t.isInterface()) {
							s+="? extends ";
						}
					}
					s+=t.getLocalName();
				}
			}
			if (i<types.size()-1)
				s+=",";
		}
		s+=">";
		return s;
	}
	
	private static class TypePair {
		private IType type;
		private String representation;
		public TypePair() {}
		public IType getType() {
			return type;
		}
		public void setType(IType type) {
			this.type = type;
		}
		public String getRepresentation() {
			return representation;
		}
		public void setRepresentation(String representation) {
			this.representation = representation;
		}
	}

	public IHasGenericParameters getTypeWithGenericParameters() {
		return this.type;
	}
	
	public void createImports(MBNonPrimitiveType owner) {
		MBImport.create(this.type.getPathMapping(), owner);
		for (TypePair tp : this.types){
			MBImport.create(tp.getType().getPathMapping(),owner);
		}
//		if (this.getType() instanceof IParameteredType) {
//			IParameteredType pt = (IParameteredType)this.getType();
//			for (MBGenericParameter gp : pt.getParameters()) {
//				MBImport.create(pt.getType(gp.getRepresentation()).getPathMapping(), t);
//			}
//		} else {
//			IHasGenericParameters hgp = (IHasGenericParameters)this.getType();
//		}
	}

	public void print(int i) {
		if (type instanceof MBClassWithParameters) {
			((MBClassWithParameters)type).print(i);
		} else if (type instanceof MBInterfaceWithParameters) {
			((MBInterfaceWithParameters)type).print(i);
		} else {
			System.err.println("UNKNOWN PARAMETERED TYPE!");
		}
	}
}