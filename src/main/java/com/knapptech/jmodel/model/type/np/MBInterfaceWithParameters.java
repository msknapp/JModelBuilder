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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.knapptech.jmodel.model.GenericUsage;
import com.knapptech.jmodel.model.MBGenericParameter;
import com.knapptech.jmodel.model.MBImport;
import com.knapptech.jmodel.model.MBPackage;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.parametered.MBParameteredInterface;

public class MBInterfaceWithParameters extends MBInterface implements IHasGenericParameters {
	public static final MBInterfaceWithParameters COMPARABLE = getOrCreate("Comparable",MBPackage.JAVALANG);
	public static final MBInterfaceWithParameters COLLECTION = getOrCreate("Collection",MBPackage.JAVAUTIL);
	public static final MBInterfaceWithParameters MAP = getOrCreate("Map",MBPackage.JAVAUTIL);
	public static final MBInterfaceWithParameters SET = getOrCreate("Set",MBPackage.JAVAUTIL);
	public static final MBInterfaceWithParameters NAVIGABLESET = getOrCreate("NavigableSet",MBPackage.JAVAUTIL);
	public static final MBInterfaceWithParameters SORTEDMAP = getOrCreate("SortedMap",MBPackage.JAVAUTIL);
	public static final MBInterfaceWithParameters SORTEDSET = getOrCreate("SortedSet",MBPackage.JAVAUTIL);
	public static final MBInterfaceWithParameters LIST = getOrCreate("List",MBPackage.JAVAUTIL);
	public static final MBInterfaceWithParameters ITERABLE = getOrCreate("Iterable",MBPackage.JAVALANG);
	
	static {
		COLLECTION.implementedInterfaces.put(ITERABLE.path, ITERABLE);
		MBGenericParameter.getOrCreate(COLLECTION, "E", MBClass.OBJECT);
		SET.implementedInterfaces.put(COLLECTION.path, COLLECTION);
		MBGenericParameter.getOrCreate(SET, "E", MBClass.OBJECT);
		LIST.implementedInterfaces.put(COLLECTION.path, COLLECTION);
		MBGenericParameter.getOrCreate(LIST, "E", MBClass.OBJECT);
		SORTEDSET.implementedInterfaces.put(COLLECTION.path, COLLECTION);
		MBGenericParameter.getOrCreate(SORTEDSET, "E", MBClass.OBJECT);
		MBGenericParameter.getOrCreate(COMPARABLE, "E", MBClass.OBJECT);
		MBGenericParameter.getOrCreate(ITERABLE, "E", MBClass.OBJECT);
		MBGenericParameter.getOrCreate(MAP, "K", MBClass.OBJECT);
		MBGenericParameter.getOrCreate(MAP, "V", MBClass.OBJECT);
		MBGenericParameter.getOrCreate(SORTEDMAP, "K", MBClass.OBJECT);
		MBGenericParameter.getOrCreate(SORTEDMAP, "V", MBClass.OBJECT);
		MBGenericParameter.getOrCreate(NAVIGABLESET, "E", MBClass.OBJECT);
	}
	
	public static MBParameteredInterface createComparableTo(IType t) {
		return MBParameteredInterface.createComparableTo(t);
	}

	private List<MBGenericParameter> parameters = new ArrayList<MBGenericParameter>();
	
	protected MBInterfaceWithParameters(String localName,MBPackage ownerPackage) {
		super(localName,ownerPackage);
	}
	
	public static final MBInterfaceWithParameters getOrCreate(String localName,MBPackage ownerPackage) {
		if (localName.contains("."))
			throw new IllegalArgumentException("local name cannot contain a period.");
		MBInterface mbie = ownerPackage.getInterface(localName);
		if (mbie != null) {
			if (!(mbie instanceof MBInterfaceWithParameters)) {
				throw new IllegalArgumentException("An interface exists with that name, but it does not accept parameters.");
			}
			return (MBInterfaceWithParameters)mbie;
		}
		MBInterfaceWithParameters mbi = new MBInterfaceWithParameters(localName, ownerPackage);
		mbi.ownerPackage.addInterface(mbi);
		return mbi;
	}
	
	public static final MBInterfaceWithParameters getOrCreate(String localName,IClass ownerClass) {
		if (localName.contains("."))
			throw new IllegalArgumentException("local name cannot contain a period.");
		MBInterface mbie = (MBInterface) ownerClass.getInnerInterface(localName);
		if (mbie != null) {
			if (!(mbie instanceof MBInterfaceWithParameters)) {
				throw new IllegalArgumentException("An interface exists with that name, but it does not accept parameters.");
			}
			return (MBInterfaceWithParameters)mbie;
		}
		MBInterfaceWithParameters mbi = new MBInterfaceWithParameters(localName, ownerClass);
		mbi.ownerPackage.addInterface(mbi);
		return mbi;
	}
	
	protected MBInterfaceWithParameters(String name,
			IClass ownerClass) {
		super(name, ownerClass);
	}

	public int sizeParameters() {
		return parameters.size();
	}

	public List<MBGenericParameter> getParameters() {
		return Collections.unmodifiableList(parameters);
	}
	
	public void addGenericParameter(MBGenericParameter parm) {
		if (parm ==null)
			throw new NullPointerException("Must define the parameter to add.");
		if (hasGenericParameter(parm.getRepresentation()))
			throw new IllegalArgumentException("We already have a generic parameter with that name.");
		this.parameters.add(parm);
	}
	
	public MBGenericParameter getParameter(String representation) {
		for (MBGenericParameter prm : parameters)
			if (prm.getRepresentation().equals(representation))
				return prm;
		return null;
	}
	
	public boolean hasGenericParameter(String representation) {
		return getParameter(representation)!=null;
	}
	public String getGenericName(GenericUsage usage,IType typeUsing) {
		return getLocalName()+getGenericPart(usage,this);
	}
	
	public String getGenericPart(GenericUsage usage,IType typeUsing) {
		if (parameters.size()<1)
			return "";
		String s = "<";
		for (int i = 0;i<parameters.size();i++) {
			MBGenericParameter parm = parameters.get(i);
			List<IType> ub = parm.getUpperBounds();
			if (parm.getOwner().equals(typeUsing)) {
				// local usage.
				s+= parm.getRepresentation();
			} else {
				s+="?";
				IType tub = (ub.size() > 0 ? ub.get(0) : MBClass.OBJECT);
				if (tub != MBClass.OBJECT) {
					s+=" extends ";
					if (tub instanceof IHasGenericParameters) {
						s+=((IHasGenericParameters)tub).getGenericName(usage,typeUsing);
					} else {
						s+=tub.getLocalName();
					}
				}
			}
			if (i<parameters.size()-1)
				s+=",";
		}
		s+=">";
		return s;
	}

	public MBGenericParameter getParameter(int index) {
		return parameters.get(index);
	}
	
	public void createImports(MBNonPrimitiveType owner) {
		MBImport.create(this.getPathMapping(), owner);
		for (MBGenericParameter gp : this.parameters){
			for (IType ubt : gp.getUpperBounds()) {
				MBImport.create(ubt.getPathMapping(),owner);
			}
			for (IType ubt : gp.getLowerBounds()) {
				MBImport.create(ubt.getPathMapping(),owner);
			}
		}
	}
}