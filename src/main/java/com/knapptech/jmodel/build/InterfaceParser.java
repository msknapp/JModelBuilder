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