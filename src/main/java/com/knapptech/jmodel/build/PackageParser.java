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

import com.knapptech.jmodel.model.MBPackage;

public class PackageParser {
	
	public static MBPackage parse(Element item, MBPackage parent,boolean withElements) {
		MBPackage p = parent.getOrCreateSubPackage(item.getAttribute("name"));
		System.out.println("Parsing package "+p.getLocalName()+", with"+(withElements ? "" : "out")+" elements.");
		for (Element pkg : XMLHelper.getImmediateSubElements(item, "package")) {
			PackageParser.parse(pkg,p,withElements);
		}
		for (Element clz : XMLHelper.getImmediateSubElements(item, "class")) {
			if (withElements) {
				ClassParser.buildElements(clz,p);
			} else {
				ClassParser.buildTypes(clz,p);
			}
		}
		for (Element nfc : XMLHelper.getImmediateSubElements(item, "interface")) {
			if (withElements) {
				InterfaceParser.buildElements(nfc,p);
			} else {
				InterfaceParser.buildTypes(nfc,p);
			}
		}
		for (Element enm : XMLHelper.getImmediateSubElements(item, "enum")) {
			if (withElements) {
				EnumParser.buildElements(enm,p);
			} else {
				EnumParser.buildTypes(enm,p);
			}
		}
		return p;
	}
}