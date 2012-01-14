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

import java.util.Collection;
import java.util.HashMap;

import org.w3c.dom.Element;

import com.knapptech.jmodel.beans.MBMethodBean;
import com.knapptech.jmodel.build.strategic.StrategicClassParser;
import com.knapptech.jmodel.model.IPathItem;
import com.knapptech.jmodel.model.MBPackage;
import com.knapptech.jmodel.model.type.np.IClass;
import com.knapptech.jmodel.model.type.np.MBInterface;

public class InterfaceParser {
	private static final HashMap<IPathItem,InterfaceParser> parsers = new HashMap<IPathItem, InterfaceParser>();
	private MBInterface builtInterface;
	
	public static MBInterface buildTypes(Element item, IPathItem pathItem) {
		InterfaceParser p = getOrCreateParser(pathItem);
		return p.instanceBuildTypes(item, pathItem);
	}
	
	public static MBInterface buildElements(Element item, IPathItem pathItem) {
		InterfaceParser p = getOrCreateParser(pathItem);
		return p.instanceBuildElements(item, pathItem);
	}
	
	private static final InterfaceParser getOrCreateParser(IPathItem pathItem) {
		InterfaceParser p = parsers.get(pathItem);
		if (p != null)
			return p;
		p = new InterfaceParser();
		parsers.put(pathItem, p);
		return p;
	}
	
	public MBInterface instanceBuildTypes(Element item, IPathItem p) {
		String snm = item.getAttribute("name");
		System.out.println("Parsing interface "+snm);
		String name = snm;
		if (p instanceof MBPackage) {
			builtInterface = MBInterface.getOrCreate(name, (MBPackage)p);
		} else if (p instanceof IClass) {
			builtInterface = MBInterface.getOrCreate(name, (IClass)p);
		} else {
			throw new IllegalArgumentException("Object must either be a package or class.");
		}
		return builtInterface;
	}
	
	public MBInterface instanceBuildElements(Element item, IPathItem p) {
		System.out.println("Parsing elements of interface "+this.builtInterface.getLocalName());
		Collection<MBMethodBean> requiredMethods = ClassParser.getRequiredMethods(item);
		StrategicClassParser.addRequiredMethods(builtInterface, requiredMethods, true);
		return builtInterface;
	}
}