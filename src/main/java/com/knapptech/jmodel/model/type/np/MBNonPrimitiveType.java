package com.knapptech.jmodel.model.type.np;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import com.knapptech.jmodel.model.IWritable;
import com.knapptech.jmodel.model.MBDocComment;
import com.knapptech.jmodel.model.MBImport;
import com.knapptech.jmodel.model.MBMethodSignature;
import com.knapptech.jmodel.model.MBModifiers;
import com.knapptech.jmodel.model.MBPackage;
import com.knapptech.jmodel.model.MBPathMapping;
import com.knapptech.jmodel.model.element.MBField;
import com.knapptech.jmodel.model.element.MBMethod;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.MBType;
import com.knapptech.jmodel.model.type.delayed.IDelayedType;

public abstract class MBNonPrimitiveType extends MBType implements IWritable {
	protected TreeMap<String,MBField> fields = new TreeMap<String,MBField>();
	protected TreeMap<MBMethodSignature,MBMethod> methods = new TreeMap<MBMethodSignature,MBMethod>();
	protected IClass superType;
	protected TreeMap<MBPathMapping,IInterface> implementedInterfaces = new TreeMap<MBPathMapping, IInterface>();
	protected MBModifiers typeModifiers = new MBModifiers();
	protected MBPackage ownerPackage;
	protected IClass ownerClass;
	protected TreeMap<MBPathMapping,MBImport> importStatements = new TreeMap<MBPathMapping, MBImport>();
	protected MBDocComment comment = new MBDocComment();
	private IType comparableTo = null;
	
	public void addField(MBField field) {
		if (field == null)
			throw new NullPointerException("Cannot use a null field.");
		if (field.getName()==null || field.getName().length()<1)
			throw new IllegalArgumentException("Must provide a field with a name.");
		if (fields.containsKey(field.getName())) {
			MBField f = fields.get(field.getName());
			if (f == field) 
				// just abort, no exception
				return;
			throw new IllegalStateException("Already has a field with that name, you cannot redefine it.");
		}
		fields.put(field.getName(),field);
	}

	public void addMethod(MBMethod method) {
		if (method == null)
			throw new NullPointerException("Cannot use a null method.");
		if (method.getName()==null || method.getName().length()<1)
			throw new IllegalArgumentException("Must provide a method with a name.");
		MBMethodSignature sgn = method.createSignature();
		if (methods.containsKey(sgn)) {
			MBMethod m = methods.get(sgn);
			if (m == method) 
				// just abort, no exception
				return;
			throw new IllegalStateException("The application attempted to redefine the method "+
				"\""+m.getName()+"\" in the type \""+getLocalName()+"\", this is not allowed.");
		}
		methods.put(sgn,method);
	}
	
	public boolean hasMethod(MBMethod method) {
		if (method == null) 
			throw new NullPointerException("Cannot use a null method.");
		if (method.getName()==null || method.getName().length()<1)
			throw new IllegalArgumentException("Must provide a method with a name.");
		MBMethodSignature sgn = method.createSignature();
		return (methods.containsKey(sgn));
	}

	public void addImplementedInterface(IInterface implementedInterface) {
		if (implementedInterfaces.containsKey(implementedInterface.getPathMapping())) {
			IInterface nf = implementedInterfaces.get(implementedInterface.getPathMapping());
			if (nf==implementedInterface) {
				// no problem, just abort.
				return;
			}
			if (!(nf instanceof IDelayedType)) {
				throw new IllegalArgumentException("The class already has implemented an interface with that name.\n\t"+
						implementedInterface.getFullyQualifiedName());
			}
		}
		implementedInterfaces.put(implementedInterface.getPathMapping(), implementedInterface);
	}
	
	public boolean hasField(String name) {
		return fields.containsKey(name);
	}
	
	public MBField getField(String name) {
		return fields.get(name);
	}
	
	protected MBNonPrimitiveType(String localName,IClass superType,MBPackage ownerPackage,IClass ownerClass) {
		if (ownerPackage == null && ownerClass == null)
			throw new NullPointerException("Must specify an owner package or class.");
		if (localName.contains("."))
			throw new IllegalArgumentException("local name cannot contain a period.");
		if (superType == null) {
			if (!(this instanceof MBInterface)) {
				if (ownerPackage != MBPackage.JAVALANG || !localName.equals("Object"))
					superType = MBClass.OBJECT;
			}
		}
		if ((superType != null && superType.getPathMapping().getFullyQualifiedName().equals("java.lang.Enum")) && 
				!(this instanceof MBEnum) ) {
			throw new IllegalStateException("Only the MBEnum class can use a super type of java.lang.Enum.");
		}
		if (ownerPackage != null && ownerPackage.hasItemWithName(localName))
			throw new IllegalStateException("The package already has an item with that name.");
		if (ownerClass != null && ownerClass.hasInnerClass(localName))
			throw new IllegalStateException("The package already has an item with that name.");
		this.localName = localName;
		this.superType = superType;
		this.ownerPackage = ownerPackage;
		this.ownerClass = ownerClass; // can safely be null, just used for inner classes.
		this.path = ownerPackage.getPathMapping().createChild(this, this.localName,false);
	}
	
	public IClass getSuperType() {
		return superType;
	}
	
	public MBModifiers getModifiers() {
		return typeModifiers;
	}
	
	public Collection<IInterface> getImplementedInterfaces() {
		return Collections.unmodifiableCollection(this.implementedInterfaces.values());
	}
	
	@Override
	public boolean isIterable() {
		if (this.path == MBPathMapping.ITERABLE)
			return true;
		IClass c = superType;
		if (c != null && c.isIterable())
			return true;
		for (IInterface mbi : implementedInterfaces.values()) {
			if (mbi.isIterable())
				return true;
			if (mbi.getPathMapping()==MBPathMapping.ITERABLE)
				return true;
		}
		return false;
	}

	@Override
	public Collection<MBImport> getRequiredImports() {
		return Collections.unmodifiableCollection(importStatements.values());
	}

	@Override
	public boolean isAbstract() {
		return this.typeModifiers.isAbstract();
	}
	
	@Override
	public boolean isFinal() {
		return this.typeModifiers.isFinal();
	}

	@Override
	public boolean isComparable() {
		if (this.path == MBPathMapping.COMPARABLE)
			return true;
		if (superType != null && superType.isComparable())
			return true;
		for (IInterface mbi : implementedInterfaces.values()) {
			if (mbi.getPathMapping()==MBPathMapping.COMPARABLE)
				return true;
			if (mbi.isComparable())
				return true;
		}
		return false;
	}

	@Override
	public boolean isClonable() {
		if (this.path == MBPathMapping.CLONABLE)
			return true;
		if (superType != null && superType.isClonable())
			return true;
		for (IInterface mbi : implementedInterfaces.values()) {
			if (mbi.getPathMapping()==MBPathMapping.CLONABLE)
				return true;
			if (mbi.isClonable())
				return true;
		}
		return false;
	}

	@Override
	public boolean isSerializable() {
		if (this.path == MBPathMapping.SERIALIZABLE)
			return true;
		if (superType != null && superType.isSerializable())
			return true;
		for (IInterface mbi : implementedInterfaces.values()) {
			if (mbi.getPathMapping()==MBPathMapping.SERIALIZABLE)
				return true;
			if (mbi.isSerializable())
				return true;
		}
		return false;
	}

	@Override
	public IType comparesTo() {
		if (!isComparable())
			return null;
		if (comparableTo != null)
			return comparableTo;
		if (this.superType != null && this.superType.isComparable()) {
			this.comparableTo = superType.comparesTo();
			return comparableTo;
		}
		for (IInterface mbi : this.implementedInterfaces.values()) {
			if (!mbi.isComparable())
				continue;
			this.comparableTo = mbi.comparesTo();
			return comparableTo;
		}
		return comparableTo;
	}

	public void setComparableTo(MBType comparableTo) {
		this.comparableTo = comparableTo;
	}
	
	public void refreshTypes() {
		if (superType instanceof IDelayedType) {
			IDelayedType dt = (IDelayedType)superType;
			if (dt.isFound()) {
				superType = (IClass) dt.getFoundType();
			}
		}
		if (ownerClass instanceof IDelayedType) {
			IDelayedType dt = (IDelayedType)ownerClass;
			if (dt.isFound()) {
				ownerClass = (IClass) dt.getFoundType();
			}
		}
		if (comparableTo instanceof IDelayedType) {
			IDelayedType t = (IDelayedType)comparableTo;
			if (t.isFound())
				this.comparableTo = t.getFoundType();
		}
		for (MBPathMapping mpng : this.implementedInterfaces.keySet()) {
			IInterface nfc = this.implementedInterfaces.get(mpng);
			if (nfc instanceof IDelayedType) {
				IDelayedType t = (IDelayedType)nfc;
				if (t.isFound())
					this.implementedInterfaces.put(mpng, (IInterface) t.getFoundType());
				
			}
		}
		for (MBField fld : this.fields.values()) 
			fld.refreshType();
		for (MBMethod mtd : this.methods.values()) 
			mtd.refreshTypes();
	}
	
	public MBNonPrimitiveType getOwnerClass() {
		return (MBNonPrimitiveType) ownerClass;
	}
	
	public void refreshImports() {
		refreshTypes();
		MBNonPrimitiveType t = this;
		while (t.ownerClass != null)
			t = (MBNonPrimitiveType) t.ownerClass;
		if (superType != null)
			MBImport.create(superType.getPathMapping(),t);
		if (comparableTo != null)
			MBImport.create(comparableTo.getPathMapping(),t);
		if (ownerClass != null)
			MBImport.create(ownerClass.getPathMapping(),t);
		for (MBField fld : this.fields.values()) 
			fld.createImports();
		for (MBMethod mtd : this.methods.values()) 
			mtd.createImports();
		for (IInterface mbi : this.implementedInterfaces.values()) {
			MBImport.create(mbi.getPathMapping(), t);
		}
	}

	public void addImport(MBImport mbImport) {
		if (mbImport == null || mbImport.getPathMapping() == null || mbImport.getPathMapping() == MBPathMapping.DEFAULTPATH)
			return;
		if (this.ownerPackage == null)
			return;
//		if (mbImport.getPathMapping().getElement() instanceof MBGenericParameter)
//			return;
		if (this.importStatements.containsKey(mbImport.getPathMapping())) {
			MBImport mprt = this.importStatements.get(mbImport.getPathMapping());
			if (mprt == mbImport)
				return;
			throw new IllegalArgumentException("Already has this import, you cannot redefine it.");
		}
		if (mbImport.getPackagePath().equals(this.ownerPackage.getPath())) {
			return;
		}
		if (mbImport.getPackagePath().equals(MBPackage.JAVALANG.getPath()))
			return;
		this.importStatements.put(mbImport.getPathMapping(),mbImport);
	}

	public boolean hasImport(MBPathMapping path) {
		return (this.importStatements.containsKey(path));
	}

	public MBImport getImport(MBPathMapping path) {
		return this.importStatements.get(path);
	}
	
	public void setComment(String comment) {
		this.comment = new MBDocComment(comment);
	}

	public boolean hasImplementedInterface(IInterface cprbl) {
		return (this.implementedInterfaces.containsKey(cprbl.getPathMapping()));
	}
}