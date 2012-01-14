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
package com.knapptech.jmodel.model.type.np;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import com.knapptech.jmodel.model.GenericUsage;
import com.knapptech.jmodel.model.MBImport;
import com.knapptech.jmodel.model.MBMethodSignature;
import com.knapptech.jmodel.model.MBPackage;
import com.knapptech.jmodel.model.MBParameter;
import com.knapptech.jmodel.model.ProgrammingLanguage;
import com.knapptech.jmodel.model.element.MBConstructor;
import com.knapptech.jmodel.model.element.MBField;
import com.knapptech.jmodel.model.element.MBMethod;
import com.knapptech.jmodel.model.type.MBPrimitiveType;
import com.knapptech.jmodel.model.type.MBPrimitiveWrapper;
import com.knapptech.jmodel.model.type.delayed.IDelayedType;

public class MBClass extends MBNonPrimitiveType implements IClass {
	public static final MBClass OBJECT = new MBClass("Object",null,MBPackage.JAVALANG,null);
	public static final MBClass THROWABLE = new MBClass("Throwable",OBJECT,MBPackage.JAVALANG,null);
	
	static {
		
		
	}
	
	protected List<MBConstructor> constructors = new ArrayList<MBConstructor>();
	protected TreeMap<String,IClass> innerClasses = new TreeMap<String,IClass>();
	protected TreeMap<String,IInterface> innerInterfaces = new TreeMap<String,IInterface>();
	protected TreeMap<String,MBEnum> innerEnums = new TreeMap<String,MBEnum>();
	
	protected MBClass(String localName,IClass superType, MBPackage ownerPackage,
			IClass ownerClass) {
		super(localName,superType,ownerPackage,ownerClass);
		System.out.println("Created class "+localName);
		if (localName.equals("PrimitiveApe")) {
			Thread.dumpStack();
		}
	}
	
	public static MBClass getOrCreate(String localName,MBPackage owner) {
		MBClass c = owner.getClass(localName);
		if (c != null)
			return c;
		MBClass clz = new MBClass(localName,OBJECT,owner,null);
		owner.addClass(clz);
		return clz;
	}
	
	public static MBClass getOrCreate(String localName,IClass superClass,MBPackage owner) {
		MBClass c = owner.getClass(localName);
		if (c != null)
			return c;
		if (superClass == null)
			superClass = OBJECT;
		MBClass clz = new MBClass(localName,superClass,owner,null);
		owner.addClass(clz);
		return clz;
	}

	public void addConstructor(MBConstructor constructor) {
		if (constructor == null)
			throw new NullPointerException("Cannot use a null field.");
		if (constructor.getName()==null || constructor.getName().length()<1)
			throw new IllegalArgumentException("Must provide a field with a name.");
		if (constructors.contains(constructor)) {
			for (MBConstructor cns : constructors) {
				if (cns == constructor) 
					// just abort, no exception
					return;
			}
			throw new IllegalStateException("The application attempted to add a redundant constructor to "+
					"the class \""+getLocalName()+"\", this is not allowed.");
		}
		constructors.add(constructor);
	}
	
	public void addInterface(IInterface mbi) {
		if (this.innerInterfaces.containsKey(mbi.getLocalName())) {
			IInterface m = this.innerInterfaces.get(mbi.getLocalName());
			if (m == mbi)
				return;
			if (! (m instanceof IDelayedType)) {
				throw new IllegalStateException("This already has an interface with that name, you cannot redefine it.");
			}
		}
		this.innerInterfaces.put(mbi.getLocalName(),mbi);
	}
	
	public void addEnum(MBEnum enm) {
		if (this.innerEnums.containsKey(enm.getLocalName())) {
			MBEnum m = this.innerEnums.get(enm.getLocalName());
			if (m == enm)
				return;
			if (! (m instanceof IDelayedType)) {
				throw new IllegalStateException("This already has an enum with that name, you cannot redefine it.");
			}
		}
		this.innerEnums.put(enm.getLocalName(),enm);
	}
	
	public void addClass(MBClass clazz) {
		if (this.innerClasses.containsKey(clazz.getLocalName())) {
			IClass m = this.innerClasses.get(clazz.getLocalName());
			if (m == clazz)
				return;
			if (! (m instanceof IDelayedType)) {
				throw new IllegalStateException("This already has a class with that name, you cannot redefine it.");
			}
		}
		this.innerClasses.put(clazz.getLocalName(),clazz);
	}
	
	public void refreshImports() {
		super.refreshImports();
		for (MBConstructor cns : this.constructors) 
			cns.createImports();
		for (IClass ic : this.innerClasses.values()) {
			ic.refreshImports();
		}
		for (IInterface ic : this.innerInterfaces.values()) {
			ic.refreshImports();
		}
		for (MBEnum ic : this.innerEnums.values()) {
			ic.refreshImports();
		}
	}

	public int write(Writer writer, int currentIndent,ProgrammingLanguage language) throws IOException {
		if (language == ProgrammingLanguage.JAVA) {
			return writeAsJava(writer,currentIndent);
		} else if (language == ProgrammingLanguage.JAVASCRIPT) {
			throw new UnsupportedOperationException("Can only code in java presently.");
		} else if (language == ProgrammingLanguage.CSHARP) {
			throw new UnsupportedOperationException("Can only code in java presently.");
		} else if (language == ProgrammingLanguage.JAVA) {
			throw new UnsupportedOperationException("Can only code in java presently.");
		}
		return 0;
	}

	protected int writeAsJava(Writer writer, int currentIndent) throws IOException {
		this.refreshTypes();
		if (!this.getModifiers().isAbstract())
			createMissingMethods();
		refreshImports();
		writeHeaderAsJava(writer,currentIndent);
		String startLine = writeClassLineAsJava(writer, currentIndent);
		writer.write(startLine);
		writeFieldsAsJava(writer, currentIndent);
		writeConstructorsAsJava(writer, currentIndent);
		writeMethodsAsJava(writer, currentIndent);
		writeInnersAsJava(writer, currentIndent);
		writer.write("}");
		return 0;
	}

	protected void writeFieldsAsJava(Writer writer, int currentIndent)
			throws IOException {
		for (MBField field : this.fields.values()) {
			field.write(writer, currentIndent+1, ProgrammingLanguage.JAVA);
		}
		if (this.fields.size()>0)
			writer.write("\n");
	}

	protected void writeConstructorsAsJava(Writer writer, int currentIndent)
			throws IOException {
		for (MBConstructor cns : this.constructors) {
			cns.write(writer, currentIndent+1, ProgrammingLanguage.JAVA);
		}
		if (this.constructors.size()>0)
			writer.write("\n");
	}

	protected void writeMethodsAsJava(Writer writer, int currentIndent)
			throws IOException {
		for (MBMethod mtd : this.methods.values()) {
			mtd.write(writer, currentIndent+1, ProgrammingLanguage.JAVA);
		}
		if (this.methods.size()>0)
			writer.write("\n");
	}

	protected void writeInnersAsJava(Writer writer, int currentIndent)
			throws IOException {
		for (IClass clz : this.innerClasses.values()) {
			clz.write(writer, currentIndent+1, ProgrammingLanguage.JAVA);
		}
		if (this.innerClasses.size()>0)
			writer.write("\n");
		for (IInterface clz : this.innerInterfaces.values()) {
			clz.write(writer, currentIndent+1, ProgrammingLanguage.JAVA);
		}
		if (this.innerInterfaces.size()>0)
			writer.write("\n");
		for (MBEnum clz : this.innerEnums.values()) {
			clz.write(writer, currentIndent+1, ProgrammingLanguage.JAVA);
		}
		if (this.innerEnums.size()>0)
			writer.write("\n");
	}

	protected String writeClassLineAsJava(Writer writer, int currentIndent)
			throws IOException {
		this.comment.write(writer, currentIndent, ProgrammingLanguage.JAVA);
		String startLine = "";
		startLine += this.getModifiers().getVisibility().toString();
		if (startLine.length()>0) startLine+=" ";
		if (this.ownerClass != null) {
			if (this.getModifiers().isStatic())
				startLine += "static ";
		}
		if (this.getModifiers().isAbstract())
			startLine += "abstract ";
		if (this.getModifiers().isFinal())
			startLine += "final ";
		startLine+="class ";//+localName+" ";
		if (this instanceof IHasGenericParameters) {
			startLine += ((IHasGenericParameters)this).getGenericName(GenericUsage.CLASSDECLARATION, this)+" ";
		} else {
			startLine += localName+" ";
		}
		if (this.superType != null && this.superType != OBJECT) {
			startLine += "extends "+this.superType.getLocalName()+" ";
		}
		if (this.implementedInterfaces.size()>0) {
			startLine+="implements ";
			int sz = this.implementedInterfaces.size();
			Iterator<IInterface> iter = this.implementedInterfaces.values().iterator();
			int i = 0;
			while (iter.hasNext()) {
				IInterface nfc = iter.next();
				if (nfc instanceof IHasGenericParameters) {
					startLine+=((IHasGenericParameters)nfc).getGenericName(GenericUsage.CLASSDECLARATION, this);
				} else {
					startLine+=nfc.getLocalName();
				}
				i++;
				if (i<sz)
					startLine+=", ";
				else 
					startLine+=" ";
			}
		}
		startLine+="{\n";
		return startLine;
	}
	
	protected void writeHeaderAsJava(Writer writer,int currentIndent) throws IOException {
		if (this.ownerClass == null) {
			currentIndent = 0;
			
			writer.write("package "+this.ownerPackage.getFullyQualifiedName()+";\n\n");
			for (MBImport mp : this.importStatements.values()) {
				if (!mp.getPathMapping().equals(this.ownerPackage.getPathMapping()))
					mp.write(writer, currentIndent, ProgrammingLanguage.JAVA);
			}
			if (this.importStatements.size()>0)
				// add an extra line to keep things neat.
				writer.write("\n");
		}
	}

	protected void createMissingMethods() {
		List<MBMethod> missingMethods = getMissingMethods(true);
		for (MBMethod m : missingMethods) {
			if (this.hasMethod(m))
				continue;
			MBMethod myImplementation = new MBMethod(this,m);
			if (myImplementation.getSourceCode().lines()>0)
				continue;
			myImplementation.getSourceCode().addLine("// TODO write the \""+myImplementation.getName()+
					"\" method for the \""+getLocalName()+"\" class.");
			if (m.getReturnType()==null)
				continue;
			// need to add a default return.
			if (m.getReturnType().isPrimitive()) {
				MBPrimitiveType t = null;
				if (m.getReturnType() instanceof MBPrimitiveType)
					t = (MBPrimitiveType)m.getReturnType();
				else 
					t = ((MBPrimitiveWrapper)m.getReturnType()).getPrimitive();
				if (t == MBPrimitiveType.BOOLEAN) {
					myImplementation.getSourceCode().add("return false;");
				} else if (t== MBPrimitiveType.CHAR) {
					myImplementation.getSourceCode().add("return 'a';");
				} else if (t.isNumberType()) {
					myImplementation.getSourceCode().add("return ("+t.getLocalName()+")0;");
				} else {
					assert(false);
					throw new IllegalStateException("Never identified primitive type.");
				}
			} else {
				myImplementation.getSourceCode().add("return null;");
			}
		}
	}

	public List<MBMethod> getDefinedMethods(boolean includeMethodsYouWillDefine) {
		ArrayList<MBMethod> mms = new ArrayList<MBMethod>();
		if (this.superType != null && this.superType instanceof MBClass) {
			List<MBMethod> superDefined = ((MBClass)this.superType).getDefinedMethods(true);
			for (MBMethod mtd : superDefined){ 
				if (mms.contains(mtd))
					continue;
				mms.add(mtd);
			}
		}
		for (MBMethod m : this.methods.values()) {
			if (m.getModifiers().isAbstract())
				continue;
			mms.add(m);
		}
		if (this.getModifiers().isAbstract() || !includeMethodsYouWillDefine)
			return mms;
		// this class is not abstract, and
		// the user wants us to include methods that we will define.
		// so find interface methods we don't have.
		for (IInterface mbi : this.implementedInterfaces.values()) {
			if (!(mbi instanceof MBInterface))
				continue;
			MBInterface nf = (MBInterface)mbi;
			Collection<? extends MBMethod> nfms = nf.getMethodsRecursively();
			for (MBMethod mtd : nfms) {
				if (mms.contains(mtd))
					continue;
				mms.add(mtd);
			}
		}
		return mms;
	}

	public List<MBMethod> getMissingMethods(boolean includeMethodsYouWillImplement) {
		ArrayList<MBMethod> mms = new ArrayList<MBMethod>();
		if (this.getModifiers().isAbstract()) {
			for (MBMethod mtd : this.methods.values()) {
				if (mtd.getModifiers().isAbstract() && !mms.contains(mtd))
					mms.add(mtd);
			}
		}
		if (this.superType != null && this.superType instanceof MBClass) {
			List<MBMethod> superMissing = ((MBClass)this.superType).getMissingMethods(false);
			for (MBMethod mtd : superMissing){ 
				if (mms.contains(mtd))
					continue;
				mms.add(mtd);
			}
		}
		for (IInterface mbi : this.implementedInterfaces.values()) {
			if (!(mbi instanceof MBInterface))
				continue;
			MBInterface nf = (MBInterface)mbi;
			Collection<? extends MBMethod> nfms = nf.getMethodsRecursively();
			for (MBMethod mtd : nfms) {
				if (mms.contains(mtd))
					continue;
				mms.add(mtd);
			}
		}
		List<MBMethod> dms = getDefinedMethods(!includeMethodsYouWillImplement);
		Iterator<MBMethod> miter = mms.iterator();
		while (miter.hasNext()) {
			MBMethod mtd = miter.next();
			MBMethodSignature ms = mtd.createSignature();
			for (MBMethod m : dms) {
				if (m.createSignature().equals(ms)) {
					miter.remove();
					break;
				}
			}
		}
		return mms;
	}

	public boolean hasInnerClass(String name) {
		return innerClasses.containsKey(name);
	}
	
	public boolean hasInnerInterface(String name) {
		return innerInterfaces.containsKey(name);
	}

	public boolean hasInnerEnum(String name) {
		return innerEnums.containsKey(name);
	}

	public void refreshTypes() {
		super.refreshTypes();
		for (MBConstructor cns : this.constructors) {
			cns.refreshTypes();
		}
		for (IClass c : innerClasses.values()) {
			if (c instanceof IDelayedType) {
				
			}
			c.refreshTypes();
		}
		for (IInterface c : innerInterfaces.values()) {
			c.refreshTypes();
		}
		for (MBEnum c : innerEnums.values()) {
			c.refreshTypes();
		}
	}

	public IInterface getInnerInterface(String localName) {
		return this.innerInterfaces.get(localName);
	}

	public static void init() {
		// do not delete, here to ensure the class is statically initialized so path mappings are created.
	}

	public MBEnum getEnum(String localName) {
		return innerEnums.get(localName);
	}

	public void print(int i) {
		String tb = "";
		for (int j = 0;j<i;j++) {
			tb+="\t";
		}
		System.out.println(tb+(this instanceof MBEnum ? "Enum" : "Class") +" \""+this.localName+"\": ");
		System.out.println(tb+"  Path: "+this.path.getFullyQualifiedName()+"");
		System.out.println(tb+"  Comment: "+this.comment.getText());
		for (IClass p : this.innerClasses.values()) {
			p.print(i+1);
		}
		for (IInterface c : this.innerInterfaces.values()) {
			c.print(i+1);
		}
		for (MBEnum c : this.innerEnums.values()) {
			c.print(i+1);
		}
	}

	public MBConstructor getConstructor() {
		for (MBConstructor c : this.constructors) {
			if (c.getParameters().size()<1)
				return c;
		}
		return null;
	}

	public MBConstructor getConstructor(MBParameter[] parms) {
		if (parms == null || parms.length<1)
			return getConstructor();
		for (MBConstructor c : this.constructors) {
			if (parms.length != c.getParameters().size())
				continue;
			int i=0;
			boolean match = true;
			for (MBParameter p : c.getParameters()) {
				if (!p.getType().equals(parms[i++].getType())) {
					match = false;
				}
			}
			if (match)
				return c;
		}
		return null;
	}
}