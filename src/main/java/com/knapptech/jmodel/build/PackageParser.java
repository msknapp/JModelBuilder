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