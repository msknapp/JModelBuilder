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
package com.knapptech.jmodel.build.strategic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

import com.knapptech.jmodel.beans.MBClassBean;
import com.knapptech.jmodel.beans.MBFieldBean;
import com.knapptech.jmodel.beans.MBGenericParameterBean;
import com.knapptech.jmodel.beans.MBMethodBean;
import com.knapptech.jmodel.build.FieldParser;
import com.knapptech.jmodel.model.ISourceCode;
import com.knapptech.jmodel.model.MBExistingClassParser;
import com.knapptech.jmodel.model.MBFieldInitializer;
import com.knapptech.jmodel.model.MBGenericParameter;
import com.knapptech.jmodel.model.MBModifiers;
import com.knapptech.jmodel.model.MBPackage;
import com.knapptech.jmodel.model.MBParameter;
import com.knapptech.jmodel.model.MBSourceLine;
import com.knapptech.jmodel.model.Visibility;
import com.knapptech.jmodel.model.element.MBConstructor;
import com.knapptech.jmodel.model.element.MBField;
import com.knapptech.jmodel.model.element.MBMethod;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.MBPrimitiveType;
import com.knapptech.jmodel.model.type.MBType;
import com.knapptech.jmodel.model.type.np.IClass;
import com.knapptech.jmodel.model.type.np.IInterface;
import com.knapptech.jmodel.model.type.np.MBClass;
import com.knapptech.jmodel.model.type.np.MBClassWithParameters;
import com.knapptech.jmodel.model.type.np.MBInterface;
import com.knapptech.jmodel.model.type.np.MBInterfaceWithParameters;
import com.knapptech.jmodel.model.type.np.MBNonPrimitiveType;
import com.knapptech.jmodel.model.type.parametered.MBParameteredInterface;

public abstract class StrategicClassParser implements IStrategicClassParser {
	
	protected StrategicClassParser() {}
	
	private MBClassBean clazz;
	private MBPackage owner;

	public MBClassBean getClassBean() {
		return clazz;
	}
	public void setClassBean(MBClassBean clazz) {
		this.clazz = clazz;
	}
	public MBPackage getOwner() {
		return owner;
	}
	public void setOwner(MBPackage owner) {
		this.owner = owner;
	}
	
	public abstract void buildTypes();
	public abstract void buildElements();
	
	public static String capitalizeName(String name) {
		return name.substring(0,1).toUpperCase()+name.substring(1);
	}
	
	public static MBClass makeDefaultClass(MBClassBean clazz,MBPackage owner) {
		return makeClass(clazz,owner,clazz.getLocalName(),(IClass) clazz.getExtends());
	}
	
	public static MBClass makeClass(MBClassBean clazz,MBPackage owner,String localName) {
		return makeClass(clazz,owner,localName,(IClass) clazz.getExtends());
	}
	
	public static MBClass makeClass(MBClassBean clazz,MBPackage owner,String localName,IClass parent) {
		MBClass cl = null;
		if (clazz.getGenericParameters().size()<1) {
			cl = MBClass.getOrCreate(localName,parent, owner);
		} else {
			MBClassWithParameters cwp = MBClassWithParameters.getOrCreate(localName, parent,owner);
			for (MBGenericParameterBean gpb : clazz.getGenericParameters()) {
				MBGenericParameter.getOrCreate(cwp, gpb.getRepresentation(), 
						gpb.getUpperBounds(),
						gpb.getLowerBounds());
			}
			cl = cwp;
		}
		cl.getModifiers().setAbstract(clazz.isAbstract());
		cl.getModifiers().setVisibility(clazz.getVisibility());
		addInterfacesImplemented(clazz.getImplements(), cl);
		if (clazz.getComment() != null && clazz.getComment().length()>0)
			cl.setComment(clazz.getComment());
		return cl;
	}
	
	public static void addImplementsComparable(MBNonPrimitiveType type) {
		MBParameteredInterface cprbl = MBInterfaceWithParameters.createComparableTo(type);
		if (!type.hasImplementedInterface(cprbl)) {
			type.addImplementedInterface(cprbl);
		}
	}
	
	public static void addInterfacesImplemented(Collection<IInterface> implementations,MBClass cl) {
		if (implementations != null) {
			for (IInterface ji : implementations) {
				cl.addImplementedInterface(ji);
			}
		}
	}
	
	public static void addFields(Collection<MBFieldBean> fields,MBClass cl) {
		for (MBFieldBean field : fields) {
			MBField f = new MBField(field.getName(),cl,field.getType());
			f.setModifiers(new MBModifiers(field.getModifiers()));
			f.setComment(field.getComment());
			if (field.getInitializer()!=null && field.getInitializer().length()>0)
				MBFieldInitializer.createInitializer(field.getInitializer(), f);
		}
	}
	
	public static ArrayList<MBFieldBean> getUniqueFields(Collection<MBFieldBean> fields) {
		ArrayList<MBFieldBean> nfs = new ArrayList<MBFieldBean>();
		if (fields == null || fields.size()<1)
			return nfs;
		for (MBFieldBean f : fields) {
			if (f == null)
				continue;
			if (f.isUnique())
				nfs.add(f);
		}
		return nfs;
	}
	
	public static ArrayList<MBParameter> getUniqueParameters(Collection<MBFieldBean> fields) {
		ArrayList<MBParameter> nfs = new ArrayList<MBParameter>();
		if (fields == null || fields.size()<1)
			return nfs;
		for (MBFieldBean f : fields) {
			if (f == null)
				continue;
			if (f.isUnique())
				nfs.add(FieldParser.convertToParameter(f));
		}
		return nfs;
	}
	
	public static MBConstructor makeNoArgConstructor(MBClass clazz) {
		MBConstructor cns = MBConstructor.getOrCreate(clazz);
		cns.getModifiers().setVisibility(Visibility.PUBLIC);
		return cns;
	}
	
	public static MBConstructor makeDefaultConstructor(MBClass clazz,Collection<MBFieldBean> fields) {
		List<MBParameter> prms = new ArrayList<MBParameter>();
		for (MBFieldBean fld : fields) {
			if (fld.isNullable() && !fld.isImmutable() || fld.isStatic())
				continue;
			prms.add(new MBParameter(fld.getType(), fld.getName()));
		}
		MBConstructor cns = MBConstructor.getOrCreate(clazz,prms.toArray(new MBParameter[prms.size()]));
		cns.getModifiers().setVisibility(Visibility.PUBLIC);
		for (MBFieldBean fld : fields) {
			if (fld.isNullable() && !fld.isImmutable() || fld.isStatic())
				continue;
			if (!fld.getType().isPrimitive()) {
				cns.getSourceCode().add("if ("+fld.getName()+"==null)");
				cns.getSourceCode().addIndented("throw new NullPointerException(\""+fld.getName()+" cannot be null.\");");
			}
			if (fld.isSettable() && !fld.isImmutable()) {
				cns.getSourceCode().add("set"+capitalizeName(fld.getName())+"("+fld.getName()+");");
			} else {
				cns.getSourceCode().add("this."+fld.getName()+"="+fld.getName()+";");
			}
		}
		return cns;
	}
	
	public static MBConstructor makeConstructorForImmutable(MBClass clazz,Collection<MBFieldBean> fields) {
		for (MBFieldBean field : fields) {
			if (field.isStatic())
				continue;
			field.setImmutable(true);
		}
		MBConstructor cns = makeConstructorForFields(clazz, getNonStaticFields(fields));
		return cns;
	}
	
	public static MBConstructor makeConstructorForFields(MBClass clazz,List<MBFieldBean> fields) {
		MBParameter[] parms = convertFieldsToParameters(fields);
		MBConstructor cns = MBConstructor.getOrCreate(clazz,parms);
		cns.getModifiers().setVisibility(Visibility.PUBLIC);
		for (MBFieldBean fld : fields) {
			if (fld.isStatic())
				continue;
			if (!fld.getType().isPrimitive()) {
				cns.getSourceCode().add("if ("+fld.getName()+"==null)");
				cns.getSourceCode().addIndented("throw new NullPointerException(\""+fld.getName()+" cannot be null.\");");
			}
			if (fld.isSettable() && !fld.isImmutable()) {
				cns.getSourceCode().add("set"+capitalizeName(fld.getName())+"("+fld.getName()+");");
			} else {
				cns.getSourceCode().add("this."+fld.getName()+"="+fld.getName()+";");
			}
		}
		return cns;
	}
	
	public static MBConstructor makeAllFieldsConstructor(MBClass clazz,Collection<MBFieldBean> fields) {
		List<MBParameter> prms = new ArrayList<MBParameter>();
		for (MBFieldBean fld : fields) {
			prms.add(new MBParameter(fld.getType(), fld.getName()));
		}
		MBConstructor cns = MBConstructor.getOrCreate(clazz,prms.toArray(new MBParameter[prms.size()]));
		cns.getModifiers().setVisibility(Visibility.PUBLIC);
		for (MBFieldBean fld : fields) {
			if (!fld.isNullable() && !fld.getType().isPrimitive()) {
				cns.getSourceCode().add("if ("+fld.getName()+"==null)");
				cns.getSourceCode().addIndented("throw new NullPointerException(\""+fld.getName()+" cannot be null.\");");
			}
			if (fld.isSettable() && !fld.isImmutable()) {
				cns.getSourceCode().add("set"+capitalizeName(fld.getName())+"("+fld.getName()+");");
			} else {
				cns.getSourceCode().add("this."+fld.getName()+"="+fld.getName()+";");
			}
		}
		return cns;
	}
	
	public static MBConstructor makeCloneConstructor(MBClass clazz,Collection<MBFieldBean> fields,IType otherType) {
		MBConstructor cns = MBConstructor.getOrCreate(clazz,new MBParameter(otherType,"original"));
		cns.getModifiers().setVisibility(Visibility.PUBLIC);
		for (MBFieldBean fld : fields) {
			if (fld.isStatic())
				continue;
			if (!fld.isGettable()) {
				if (otherType != clazz)
					continue;
			}
			if (!fld.isNullable() && !fld.getType().isPrimitive()) {
				if (fld.isGettable()) {
					cns.getSourceCode().add("if (original.get"+capitalizeName(fld.getName())+"()==null)");
				} else {
					cns.getSourceCode().add("if (original."+fld.getName()+"==null)");
				}
				cns.getSourceCode().addIndented("throw new NullPointerException(\""+fld.getName()+" cannot be null.\");");
			}
			if (fld.isSettable() && !fld.isImmutable()) {
				if (fld.isGettable() ) {
					cns.getSourceCode().add("set"+capitalizeName(fld.getName())+"(original.get"+capitalizeName(fld.getName())+"());");
				} else {
					cns.getSourceCode().add("set"+capitalizeName(fld.getName())+"(original."+fld.getName()+");");
				}
			} else {
				if (fld.isGettable() ) {
					cns.getSourceCode().add("this."+fld.getName()+"=get"+capitalizeName(fld.getName())+"();");
				} else {
					cns.getSourceCode().add("this."+fld.getName()+"=original."+fld.getName()+");");
				}
			}
		}
		return cns;
	}
	
	public static MBConstructor makeCloneConstructorForImmutable(MBClass clazz,Collection<MBFieldBean> fields,IType otherType) {
		MBConstructor cns = MBConstructor.getOrCreate(clazz,new MBParameter(otherType,"original"));
		cns.getModifiers().setVisibility(Visibility.PUBLIC);
		for (MBFieldBean fld : fields) {
			if (fld.isStatic())
				continue;
			if (!fld.isGettable()) {
				if (otherType != clazz)
					continue;
			}
			if (!fld.isNullable() && !fld.getType().isPrimitive()) {
				if (fld.isGettable()) {
					cns.getSourceCode().add("if (original.get"+capitalizeName(fld.getName())+"()==null)");
				} else {
					cns.getSourceCode().add("if (original."+fld.getName()+"==null)");
				}
				cns.getSourceCode().addIndented("throw new NullPointerException(\""+fld.getName()+" cannot be null.\");");
			}
			if (fld.isGettable() ) {
				cns.getSourceCode().add("this."+fld.getName()+"=original.get"+capitalizeName(fld.getName())+"();");
			} else {
				cns.getSourceCode().add("this."+fld.getName()+"=original."+fld.getName()+";");
			}
		}
		return cns;
	}
	
	public static MBMethodBean createGetMethod(MBFieldBean fe) {
		MBMethodBean m = new MBMethodBean();
		m.setName("get"+capitalizeName(fe.getName()));
		m.setType(fe.getType());
		m.getModifiers().setStatic(fe.isStatic());
		m.getModifiers().makePublic();
		m.getModifiers().setSynchronized(fe.isSynchronized());
		m.getCode().addLine("return " +
				(fe.isStatic() ? 
						(fe.getOwnerBean() != null ? fe.getOwnerBean().getLocalName() : "")
						: "this") +
				"."+fe.getName()+";");
		m.setOwnerBean(fe.getOwnerBean());
		return m;
	}
	
	public static MBMethodBean createSetMethod(MBFieldBean fe) {
		if (fe == null || fe.isFinal())
			return null;
		if (!fe.getOwnerBean().getFields().containsKey(fe.getName())) {
			System.out.println("wow");
		}
		MBMethodBean m = new MBMethodBean();
		m.setName("set"+capitalizeName(fe.getName()));
		m.addParameter(new MBParameter(fe.getType(), fe.getName()));
		if (!fe.isNullable() && !fe.getType().isPrimitive()) {
			m.getCode().addLine("if ("+fe.getName()+" == null)");
			m.getCode().addIndented("throw new NullPointerException(\"This field cannot be null.\");");
		}
		m.getCode().addLine(
				(fe.isStatic() ? 
						(fe.getOwnerBean() != null ? fe.getOwnerBean().getLocalName() : "")
						: "this")+
						"."+fe.getName()+" = "+fe.getName()+";");
		m.getModifiers().setStatic(fe.isStatic());
		m.getModifiers().makePublic();
		m.getModifiers().setSynchronized(fe.isSynchronized());
		m.setOwnerBean(fe.getOwnerBean());
		return m;
	}
	
	public static void addGetters(MBClass cl,Collection<MBFieldBean> fields) {
		for (MBFieldBean fe : fields) {
			if (fe.isGettable()) {
				MBMethodBean m = createGetMethod(fe);
				MBMethod mm = makeRealMethod(m, cl);
			}
		}
	}
	
	public static void addSetters(MBClass cl,Collection<MBFieldBean> fields) {
		for (MBFieldBean fe : fields) {
			if (fe.isSettable() && !fe.isImmutable() && !fe.isFinal()) {
				MBMethodBean m = createSetMethod(fe);
				MBMethod mm = makeRealMethod(m, cl);
			}
		}
	}
	
	public static void addNonStaticGetters(MBClass cl,Collection<MBFieldBean> fields) {
		addGetters(cl, getNonStaticFields(fields));
	}
	
	public static void addNonStaticSetters(MBClass cl,Collection<MBFieldBean> fields) {
		addSetters(cl, getNonStaticFields(fields));
	}
	
	public static List<MBFieldBean> getNonStaticFields(Collection<MBFieldBean> fields) {
		ArrayList<MBFieldBean> flds = new ArrayList<MBFieldBean>();
		for (MBFieldBean fb : fields) {
			if (!fb.isStatic())
				flds.add(fb);
		}
		return flds;
	}
	
	public static void addGetters(MBInterface ji,Collection<MBFieldBean> fields) {
		for (MBFieldBean fe : fields) {
			if (fe.isGettable()) {
				MBMethodBean m = createGetMethod(fe);
				makeRealMethod(m, ji);
			}
		}
	}
	
	public static void addSetters(MBInterface ji,Collection<MBFieldBean> fields) {
		for (MBFieldBean fe : fields) {
			if (fe.isGettable()) {
				MBMethodBean m = createSetMethod(fe);
				if (m!=null)
					makeRealMethod(m, ji);
			}
		}
	}
	
	public static void addRequiredMethods(MBClass cl, Collection<MBMethodBean> requiredMethods,boolean includeModifiers) {
		for (MBMethodBean rm : requiredMethods) {
			if (!includeModifiers && rm.isModifiesClass())
				continue;
			MBMethod method = new MBMethod(rm.getName(),cl,rm.getType(),rm.getParameters().toArray(new MBParameter[rm.getParameters().size()]));
			method.setModifiers(rm.getModifiers());
			if (rm.getSourceCode().lines()>0) {
				for (ISourceCode sc : rm.getSourceCode().getCode()) {
					method.getSourceCode().add(sc);
				}
			} else {
				method.getSourceCode().addLine("// TODO write the \""+method.getName()+
						"\" method for the \""+cl.getLocalName()+"\" class.");
				IType t = method.getReturnType();
				if (t != null) {
					if (t.isPrimitive()) {
						if (t.equals(MBPrimitiveType.BOOLEAN)) {
							method.getSourceCode().addLine("return false;");
						} else if (t.equals(MBPrimitiveType.CHAR)) {
							method.getSourceCode().addLine("return 'a';");
						} else if (t.equals(MBPrimitiveType.LONG)) {
							method.getSourceCode().addLine("return 0l;");
						} else {
							method.getSourceCode().addLine("return 0;");
						}
					} else {
						method.getSourceCode().addLine("return null;");
					}
				}
			}
			method.setComment(rm.getComment());
		}
	}
	
	public static void addRequiredMethods(MBInterface ji, Collection<MBMethodBean> requiredMethods,boolean includeModifiers) {
		for (MBMethodBean rm : requiredMethods) {
			if (!includeModifiers && rm.isModifiesClass())
				continue;
			MBMethod method = null;
			try {
				method = new MBMethod(rm.getName(),ji,rm.getType(),rm.getParameters().toArray(new MBParameter[rm.getParameters().size()]));
				method.setModifiers(rm.getModifiers());
			} catch (Exception e) {
				System.err.println("Error trying to add required method.");
				e.printStackTrace();
			}
		}
	}
	
	public static MBMethod makeRealMethod(MBMethodBean method,MBInterface nfc) {
		MBMethod m = new MBMethod(method.getName(),nfc,method.getType(),method.getParameters().toArray(new MBParameter[method.getParameters().size()]));
		m.setSourceCode(method.getSourceCode());
		m.setModifiers(new MBModifiers(method.getModifiers()));
		return m;
	}
	
	public static MBMethod makeRealMethod(MBMethodBean method,MBClass clazz) {
		MBMethod m = new MBMethod(method.getName(),clazz,method.getType(),method.getParameters().toArray(new MBParameter[method.getParameters().size()]));
		m.setSourceCode(method.getSourceCode());
		m.setModifiers(new MBModifiers(method.getModifiers()));
		return m;
	}
	
	public static MBMethodBean createEqualsMethod(Collection<MBFieldBean> fields,MBType expectedObjectType) {
		MBMethodBean m = new MBMethodBean();
		m.setName("equals");
		m.setType(MBPrimitiveType.BOOLEAN);
		m.addParameter(new MBParameter(MBClass.OBJECT,"o"));
		m.getSourceCode().add("if (o == this)");
		m.getSourceCode().addIndented("return true;");
		m.getSourceCode().add("if (o == null)");
		m.getSourceCode().addIndented("return false;");
		m.getSourceCode().add("if (!(o instanceof "+expectedObjectType.getLocalName()+"))");
		m.getSourceCode().addIndented("return false;");
		m.getSourceCode().add(expectedObjectType.getLocalName()+" m = ("+expectedObjectType.getLocalName()+")o;");
		// start checking the fields.
		for (MBFieldBean field : fields) {
			if (!field.isUnique() || field.isStatic())
				continue;
			String n = field.getName();
			String cn = capitalizeName(n);
			if (field.getType().isArray()) {
				MBSourceLine sl = new MBSourceLine("if (!Arrays.equals(get"+cn+"(),m.get"+cn+"()))");
				m.getSourceCode().add(sl);
				m.getSourceCode().addIndented("return false;");
				m.getSourceCode().addRequiredImport(MBExistingClassParser.parse(java.util.Arrays.class));
			} else if (field.getType().isPrimitive()) {
				m.getSourceCode().add("if ("+n+"!=m.get"+cn+"())");
				m.getSourceCode().addIndented("return false;");
			} else {
				m.getSourceCode().add("if (get"+cn+"()==null) {");
				m.getSourceCode().addIndented("if (m.get"+cn+"() != null);");
				m.getSourceCode().addIndented("\treturn false;");
				m.getSourceCode().add("} else if (!get"+cn+"().equals(m.get"+cn+"()))");
				m.getSourceCode().addIndented("return false;");
			}
		}
		m.getSourceCode().add("return true;");
		return m;
	}

	
	public static MBMethodBean createHashCodeMethod(Collection<MBFieldBean> fields) {
		MBMethodBean m = new MBMethodBean();
		m.setName("hashCode");
		m.setType(MBPrimitiveType.INT);
		m.getSourceCode().add("int i = 1;");
		// start checking the fields.
		for (MBFieldBean field : fields) {
			if (!field.isUnique() || field.isStatic())
				continue;
			String n = field.getName();
			String cn = capitalizeName(n);
			if (field.getType().isArray()) {
				MBSourceLine sl = new MBSourceLine("i += Arrays.hashCode(get"+cn+"());");
				m.getSourceCode().add(sl);
				m.getSourceCode().addRequiredImport(MBExistingClassParser.parse(java.util.Arrays.class));
			} else if (field.getType().isPrimitive()) {
				if (field.getType().equals(MBPrimitiveType.BOOLEAN)) {
					m.getSourceCode().add("if ("+n+")");
					m.getSourceCode().addIndented("i=i*1000;");
				} else if (field.getType().equals(MBPrimitiveType.DOUBLE)) {
					m.getSourceCode().add("i+=(int)(1000*"+n+");");
				} else {
					m.getSourceCode().add("i+=(int)"+n+";");
				}
			} else {
				m.getSourceCode().add("if ("+field.getName()+"!=null)");
				m.getSourceCode().addIndented("i+="+n+".hashCode();");
			}
		}
		m.getSourceCode().add("return i;");
		return m;
	}
	
	public static MBMethodBean createCompareToMethod(Collection<MBFieldBean> fields,MBType expectedObjectType) {
		MBMethodBean m = new MBMethodBean();
		m.setName("compareTo");
		m.setType(MBPrimitiveType.INT);
		try {
			m.addParameter(new MBParameter(expectedObjectType,"o"));
		} catch (Exception e) {}
		m.getSourceCode().add("if (o == null)");
		m.getSourceCode().addIndented("return -1;");
		m.getSourceCode().add("if (o == this)");
		m.getSourceCode().addIndented("return 0;");
		m.getSourceCode().add("int i = 0;");
		TreeSet<MBFieldBean> sortedFields = new TreeSet<MBFieldBean>(new FieldCompareOrderComparator());
		sortedFields.addAll(fields);
		// start checking the fields.
		for (MBFieldBean field : sortedFields) {
			if (!field.isUnique() || !field.isGettable() || field.isStatic())
				continue;
			IType t = field.getType();
			if (!t.isComparable())
				continue;
			String n = field.getName();
			String cn = capitalizeName(n);
			if (t instanceof MBPrimitiveType) {
				MBPrimitiveType pt = (MBPrimitiveType)t;
				if  (pt.isNumberType()) {
					m.getSourceCode().add("i = (int)(get"+cn+"() - o.get"+cn+"());");
					m.getSourceCode().add("if (i != 0) return i;");
				} else if (pt==MBPrimitiveType.BOOLEAN) {
					m.getSourceCode().add("i = (int)((get"+cn+"() ? 1 : 0 ) - (o.get"+cn+"() ? 1 : 0));");
					m.getSourceCode().add("if (i != 0) return i;");
				} else if (pt==MBPrimitiveType.CHAR) {
					m.getSourceCode().add("i = (int)((int)get"+cn+"() - (int)o.get"+cn+"());");
					m.getSourceCode().add("if (i != 0) return i;");
				} else {
					throw new RuntimeException("Unknown primitive type: "+pt.getLocalName());
				}
			} else if (field.getType() instanceof MBClass) {
				m.getSourceCode().add("i = get"+cn+"().compareTo(o.get"+cn+"());");
				m.getSourceCode().add("if (i != 0) return i;");
			}
		}
		m.getSourceCode().add("return i;");
		return m;
	}
	
	private static class FieldCompareOrderComparator implements Comparator<MBFieldBean> {

//		@Override
		public int compare(MBFieldBean o1, MBFieldBean o2) {
			if (!o1.isComparable() && !o2.isComparable())
				return 0;
			if (o1.isComparable() && !o2.isComparable())
				return -1;
			if (!o1.isComparable() && o2.isComparable())
				return 1;
			int i1 = o1.getCompareOrder();
			int i2 = o2.getCompareOrder();
			if (i1 == i2)
				return 0;
			if (i1 < 0)
				return 1;
			if (i2 < 0)
				return -1;
			return i1-i2; // for ascending order.
		}
	}
	
	public static MBParameter[] convertFieldsToParameters(Collection<MBFieldBean> fields) {
		return parametersMeetingAllCriteria(fields, false, false, false, false, false,false);
	}
	
	public static MBParameter[] convertUniqueFieldsToParameters(Collection<MBFieldBean> fields) {
		return parametersMeetingAllCriteria(fields, true, false, false, false, false,false);
	}
	
	public static MBParameter[] convertNotNullableFieldsToParameters(Collection<MBFieldBean> fields) {
		return parametersMeetingAllCriteria(fields, false, false, true, false, false,false);
	}
	
	public static MBParameter[] convertImmutableFieldsToParameters(Collection<MBFieldBean> fields) {
		return parametersMeetingAllCriteria(fields, false, true, false, false, false,false);
	}
	
	public static MBParameter[] convertKeyFieldsToParameters(Collection<MBFieldBean> fields) {
		return parametersMeetingAllCriteria(fields, false, false, false, false, false,true);
	}
	
	public static MBParameter[] parametersMeetingAllCriteria(Collection<MBFieldBean> fields,
			boolean mustBeUnique,boolean mustBeImmutable,boolean mustBeNotNullable,
			boolean mustBeNotSettable,boolean mustBeNotGettable,boolean includeKey) {
		List<MBFieldBean> includes = fieldsMeetingAllCriteria(fields, mustBeUnique,
				mustBeImmutable,mustBeNotNullable,mustBeNotSettable,mustBeNotGettable,includeKey);
		List<MBParameter> parameters = new ArrayList<MBParameter>();
		for (MBFieldBean field : includes) {
			MBParameter parameter = new MBParameter(field.getType(),field.getName());
			parameters.add(parameter);
		}
		return parameters.toArray(new MBParameter[parameters.size()]);
	}
	
	public static MBParameter[] parametersMeetingAnyCriteria(Collection<MBFieldBean> fields,
			boolean includeUnique,boolean includeImmutable,boolean includeNotNullable,
			boolean includeNotSettable,boolean includeNotGettable,boolean includeKey) {
		List<MBFieldBean> includes = fieldsMeetingAnyCriteria(fields, includeUnique, 
				includeImmutable, includeNotNullable, includeNotSettable, includeNotGettable,includeKey);
		List<MBParameter> parameters = new ArrayList<MBParameter>();
		for (MBFieldBean field : includes) {
			MBParameter parameter = new MBParameter(field.getType(),field.getName());
			parameters.add(parameter);
		}
		return parameters.toArray(new MBParameter[parameters.size()]);
	}
	
	public static List<MBFieldBean> fieldsMeetingAnyCriteria(Collection<MBFieldBean> fields,
			boolean includeUnique,boolean includeImmutable,boolean includeNotNullable,
			boolean includeNotSettable,boolean includeNotGettable,boolean includeKey) {
		List<MBFieldBean> includeFields = new ArrayList<MBFieldBean>();
		for (MBFieldBean field : fields) {
			boolean include = false;
			if (includeImmutable && !field.isImmutable())
				include=true;
			if (includeUnique && field.isUnique())
				include=true;
			if (includeNotNullable && !field.isNullable())
				include=true;
			if (includeNotSettable && !field.isSettable())
				include=true;
			if (includeNotGettable && !field.isGettable())
				include=true;
			if (includeKey && field.isKey())
				include=true;
			if (include) {
				includeFields.add(field);
			}
		}
		return includeFields;
	}
	
	public static List<MBFieldBean> getKeyFields(Collection<MBFieldBean> fields) {
		return fieldsMeetingAllCriteria(fields, false,false,false,false,false,true);
	}
	
	public static List<MBFieldBean> getImmutableFields(Collection<MBFieldBean> fields) {
		return fieldsMeetingAllCriteria(fields, false,true,false,false,false,false);
	}
	
	public static List<MBFieldBean> getNotNullableFields(Collection<MBFieldBean> fields) {
		return fieldsMeetingAllCriteria(fields, false,false,true,false,false,false);
	}
	
	public static List<MBFieldBean> fieldsMeetingAllCriteria(Collection<MBFieldBean> fields,
			boolean mustBeUnique,boolean mustBeImmutable,boolean mustBeNotNullable,
			boolean mustBeNotSettable,boolean mustBeNotGettable,boolean mustBeKey) {
		List<MBFieldBean> includeFields = new ArrayList<MBFieldBean>();
		for (MBFieldBean field : fields) {
			if (mustBeUnique && !field.isUnique())
				continue;
			if (mustBeImmutable && !field.isImmutable())
				continue;
			if (mustBeNotNullable && field.isNullable())
				continue;
			if (mustBeNotSettable && field.isSettable())
				continue;
			if (mustBeNotGettable && field.isGettable())
				continue;
			if (mustBeKey && !field.isKey())
				continue;
			includeFields.add(field);
		}
		return includeFields;
	}
	
	public static List<MBFieldBean> fieldsMeetingAllCriteria(Collection<MBFieldBean> fields,
			boolean mustBeSettable,boolean mustBeGettable) {
		List<MBFieldBean> includeFields = new ArrayList<MBFieldBean>();
		for (MBFieldBean field : fields) {
			if (mustBeSettable && !field.isSettable())
				continue;
			if (mustBeGettable && !field.isGettable())
				continue;
			includeFields.add(field);
		}
		return includeFields;
	}
	
	public static List<MBFieldBean> nonStatic(Collection<MBFieldBean> fields) {
		return getNonStaticFields(fields);
	}
	
	public static void manageStaticFinalFields(MBClass clazz,Collection<MBFieldBean> fields) {
		for (MBFieldBean field : fields) {
			if (!field.isStatic() || !field.isFinal())
				continue;
			if (field.getInitializer()!=null && field.getInitializer().length()>0)
				continue;
			// this is a static final field that is not initialized.
			String methodName = "initialize"+field.getName();
			field.setInitializer(methodName+"()");
			MBMethod mtd = new MBMethod(methodName,clazz,clazz);
			mtd.getSourceCode().add("// TODO create the initializer for \""+field.getName()+"\"");
			mtd.getSourceCode().add("return null;");
			mtd.getModifiers().makePrivate();
			mtd.getModifiers().setStatic(true);
			mtd.getModifiers().setFinal(true);
		}
		
	}
}