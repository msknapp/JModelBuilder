package com.knapptech.jmodel.model.type.np;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.knapptech.jmodel.model.GenericUsage;
import com.knapptech.jmodel.model.MBGenericParameter;
import com.knapptech.jmodel.model.MBImport;
import com.knapptech.jmodel.model.MBPackage;
import com.knapptech.jmodel.model.type.IType;

public class MBClassWithParameters extends MBClass implements IHasGenericParameters {
	public static final MBClassWithParameters HASHSET = new MBClassWithParameters("HashSet", OBJECT, MBPackage.JAVAUTIL, null);
	public static final MBClassWithParameters HASHMAP = new MBClassWithParameters("HashMap", OBJECT, MBPackage.JAVAUTIL, null);
	public static final MBClassWithParameters HASHTABLE = new MBClassWithParameters("Hashtable", OBJECT, MBPackage.JAVAUTIL, null);
	public static final MBClassWithParameters ARRAYLIST = new MBClassWithParameters("ArrayList", OBJECT, MBPackage.JAVAUTIL, null);
	public static final MBClassWithParameters VECTOR = new MBClassWithParameters("Vector", OBJECT, MBPackage.JAVAUTIL, null);
	public static final MBClassWithParameters TREESET = new MBClassWithParameters("TreeSet", OBJECT, MBPackage.JAVAUTIL, null);
	public static final MBClassWithParameters TREEMAP = new MBClassWithParameters("TreeMap", OBJECT, MBPackage.JAVAUTIL, null);
	
	private List<MBGenericParameter> parameters = new ArrayList<MBGenericParameter>();
	
	static {
		MBGenericParameter.getOrCreate(HASHSET, "E", OBJECT);
		MBGenericParameter.getOrCreate(HASHMAP, "K", OBJECT);
		MBGenericParameter.getOrCreate(HASHTABLE, "K", OBJECT);
		MBGenericParameter.getOrCreate(HASHMAP, "V", OBJECT);
		MBGenericParameter.getOrCreate(HASHTABLE, "V", OBJECT);
		MBGenericParameter.getOrCreate(ARRAYLIST, "E", OBJECT);
		MBGenericParameter.getOrCreate(VECTOR, "E", OBJECT);
		MBGenericParameter.getOrCreate(TREESET, "E", OBJECT);
		MBGenericParameter.getOrCreate(TREEMAP, "K", OBJECT);
		MBGenericParameter.getOrCreate(TREEMAP, "V", OBJECT);
	}
	
	public static MBClassWithParameters getOrCreate(String localName,MBPackage owner) {
		MBClass c = owner.getClass(localName);
		if (c != null) {
			return (MBClassWithParameters)c;
		}
		MBClassWithParameters clz = new MBClassWithParameters(localName,OBJECT,owner,null);
		owner.addClass(clz);
		return clz;
	}
	
	public static MBClassWithParameters getOrCreate(String localName,IClass superClass,MBPackage owner) {
		MBClass c = owner.getClass(localName);
		if (c != null)
			return (MBClassWithParameters)c;
		if (superClass == null)
			superClass = OBJECT;
		MBClassWithParameters clz = new MBClassWithParameters(localName,superClass,owner,null);
		owner.addClass(clz);
		return clz;
	}
	
	protected MBClassWithParameters(String localName, IClass superType,
			MBPackage ownerPackage, MBClass ownerClass) {
		super(localName, superType, ownerPackage, ownerClass);
	}
	
	public int sizeParameters() {
		return parameters.size();
	}

	public List<MBGenericParameter> getParameters() {
		return Collections.unmodifiableList(parameters);
	}
	
	public void addGenericParameter(MBGenericParameter parm) {
		if (parm ==null)
			throw new NullPointerException("Must define the parameter to add.");
		if (hasGenericParameter(parm.getRepresentation()))
			throw new IllegalArgumentException("We already have a generic parameter with that name.");
		this.parameters.add(parm);
	}
	
	public MBGenericParameter getParameter(String representation) {
		for (MBGenericParameter prm : parameters)
			if (prm.getRepresentation().equals(representation))
				return prm;
		return null;
	}
	
	public boolean hasGenericParameter(String representation) {
		return getParameter(representation)!=null;
	}

	public static void init() {
		// do not delete, here to ensure the class is statically initialized so path mappings are created.
	}
	public String getGenericName(GenericUsage usage,IType typeUsing) {
		return getLocalName()+getGenericPart(usage,typeUsing);
	}
	
	public String getGenericPart(GenericUsage usage,IType typeUsing) {
		if (parameters.size()<1)
			return "";
		String s = "<";
		for (int i = 0;i<parameters.size();i++) {
			MBGenericParameter parm = parameters.get(i);
			List<IType> ub = parm.getUpperBounds();
			if (parm.getOwner().equals(typeUsing)) {
				// local usage.
				s+= parm.getRepresentation();
			} else {
				if (usage != GenericUsage.CLASSDECLARATION) {
					s+="?";
					IType tub = (ub.size() > 0 ? ub.get(0) : MBClass.OBJECT);
					if (tub != MBClass.OBJECT) {
						s+=" extends ";
						if (tub instanceof IHasGenericParameters) {
							s+=((IHasGenericParameters)tub).getGenericName(usage,typeUsing);
						} else {
							s+=tub.getLocalName();
						}
					}
				} else {
					IType tub = (ub.size() > 0 ? ub.get(0) : MBClass.OBJECT);
					if (tub instanceof IHasGenericParameters) {
						s+=((IHasGenericParameters)tub).getGenericName(usage,typeUsing);
					} else {
						s+=tub.getLocalName();
					}
				}
			}
			if (i<parameters.size()-1)
				s+=",";
		}
		s+=">";
		return s;
	}

	public MBGenericParameter getParameter(int index) {
		return parameters.get(index);
	}
	
	@Override
	public void refreshTypes() {
		super.refreshTypes();
		for (MBGenericParameter gp : this.parameters) {
			gp.refreshTypes();
		}
	}
	
	public void createImports(MBNonPrimitiveType owner) {
		MBImport.create(this.getPathMapping(), owner);
		for (MBGenericParameter gp : this.parameters){
			for (IType ubt : gp.getUpperBounds()) {
				MBImport.create(ubt.getPathMapping(),owner);
			}
			for (IType ubt : gp.getLowerBounds()) {
				MBImport.create(ubt.getPathMapping(),owner);
			}
		}
	}
}