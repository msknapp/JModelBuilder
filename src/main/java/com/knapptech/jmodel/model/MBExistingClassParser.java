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

import java.lang.reflect.TypeVariable;

import com.knapptech.jmodel.model.type.MBArrayType;
import com.knapptech.jmodel.model.type.MBPrimitiveType;
import com.knapptech.jmodel.model.type.MBPrimitiveWrapper;
import com.knapptech.jmodel.model.type.MBType;
import com.knapptech.jmodel.model.type.np.MBClass;
import com.knapptech.jmodel.model.type.np.MBClassWithParameters;
import com.knapptech.jmodel.model.type.np.MBEnum;
import com.knapptech.jmodel.model.type.np.MBInterface;
import com.knapptech.jmodel.model.type.np.MBInterfaceWithParameters;

public class MBExistingClassParser {
	
	public static final MBType parse(String fullyQualifiedName) {
		MBPrimitiveType.init();
		MBPrimitiveWrapper.init();
		MBInterface.init();
		MBClass.init();
		MBInterfaceWithParameters.init();
		MBClassWithParameters.init();
		if (!fullyQualifiedName.contains(".")) {
			MBPrimitiveType pt = MBPrimitiveType.parse(fullyQualifiedName);
			if (pt != null)
				return pt;
		}
		int arrayDimensions = 0;
		int index = 0;
		while (index>=0) {
			index = fullyQualifiedName.indexOf("[",index+1);
			if (index>0)
				arrayDimensions++;
		}
		fullyQualifiedName = fullyQualifiedName.replace("[]", "");
		fullyQualifiedName = fullyQualifiedName.trim();
		String[] nameParts = fullyQualifiedName.split("\\.");
		
		MBPath path = MBPath.createItemPath(nameParts);
		MBPathMapping mpng = MBPathMapping.getOrCreate(path);
		IPathItem itm = mpng.getPathElement();
		if (itm != null)
			return (MBType)itm;
		// must create it.
		
		// we only worry about packages, classes existing, their
		// inheritance hierarchy, and their implemented interfaces.
		// we do not worry about having all the class members
		// built correctly.
		try {
			
			Class c = ClassLoader.getSystemClassLoader().loadClass(fullyQualifiedName);
			if (c != null) {
				MBType t = parse(c);
				if (arrayDimensions>0)
					return new MBArrayType(t,(byte) arrayDimensions);
				return t;
			}
		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
			// not a problem, the type is being built.
		}
		return null;
	}
	
	public static final MBType parse(Class clazz) {
		if (clazz == null)
			throw new NullPointerException("Must have non-null class to parse.");
		String[] parts = clazz.getName().split("\\.");
		MBPath path = MBPath.createItemPath(parts);
		MBPathMapping mpng = MBPathMapping.get(path);
		String ln = parts[parts.length-1];
		if (mpng != null && mpng.getElement() != null) {
			return (MBType)mpng.getElement();
		}
		if (clazz.isInterface()) {
			// we know it's an interface.
			MBPackage pkg = MBPackage.DEFAULT;
			if (path.getFullyQualifiedName().length()>0)
				pkg =  MBPackage.getOrCreate(path.createSuperPath());
			TypeVariable[] tvs = clazz.getTypeParameters();
			MBInterface mbi = null;
			if (tvs == null || tvs.length<1 || tvs[0]==null) {
				mbi = MBInterface.getOrCreate(ln,pkg);
			} else {
				mbi = MBInterfaceWithParameters.getOrCreate(ln,pkg);
				for (TypeVariable tv : tvs)  {
					// TODO have it support upper and lower bounds.
					MBGenericParameter.getOrCreate((MBInterfaceWithParameters)mbi, tv.getName(), MBClass.OBJECT);
				}
			}
			// might have super interfaces:
			Class[] classInterfaces = clazz.getInterfaces();
			for (Class cli : classInterfaces) {
				MBInterface clt = (MBInterface)parse(cli);
				mbi.addImplementedInterface(clt);
			}
			return mbi;
		}
		if (clazz.isEnum()) {
			MBEnum mbi = MBEnum.getOrCreate(parts[parts.length-1], MBPackage.getOrCreate(path.createSuperPath()));

			Class[] classInterfaces = clazz.getInterfaces();
			for (Class cli : classInterfaces) {
				MBInterface clt = (MBInterface)parse(cli);
				mbi.addImplementedInterface(clt);
			}
			return mbi;
		}
		Class classSuper = clazz.getSuperclass();
		MBClass spr = MBClass.OBJECT;
		spr = (MBClass)parse(classSuper);
		TypeVariable[] tvs = clazz.getTypeParameters();
		MBClass mbc = null;
		if (tvs == null || tvs.length<1 || tvs[0]==null) {
			mbc = MBClass.getOrCreate(ln, spr,MBPackage.getOrCreate(path.createSuperPath()));
		} else {
			mbc = MBClassWithParameters.getOrCreate(ln,spr,MBPackage.getOrCreate(path.createSuperPath()));
			for (TypeVariable tv : tvs)  {
				// TODO have it support upper and lower bounds.
				MBGenericParameter.getOrCreate((MBClassWithParameters)mbc, tv.getName(), MBClass.OBJECT);
			}
		}
		Class[] classInterfaces = clazz.getInterfaces();
		for (Class cli : classInterfaces) {
			MBInterface clt = (MBInterface)parse(cli);
			mbc.addImplementedInterface(clt);
		}
		return mbc;
	}
}