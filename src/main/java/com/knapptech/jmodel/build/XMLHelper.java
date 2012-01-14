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

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLHelper {
	
	public static String getFirstSubElementsInnerText(Node element,String subElementName) {
		for (Element n : getImmediateSubElements(element, subElementName)) {
			Node innerNode = n.getFirstChild();
			if (innerNode ==null)
				continue;
			String innerText = innerNode.getNodeValue();
			if (innerText == null || innerText.length()<1) 
				continue;
			return innerText;
		}
		return "";
	}
	
	public static List<String> getImmediateSubElementsInnerText(Node element,String subElementName) {
		List<String> inners = new ArrayList<String>();
		for (Element n : getImmediateSubElements(element, subElementName)) {
			Node innerNode = n.getFirstChild();
			if (innerNode ==null)
				continue;
			String innerText = innerNode.getNodeValue();
			if (innerText == null || innerText.length()<1) 
				continue;
			inners.add(innerText);
		}
		return inners;
	}
	
	public static List<String> getImmediateSubElementsAttribute(Node element,String subElementName,String attributeName) {
		List<String> atts = new ArrayList<String>();
		for (Element n : getImmediateSubElements(element, subElementName)) {
			String att = n.getAttribute(attributeName);
			if (att == null || att.length()<1) 
				continue;
			atts.add(att);
		}
		return atts;
	}
	
	public static List<Element> getImmediateSubElements(Node element,String name) {
		return getImmediateSubElements((Element)element,name);
	}
	
	public static List<Element> getImmediateSubElements(Element element,String name) {
		NodeList nl = element.getChildNodes();
		List<Element> els = new ArrayList<Element>();
		for (int i = 0;i<nl.getLength();i++) {
			Node n = nl.item(i);
			if (n == null || n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (!n.getNodeName().equals(name))
				continue;
			els.add((Element)n);
		}
		return els;
	}
	
	public static Element getElement(Element el,String... path) {
		for (int i = 0;i<path.length;i++) {
			el = getImmediateSubElement(el, path[i]);
			if (el == null)
				return null;
			
		}
		return el;
	}
	
	public static Element getImmediateSubElement(Element element, String name) {
		NodeList nl = element.getChildNodes();
		for (int i = 0;i<nl.getLength();i++) {
			Node n = nl.item(i);
			if (n == null || n.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (n.getNodeName().equals(name))
				return (Element)n;
		}
		return null;
	}
}
