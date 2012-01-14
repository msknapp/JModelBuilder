package com.knapptech.jmodel.build;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import org.w3c.dom.Element;

import com.knapptech.jmodel.beans.MBFieldBean;
import com.knapptech.jmodel.beans.MBMethodBean;
import com.knapptech.jmodel.build.strategic.StrategicClassParser;
import com.knapptech.jmodel.model.IPathItem;
import com.knapptech.jmodel.model.MBFieldInitializer;
import com.knapptech.jmodel.model.MBPackage;
import com.knapptech.jmodel.model.element.MBConstructor;
import com.knapptech.jmodel.model.element.MBField;
import com.knapptech.jmodel.model.type.np.IClass;
import com.knapptech.jmodel.model.type.np.MBEnum;
import com.knapptech.jmodel.model.type.np.MBInterface;

public class EnumParser {
	private static final HashMap<IPathItem,EnumParser> parsers = new HashMap<IPathItem, EnumParser>();
	private MBEnum builtEnum;
	
	public static MBEnum buildTypes(Element item, IPathItem pathItem) {
		EnumParser p = getOrCreateParser(pathItem);
		return p.instanceBuildTypes(item, pathItem);
	}
	
	public static MBEnum buildElements(Element item, IPathItem pathItem) {
		EnumParser p = getOrCreateParser(pathItem);
		return p.instanceBuildElements(item, pathItem);
	}
	
	private static final EnumParser getOrCreateParser(IPathItem pathItem) {
		EnumParser p = parsers.get(pathItem);
		if (p != null)
			return p;
		p = new EnumParser();
		parsers.put(pathItem, p);
		return p;
	}
	
	public MBEnum instanceBuildTypes(Element item, IPathItem p) {
		String snm = item.getAttribute("name");
		System.out.println("Parsing enum "+snm);
		String name = snm;
		if (p instanceof MBPackage) {
			builtEnum = MBEnum.getOrCreate(name, (MBPackage)p);
		} else if (p instanceof IClass) {
			builtEnum = MBEnum.getOrCreate(name, (IClass)p);
		} else {
			throw new IllegalArgumentException("Object must either be a package or class.");
		}
		return builtEnum;
	}
	
	public MBEnum instanceBuildElements(Element item, IPathItem p) {
		System.out.println("Parsing elements of enum "+this.builtEnum.getLocalName());
		Collection<MBMethodBean> requiredMethods = ClassParser.getRequiredMethods(item);
		StrategicClassParser.addRequiredMethods(builtEnum, requiredMethods, true);
		List<MBFieldBean> fields = ClassParser.getFields(item);
		for (MBFieldBean fb : fields) {
			fb.setSettable(false);
		}
		StrategicClassParser.addFields(fields, builtEnum);
		MBConstructor cns = StrategicClassParser.makeConstructorForFields(builtEnum, fields);
		cns.getModifiers().makePrivate();
		StrategicClassParser.addNonStaticGetters(builtEnum, fields);
		for (Element en : XMLHelper.getImmediateSubElements(item, "enuminstance")) {
			MBField enf = new MBField(en.getAttribute("name"),builtEnum,builtEnum);
			enf.getModifiers().makePublic();
			enf.getModifiers().setStatic(true);
			enf.getModifiers().setFinal(true);
			
			String init = "";
			List<String> prms = XMLHelper.getImmediateSubElementsInnerText(en, "parameter");
			for (int i = 0;i<prms.size();i++) {
				init+=prms.get(i) + (i<prms.size()-1 ? "," : "");
			}
			if (init.length()>0) {
				init = "("+init+")";
				MBFieldInitializer.createInitializer(init, enf);
			}
		}
		return builtEnum;
	}

}