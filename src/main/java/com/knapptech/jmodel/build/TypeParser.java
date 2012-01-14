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
package com.knapptech.jmodel.build;

import org.w3c.dom.Element;

import com.knapptech.jmodel.model.MBExistingClassParser;
import com.knapptech.jmodel.model.MBPath;
import com.knapptech.jmodel.model.MBPathMapping;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.MBArrayType;
import com.knapptech.jmodel.model.type.MBPrimitiveType;
import com.knapptech.jmodel.model.type.MBPrimitiveWrapper;
import com.knapptech.jmodel.model.type.delayed.IDelayedType;
import com.knapptech.jmodel.model.type.delayed.MBDelayedClassType;
import com.knapptech.jmodel.model.type.delayed.MBDelayedInterfaceType;
import com.knapptech.jmodel.model.type.delayed.MBDelayedType;
import com.knapptech.jmodel.model.type.np.IHasGenericParameters;
import com.knapptech.jmodel.model.type.np.MBClass;
import com.knapptech.jmodel.model.type.np.MBClassWithParameters;
import com.knapptech.jmodel.model.type.np.MBInterface;
import com.knapptech.jmodel.model.type.np.MBInterfaceWithParameters;
import com.knapptech.jmodel.model.type.parametered.IParameteredType;
import com.knapptech.jmodel.model.type.parametered.MBParameteredClass;
import com.knapptech.jmodel.model.type.parametered.MBParameteredInterface;

public class TypeParser {
	
	static {
		MBPrimitiveType.init();
		MBPrimitiveWrapper.init();
		MBInterface.init();
		MBClass.init();
		MBInterfaceWithParameters.init();
		MBClassWithParameters.init();
	}
	
	private static PossibleType possibleType = PossibleType.CLASS;
	
	public static IType parse(String type,boolean array) {
		return parse(type,(array ? (byte)1 : (byte)0));//,false);
	}
	
	public static IType parse(Element item) {
		if (item.getNodeName().equals("enum"))
			possibleType = PossibleType.ENUM;
		else if (item.getNodeName().equals("interface"))
			possibleType = PossibleType.INTERFACE;
		else if (item.getNodeName().equals("implements"))
			possibleType = PossibleType.INTERFACE;
		else 
			possibleType = PossibleType.CLASS;
		String tp = item.getAttribute("type");
		int arrayDimensions = 0;
		if (tp == null || tp.length()<1)
			tp = XMLHelper.getFirstSubElementsInnerText(item, "type");
		String ad = item.getAttribute("arrayDimensions");
		if (ad != null && ad.length()>0) {
			arrayDimensions = Integer.parseInt(ad);
		}
		IType t = null;
		if (tp != null && tp.length()>0) {
			t = MBExistingClassParser.parse(tp);
		}
		if (t == null) 
			t = parseBuildingType(tp);//,hasGenericParameters);
		
		
		if (t instanceof IHasGenericParameters) {
			IParameteredType pt = null;
			if (t instanceof MBClassWithParameters) {
				pt = new MBParameteredClass((MBClassWithParameters)t);
			} else if (t instanceof MBInterfaceWithParameters) {
				pt = new MBParameteredInterface((MBInterfaceWithParameters)t);
			} else {
				throw new RuntimeException("Unknown type: "+t.getClass());
			}
			int i = 0;
			for (Element en : XMLHelper.getImmediateSubElements(item, "generictype")) {
				if (i>=pt.sizeParameters())
					throw new IndexOutOfBoundsException("You have defined more generic parameters than the type can take.\n"+
							(i+1)+">"+pt.sizeParameters());
				pt.setType(i,parse(en));
				i++;
			}
			t = pt;
		}
		if (t == null) 
			return null;
		if (arrayDimensions>0)
			return new MBArrayType(t, (byte)arrayDimensions);
		return t;
	}
	
	public static IType parse(String type,byte arrayDimensions){//,boolean hasGenericParameters) {
		if (type != null && type.length()>0) {
			IType t = MBExistingClassParser.parse(type);
			if (t != null) {
				if (arrayDimensions>0)
					return new MBArrayType(t, (byte)arrayDimensions);
				return t;
			}
			t = parseBuildingType(type);//,hasGenericParameters);
			if (t != null) {
				if (arrayDimensions>0)
					return new MBArrayType(t, (byte)arrayDimensions);
				return t;
			}
		}
		assert(false);
		return null;
	}
	
	private static IType parseBuildingType(String fullyQualifiedName){//,boolean hasGenericParameters) {
		String[] parts = fullyQualifiedName.split("\\.");
		// try checking for alternate meanings of the path.
		MBPathMapping mpng = null;
		for (int i = parts.length-1;i>=0;i--) {
			MBPath path = MBPath.createItemPath(i,parts);
			mpng = MBPathMapping.get(path);
			if (mpng != null && mpng.getElement()!=null) {
				Object o = mpng.getElement();
				if (o instanceof IType) 
					return (IType)mpng.getElement();
			}
		}
		mpng =  MBPathMapping.getOrCreate(MBPath.createItemPath(parts));
		
		if (possibleType == PossibleType.CLASS) {
			IDelayedType dt = new MBDelayedClassType(mpng);
			DelayedTypeRegister.add(dt);
			return dt;
		} else if (possibleType == PossibleType.INTERFACE) {
			IDelayedType dt = new MBDelayedInterfaceType(mpng);
			DelayedTypeRegister.add(dt);
			return dt;
		} else {
			IDelayedType dt = new MBDelayedType(mpng);
			DelayedTypeRegister.add(dt);
			return dt;
		}
	}
	
	public static enum PossibleType {
		ENUM,
		CLASS,
		INTERFACE;
	}
}