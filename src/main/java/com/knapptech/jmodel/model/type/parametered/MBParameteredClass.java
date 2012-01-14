package com.knapptech.jmodel.model.type.parametered;

import com.knapptech.jmodel.model.MBGenericParameter;
import com.knapptech.jmodel.model.element.MBConstructor;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.MBPrimitiveType;
import com.knapptech.jmodel.model.type.np.IClass;
import com.knapptech.jmodel.model.type.np.IHasGenericParameters;
import com.knapptech.jmodel.model.type.np.IInterface;
import com.knapptech.jmodel.model.type.np.MBClassWithParameters;
import com.knapptech.jmodel.model.type.np.MBEnum;


public class MBParameteredClass extends MBParameteredType implements IClass, IHasGenericParameters {
	
	public static final MBParameteredClass createHashSet(IType t) {
		if (t instanceof MBPrimitiveType) {
			t = ((MBPrimitiveType)t).getWrapper();
		}
		return new MBParameteredClass(MBClassWithParameters.HASHSET,t);
	}
	
	public static final MBParameteredClass createHashMap(IType k,IType v) {
		if (k instanceof MBPrimitiveType) {
			k = ((MBPrimitiveType)k).getWrapper();
		}
		if (v instanceof MBPrimitiveType) {
			v = ((MBPrimitiveType)v).getWrapper();
		}
		return new MBParameteredClass(MBClassWithParameters.HASHMAP,k,v);
	}
	
	public static final MBParameteredClass createHashTable(IType k,IType v) {
		if (k instanceof MBPrimitiveType) {
			k = ((MBPrimitiveType)k).getWrapper();
		}
		if (v instanceof MBPrimitiveType) {
			v = ((MBPrimitiveType)v).getWrapper();
		}
		return new MBParameteredClass(MBClassWithParameters.HASHTABLE,k,v);
	}
	
	public static final MBParameteredClass createArrayList(IType k) {
		if (k instanceof MBPrimitiveType) {
			k = ((MBPrimitiveType)k).getWrapper();
		}
		return new MBParameteredClass(MBClassWithParameters.ARRAYLIST,k);
	}
	
	public static final MBParameteredClass createVector(IType k) {
		if (k instanceof MBPrimitiveType) {
			k = ((MBPrimitiveType)k).getWrapper();
		}
		return new MBParameteredClass(MBClassWithParameters.VECTOR,k);
	}
	
	public static final MBParameteredClass createTreeSet(IType k) {
		if (k instanceof MBPrimitiveType) {
			k = ((MBPrimitiveType)k).getWrapper();
		}
		return new MBParameteredClass(MBClassWithParameters.TREESET,k);
	}
	
	public static final MBParameteredClass createTreeMap(IType k,IType v) {
		if (k instanceof MBPrimitiveType) {
			k = ((MBPrimitiveType)k).getWrapper();
		}
		if (v instanceof MBPrimitiveType) {
			v = ((MBPrimitiveType)v).getWrapper();
		}
		return new MBParameteredClass(MBClassWithParameters.TREEMAP,k,v);
	}
	
	public static final MBParameteredClass create(MBClassWithParameters clazz,IType... types) {
		return new MBParameteredClass(clazz,types);
	}
	
	public MBParameteredClass(MBClassWithParameters clazz) {
		if (clazz == null)
			throw new NullPointerException("Must define class.");
		this.type = clazz;
		init();
	}
	
	public MBParameteredClass(MBClassWithParameters clazz,IType... type) {
		if (clazz == null)
			throw new NullPointerException("Must define class.");
		this.type = clazz;
		init();
		int i = 0;
		for (MBGenericParameter prm : clazz.getParameters()) {
			String rep = prm.getRepresentation();
			setType(rep, type[i]);
			i++;
			if (i>=type.length)
				break;
		}
	}
	private MBClassWithParameters getClassWithParameters() {
		return (MBClassWithParameters)type;
	}
	public void addInterface(IInterface mbi) {
		getClassWithParameters().addInterface(mbi);
	}
	public boolean hasInnerClass(String localName) {
		return getClassWithParameters().hasInnerClass(localName);
	}
	public IInterface getInnerInterface(String localName) {
		return getClassWithParameters().getInnerInterface(localName);
	}
	public void refreshImports() {
		getClassWithParameters().refreshImports();
	}
	public void addConstructor(MBConstructor mbConstructor) {
		getClassWithParameters().addConstructor(mbConstructor);
	}

	public MBEnum getEnum(String localName) {
		return getClassWithParameters().getEnum(localName);
	}

	public void addEnum(MBEnum clz) {
		getClassWithParameters().addEnum(clz);
	}
}