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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.knapptech.jmodel.beans.MBFieldBean;
import com.knapptech.jmodel.model.MBParameter;
import com.knapptech.jmodel.model.Visibility;
import com.knapptech.jmodel.model.type.IType;

public class FieldParser {
	
	public static MBFieldBean parse(Element item) {
		MBFieldBean f = new MBFieldBean();
		f.setName(item.getAttribute("name"));
		f.setType(parseType(item));
		if (f.getType()==null)
			throw new RuntimeException("For the field named \""+f.getName()+"\", found no type declaration in the xml.");
		if (Boolean.parseBoolean(item.getAttribute("static")))
			f.getModifiers().setStatic(true);
		if (Boolean.parseBoolean(item.getAttribute("abstract")))
			f.getModifiers().setAbstract(true);
		String vsblt = item.getAttribute("visibility");
		if (vsblt==null || vsblt.length()<1)
			vsblt = "private";
		f.getModifiers().setVisibility(Visibility.parse(vsblt));
		f.setGettable(!item.getAttribute("gettable").equals("false"));
		f.setSettable(!item.getAttribute("settable").equals("false"));
		f.setIncrementable(!item.getAttribute("incrementable").equals("false"));

		f.setDecrementable(!item.getAttribute("decrementable").equals("false"));
		f.setSerializable(!item.getAttribute("serializable").equals("false"));
		f.setComparable(!item.getAttribute("comparable").equals("false"));
		f.setUnique(!item.getAttribute("unique").equals("false"));
		f.setSynchronized(Boolean.parseBoolean(item.getAttribute("synchronized")));
		f.setOverrides(Boolean.parseBoolean(item.getAttribute("overrides")));
		f.setFinal(Boolean.parseBoolean(item.getAttribute("final")));
		f.setKey(Boolean.parseBoolean(item.getAttribute("key")));
		f.setNullable(!item.getAttribute("nullable").equals("false"));
		f.setImmutable(Boolean.parseBoolean(item.getAttribute("immutable")));
		f.setComment(XMLHelper.getFirstSubElementsInnerText(item, "comment"));
		f.setInitializer(XMLHelper.getFirstSubElementsInnerText(item, "initializer"));
		
		return f;
	}
	
	private static IType parseType(Element item) {
		IType fieldType = null;
		NodeList nl = item.getElementsByTagName("type");
		if (nl !=null) {
			for (int i = 0;i<nl.getLength();i++) {
				Node n = nl.item(i);
				if (n ==null || n.getNodeType() != Node.ELEMENT_NODE)
					continue;
				fieldType=TypeParser.parse((Element)n);
				break;
			}
		}
		if (fieldType==null) {
			String tp = item.getAttribute("type");
			String ard = item.getAttribute("array");
			boolean ar = (ard!=null && Boolean.parseBoolean(ard));
			fieldType=TypeParser.parse(tp,ar);
		}
		return fieldType;
	}
	
	public static MBParameter convertToParameter(MBFieldBean field) {
		return new MBParameter(field.getType(),field.getName());
	}
}