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
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;

import com.knapptech.jmodel.model.GenericUsage;
import com.knapptech.jmodel.model.MBImport;
import com.knapptech.jmodel.model.MBPackage;
import com.knapptech.jmodel.model.ProgrammingLanguage;
import com.knapptech.jmodel.model.element.MBField;
import com.knapptech.jmodel.model.element.MBMethod;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.parametered.MBParameteredInterface;

public class MBInterface extends MBNonPrimitiveType implements IInterface {
	public static final MBInterface SERIALIZABLE = getOrCreate("Serializable",MBPackage.JAVAIO);
	public static final MBInterface CLONEABLE = getOrCreate("Cloneable",MBPackage.JAVALANG);
	public static final MBInterface READABLE = getOrCreate("Readable",MBPackage.JAVALANG);
	public static final MBInterface RUNNABLE = getOrCreate("Runnable",MBPackage.JAVALANG);
	
	protected MBInterface(String localName,MBPackage ownerPackage) {
		super(localName,null, ownerPackage, null);
		System.out.println("Created interface "+localName);
	}
	
	public static MBInterface getOrCreate(String localName,MBPackage ownerPackage) {
		if (localName.contains("."))
			throw new IllegalArgumentException("local name cannot contain a period.");
		MBInterface mbi = ownerPackage.getInterface(localName);
		if (mbi != null)
			return mbi;
		mbi = new MBInterface(localName, ownerPackage);
		mbi.ownerPackage.addInterface(mbi);
		return mbi;
	}
	
	public static MBInterface getOrCreate(String localName,IClass ownerClass) {
		if (localName.contains("."))
			throw new IllegalArgumentException("local name cannot contain a period.");
		MBInterface mbi = (MBInterface) ownerClass.getInnerInterface(localName);
		if (mbi != null)
			return mbi;
		mbi = new MBInterface(localName, ownerClass);
		mbi.ownerPackage.addInterface(mbi);
		return mbi;
	}
	
	protected MBInterface(String name,
			IClass ownerClass) {
		super(name,null, null, ownerClass);
	}
	
	public static MBParameteredInterface createComparableTo(IType t) {
		return MBParameteredInterface.createComparableTo(t);
	}
	
	@Override
	public boolean isAbstract() {
		return false;
	}

	@Override
	public boolean isFinal() {
		return false;
	}

	public int write(Writer writer, int currentIndent,ProgrammingLanguage language) throws IOException {
		refreshTypes();
		refreshImports();
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

	private int writeAsJava(Writer writer, int currentIndent) throws IOException {
		if (this.ownerClass == null) {
			currentIndent = 0;
			
			writer.write("package "+this.ownerPackage.getFullyQualifiedName()+";\n\n");
			for (MBImport mp : this.importStatements.values()) {
				if (!mp.getPathMapping().equals(this.ownerPackage.getPathMapping()))
					mp.write(writer, currentIndent, ProgrammingLanguage.JAVA);
			}
			if (this.importStatements.size()>0)
				// add a line to keep things neat.
				writer.write("\n");
		}
		this.comment.write(writer, currentIndent, ProgrammingLanguage.JAVA);
		String startLine = "";
		startLine += this.getModifiers().getVisibility().toString();
		if (startLine.length()>0) startLine+=" ";
		startLine+="interface "+localName+" ";
		if (this.implementedInterfaces.size()>0) {
			startLine+="extends ";
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
		writer.write(startLine);
		for (MBField field : this.fields.values()) {
			field.write(writer, currentIndent+1, ProgrammingLanguage.JAVA);
		}
		if (this.fields.size()>0)
			writer.write("\n");
		for (MBMethod mtd : this.methods.values()) {
			mtd.write(writer, currentIndent+1, ProgrammingLanguage.JAVA);
		}
		if (this.methods.size()>0)
			writer.write("\n");
		writer.write("}");
		return 0;
	}

	public Collection<? extends MBMethod> getMethods() {
		return Collections.unmodifiableCollection(this.methods.values());
	}

	public Collection<? extends MBMethod> getMethodsRecursively() {
		HashSet<MBMethod> mms = new HashSet<MBMethod>();
		for (IInterface nf : this.implementedInterfaces.values()) {
			if (!(nf instanceof MBInterface))
				continue;
			MBInterface mbi = (MBInterface)nf;
			mms.addAll(mbi.getMethodsRecursively());
		}
		for (MBMethod mtd : this.methods.values()) {
			mms.add(mtd);
		}
		return Collections.unmodifiableCollection(mms);
	}

	public static void init() {
		// do not delete, here to ensure the class is statically initialized so path mappings are created.
	}

	public void print(int i) {
		String tb = "";
		for (int j = 0;j<i;j++) {
			tb+="\t";
		}
		System.out.println(tb+"Interface: \""+this.localName+"\": ");
		System.out.println(tb+"  Path: "+this.path.getFullyQualifiedName()+"");
		System.out.println(tb+"  Comment: "+this.comment.getText());
	}
}