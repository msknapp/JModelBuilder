package com.knapptech.jmodel.build;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.w3c.dom.Element;

import com.knapptech.jmodel.beans.MBClassBean;
import com.knapptech.jmodel.beans.MBFieldBean;
import com.knapptech.jmodel.beans.MBGenericParameterBean;
import com.knapptech.jmodel.beans.MBMethodBean;
import com.knapptech.jmodel.build.strategic.BuildStrategy;
import com.knapptech.jmodel.build.strategic.IStrategicClassParser;
import com.knapptech.jmodel.build.strategic.StrategicClassParser;
import com.knapptech.jmodel.build.strategic.multiples.ExtensibleClassParser;
import com.knapptech.jmodel.build.strategic.multiples.ExtensibleSlaveClassParser;
import com.knapptech.jmodel.build.strategic.pairs.BeanAndInterfaceClassParser;
import com.knapptech.jmodel.build.strategic.pairs.ImmutableAndInterfaceClassParser;
import com.knapptech.jmodel.build.strategic.pairs.SingletonAndInterfaceClassParser;
import com.knapptech.jmodel.build.strategic.single.BeanClassParser;
import com.knapptech.jmodel.build.strategic.single.FactoryClassParser;
import com.knapptech.jmodel.build.strategic.single.ImmutableClassParser;
import com.knapptech.jmodel.build.strategic.single.LockingClassParser;
import com.knapptech.jmodel.build.strategic.single.ManagedClassParser;
import com.knapptech.jmodel.build.strategic.single.SingletonClassParser;
import com.knapptech.jmodel.model.MBPackage;
import com.knapptech.jmodel.model.Visibility;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.MBType;
import com.knapptech.jmodel.model.type.np.IClass;
import com.knapptech.jmodel.model.type.np.IInterface;
import com.knapptech.jmodel.model.type.np.MBClass;

public class ClassParser {
	
	private static HashMap<MBPackage,HashMap<String,StrategicClassParser>> parsers = 
			new HashMap<MBPackage, HashMap<String,StrategicClassParser>>();
	
	public static void buildTypes(Element item, MBPackage owner) {
		String localName = item.getAttribute("name");
		System.out.println("Parsing class "+localName);
		IStrategicClassParser parser = null;
		String strategyClassString = item.getAttribute("strategyclass");
		if (strategyClassString != null && strategyClassString.length()>0) {
			// try to load it from the class path.
			try {
				Class<?> c = ClassLoader.getSystemClassLoader().loadClass(strategyClassString);
				parser = (IStrategicClassParser) c.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Failed to use your strategy class.  Going to use the strategy instead as a parser.");
			}
		}
		if (parser == null) {
			String strategyString = item.getAttribute("strategy");
			if (strategyString == null || strategyString.length()<1)
				strategyString = "bean";
			BuildStrategy strategy = BuildStrategy.parse(strategyString);
			parser = getOrCreateParser(owner, localName, strategy);
		}
		parser.setClassBean(buildClassBean(item, owner));
		parser.setOwner(owner);
		if (parser.getClassBean().isComparable())
			parser.getClassBean().setComparable(peakForComparableFields(item));
		parser.buildTypes();
	}
	
	public static boolean peakForComparableFields(Element item) {
		for (String att : XMLHelper.getImmediateSubElementsAttribute(item, "field", "compareOrder")) {
			try {
				int con = Integer.parseInt(att);
				if (0<con && con<11)
					return true;
			} catch (Exception e) {
				System.out.println("Unable to parse a compare order: "+att);
			}
		}
		return false;
	}

	public static void buildElements(Element item, MBPackage owner) {
		String localName = item.getAttribute("name");
		System.out.println("Parsing elements of class "+localName);
		String strategyString = item.getAttribute("strategy");
		if (strategyString == null || strategyString.length()<1)
			strategyString = "extensible";
		BuildStrategy strategy = BuildStrategy.parse(strategyString);
		StrategicClassParser parser = getOrCreateParser(owner, localName, strategy);
		MBClassBean clazz = parser.getClassBean();
		buildClassBeanElements(clazz, item);
		parser.buildElements();
	}
	
	private static StrategicClassParser getOrCreateParser(MBPackage owner,String localName,BuildStrategy strategy) {
		HashMap<String,StrategicClassParser> parsersInPackage = parsers.get(owner);
		if (parsersInPackage == null) {
			parsersInPackage = new HashMap<String, StrategicClassParser>();
			parsers.put(owner,parsersInPackage);
		}
		StrategicClassParser parser = parsersInPackage.get(localName);
		if (parser == null) {
			// need to create the class bean.
			parser = createParser(strategy);
			parsersInPackage.put(localName, parser);
		}
		return parser;
	}
	
	public static MBClassBean buildClassBean(Element item, MBPackage owner) {
		MBClassBean clazz = new MBClassBean();
		clazz.setLocalName(item.getAttribute("name"));
		String strategy = item.getAttribute("strategy");
		if (strategy == null || strategy.length()<1)
			strategy = "extensible";
		BuildStrategy bs = BuildStrategy.parse(strategy);
		clazz.setStrategy(bs);
		String abs = item.getAttribute("abstract");
		clazz.setAbstract(abs!=null && abs.length()>0 && Boolean.parseBoolean(abs));
		Collection<IInterface> implementations = getImplementations(item);
		for (IInterface nfc : implementations) {
			clazz.getImplements().add(nfc);
		}
		IClass prnt = (IClass) getParentClass(item);
		clazz.setExtends(prnt);
		
		String s = item.getAttribute("comparable");
		if (s == null || s.length()<1) 
			s = "true";
		clazz.setComparable(Boolean.parseBoolean(s));
		String vs = item.getAttribute("visibility");
		if (vs == null || vs.length()<1)
			vs = "public";
		clazz.setVisibility(Visibility.parse(vs));
		boolean hasGenericParameters = false;
		
		for (Element en : XMLHelper.getImmediateSubElements(item, "genericparameter")) {
			String rep = en.getAttribute("representation");
			MBGenericParameterBean gp = new MBGenericParameterBean();
			gp.setRepresentation(rep);
			clazz.getGenericParameters().add(gp);
			
			for (Element en2 : XMLHelper.getImmediateSubElements(en, "upperbounds")) {
				IType t = TypeParser.parse(en2);
				gp.getUpperBounds().add(t);
			}
			
			for (Element en2 : XMLHelper.getImmediateSubElements(en, "lowerbounds")) {
				IType t = TypeParser.parse(en2);
				gp.getLowerBounds().add(t);
			}
		}
		clazz.setComment(getComment(item));
		return clazz;
	}
	
	public static MBClassBean buildClassBeanElements(MBClassBean clazz,Element item) {
		Collection<MBFieldBean> fields = getFields(item);
		for (MBFieldBean field : fields) {
			clazz.getFields().put(field.getName(), field);
			field.setOwnerBean(clazz);
		}
		Collection<MBMethodBean> requiredMethods = getRequiredMethods(item);
		for (MBMethodBean method : requiredMethods) {
			if (method == null)
				continue;
			clazz.getMethods().put(method.getName(), method);
			method.setOwnerBean(clazz);
		}
		clazz.setComment(getComment(item));
		return clazz;
	}
	
	public static String getComment(Element item) {
		return XMLHelper.getFirstSubElementsInnerText(item, "comment");
	}

	private static StrategicClassParser createParser(BuildStrategy bs) {
		switch (bs) {
			case BEAN: return new BeanClassParser();
			case IMMUTABLE: return new ImmutableClassParser();
			case BEANANDINTERFACE: return new BeanAndInterfaceClassParser();
			case IMMUTABLEANDINTERFACE: return new ImmutableAndInterfaceClassParser();
			case EXTENSIBLE: return new ExtensibleClassParser();
			case EXTENSIBLESLAVE: return new ExtensibleSlaveClassParser();
			case SINGLETON: return new SingletonClassParser();
			case SINGLETONANDINTERFACE: return new SingletonAndInterfaceClassParser();
			case MANAGED: return new ManagedClassParser();
			case FACTORY: return new FactoryClassParser();
			case LOCKING: return new LockingClassParser();
			case UNLOCKING: return new LockingClassParser() {
				protected boolean allowUnlock() {
					return true;
				}
			};
		}
		return null;
	}
	
	public static ArrayList<MBFieldBean> getFields(Element item) {
		ArrayList<MBFieldBean> fields = new ArrayList<MBFieldBean>();
		for (Element fld : XMLHelper.getImmediateSubElements(item, "field")) {
				MBFieldBean fe = FieldParser.parse(fld);
				fields.add(fe);
			}
		return fields;
	}
	
	public static ArrayList<MBMethodBean> getRequiredMethods(Element item) {
		ArrayList<MBMethodBean> fields = new ArrayList<MBMethodBean>();
		for (Element fld : XMLHelper.getImmediateSubElements(item, "requiredmethod")) {
			MBMethodBean fe = RequiredMethodParser.parse(fld);
			fields.add(fe);
		}
		return fields;
	}
	
	public static ArrayList<IInterface> getImplementations(Element item) {
		ArrayList<IInterface> ms = getImplementationsForString(item, "implements");
		if (item.getNodeName().equals("interface")) {
			ms.addAll(getImplementationsForString(item, "extends"));
		}
		return ms;
	}
	
	public static ArrayList<IInterface> getImplementationsForString(Element item,String searchNodeName) {
		ArrayList<IInterface> ms = new ArrayList<IInterface>();
		for (Element t : XMLHelper.getImmediateSubElements(item, searchNodeName)) {
			IType tp = TypeParser.parse(t);
			if (!(tp instanceof IInterface)) {
				throw new IllegalArgumentException("implements returned a type that is not an interface.\n"+tp.getClass());
			}
			ms.add((IInterface)tp);
		}
		return ms;
	}
	
	public static IType getParentClass(Element item) {
		for (Element t : XMLHelper.getImmediateSubElements(item, "extends")) {
			IType tp = TypeParser.parse(t);
			// can either be a class or a collection type.
			if (tp != null)
				return tp;
		}
		MBType t = MBClass.OBJECT;
		return t;
	}
}