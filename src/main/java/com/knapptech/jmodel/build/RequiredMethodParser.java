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

import com.knapptech.jmodel.beans.MBMethodBean;
import com.knapptech.jmodel.model.MBParameter;
import com.knapptech.jmodel.model.type.IType;

public class RequiredMethodParser {

	public static MBMethodBean parse(Element fld) {
		IType tt=null;
		
		for (Element rt : XMLHelper.getImmediateSubElements(fld, "returnType")) {
			tt=TypeParser.parse(rt);
			break;
		}
		if (tt == null) {
			String s = fld.getAttribute("returnType");
			if (s != null && s.length()>0) {
				tt = TypeParser.parse(s, false);
			}
		}
		
		MBMethodBean m = null;//
		if (tt != null && tt!= null) {
			m = new MBMethodBean();
			m.setName(fld.getAttribute("name"));
			m.setType(tt);
		} else {
			m = new MBMethodBean();
			m.setName(fld.getAttribute("name"));
		}
		String s = fld.getAttribute("modifiesClass");
		m.setModifiesClass(Boolean.parseBoolean(s));
		String v = fld.getAttribute("visibility");
		if (v != null) {
			if (v.equals("public"))
				m.getModifiers().makePublic();
			else if (v.equals("protected"))
				m.getModifiers().makeProtected();
			else if (v.equals("private"))
				m.getModifiers().makePrivate();
			else if (v.equals("package"))
				m.getModifiers().makePackage();
			else if (v.equals(""))
				m.getModifiers().makePublic();
			else if (v.equals("packageprivate"))
				m.getModifiers().makePackage();
		} else 
			m.getModifiers().makePublic();
		if (Boolean.parseBoolean(fld.getAttribute("static")))
			m.getModifiers().setStatic(true);
		if (Boolean.parseBoolean(fld.getAttribute("abstract")))
			m.getModifiers().setAbstract(true);
		if (Boolean.parseBoolean(fld.getAttribute("final")))
			m.getModifiers().setFinal(true);
		// need to add parameters.
		
		Element argumentElements = XMLHelper.getElement(fld, "arguments");
		if (argumentElements != null) {
			for (Element argumentElement : XMLHelper.getImmediateSubElements(argumentElements, "argument")) {
				Element typeElement = XMLHelper.getImmediateSubElement(argumentElement,"type");
				if (typeElement != null) {
					IType mytp = TypeParser.parse(typeElement);
					MBParameter p = new MBParameter(mytp, argumentElement.getAttribute("name"));
					m.addParameter(p);
				} else {
					IType mytp = TypeParser.parse(argumentElement.getAttribute("type"),
							Boolean.parseBoolean(argumentElement.getAttribute("array")));
					MBParameter p = new MBParameter(mytp, argumentElement.getAttribute("name"));
					m.addParameter(p);
				}
			}
		}
		m.setComment(XMLHelper.getFirstSubElementsInnerText(fld, "comment"));
		return m;
	}
}