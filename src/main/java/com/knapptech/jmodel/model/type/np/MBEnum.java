package com.knapptech.jmodel.model.type.np;

import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;

import com.knapptech.jmodel.model.GenericUsage;
import com.knapptech.jmodel.model.MBPackage;
import com.knapptech.jmodel.model.ProgrammingLanguage;
import com.knapptech.jmodel.model.Visibility;
import com.knapptech.jmodel.model.element.MBField;

public class MBEnum extends MBClass implements IEnum {
	public static final MBEnum ENUM = new MBEnum("Enum",MBPackage.JAVALANG,null);
	
	static {
		ENUM.getModifiers().setAbstract(true);
		ENUM.addInterface(MBInterface.SERIALIZABLE);
		ENUM.addInterface(MBInterfaceWithParameters.COMPARABLE);
	}
	
	public static MBEnum getOrCreate(String localName,MBPackage owner) {
		MBEnum c = owner.getEnum(localName);
		if (c != null)
			return c;
		MBEnum clz = new MBEnum(localName,owner,null);
		owner.addEnum(clz);
		return clz;
	}
	
	public static MBEnum getOrCreate(String localName,IClass owner) {
		MBEnum c = owner.getEnum(localName);
		if (c != null)
			return c;
		MBEnum clz = new MBEnum(localName,null,owner);
		owner.addEnum(clz);
		return clz;
	}

	protected MBEnum(String localName,MBPackage ownerPackage,
			IClass ownerClass) {
		super(localName,(localName.equals("Enum") ? OBJECT : ENUM),ownerPackage,ownerClass);
	}
	
	protected MBEnum(String localName,MBException superType, MBPackage ownerPackage,
			IClass ownerClass) {
		super(localName,(localName.equals("Enum") ? OBJECT : ENUM),ownerPackage,ownerClass);
	}
	
//	public static MBEnum create(String localName,MBPackage owner) {
//		if (owner.hasItemWithName(localName))
//			throw new IllegalArgumentException("Cannot create this class, the package already has something with that name.");
//		MBEnum clz = new MBEnum(localName,ENUM,owner,null);
//		owner.addClass(clz);
//		return clz;
//	}

	public int write(Writer writer, int currentIndent,ProgrammingLanguage language) throws IOException {
		return super.write(writer,currentIndent,language);
	}

	@Override
	protected int writeAsJava(Writer writer, int currentIndent) throws IOException {
		String tb = "";
		for (int i=0;i<currentIndent;i++) {
			tb+="\t";
		}
		this.refreshTypes();
		if (!this.getModifiers().isAbstract())
			createMissingMethods();
		refreshImports();
		writeHeaderAsJava(writer,currentIndent);
		String startLine = writeClassLineAsJava(writer, currentIndent);
		writer.write(tb+startLine);
		
		// write instances.
		boolean wroteFirst = false;
		for (MBField f : this.fields.values()) {
			if (!isEnumInstanceField(this, f))
				continue;
			// must be a real enum instance.
			if (wroteFirst)
				writer.write(",\n");
			writer.write(tb+"\t"+f.getName());
			if (f.getInitializer() != null && 
					f.getInitializer().getText() !=null && 
					f.getInitializer().getText().length()>0) {
				writer.write(" "+f.getInitializer().getText());
			}
			wroteFirst = true;
		}
		if (wroteFirst) {
			writer.write(";\n\n");
		}
		for (MBField f : this.fields.values()) {
			if (isEnumInstanceField(this, f))
				continue;
			// must not be a real enum instance.
			f.write(writer, currentIndent+1, ProgrammingLanguage.JAVA);
			writer.write("\n");
		}
		if (this.fields.values().size()<1)
			writer.write("\n");
		writeConstructorsAsJava(writer, currentIndent);
		writeMethodsAsJava(writer, currentIndent);
		writer.write(tb+"}");
		return 0;
	}
	
	private static boolean isEnumInstanceField(MBEnum enm,MBField field) {

		if (!field.getModifiers().getVisibility().equals(Visibility.PUBLIC))
			return false;
		if (!field.getModifiers().isStatic()) 
			return false;
		if (!field.getModifiers().isFinal())
			return false;
		if (!field.getType().equals(enm))
			return false;
		boolean hasLegitInitializer = (field.getInitializer()!=null && 
				field.getInitializer().getText()!=null && field.getInitializer().getText().length()>0);
		if (hasLegitInitializer) {
			String init = field.getInitializer().getText();
			if (!init.startsWith("(")) {
				// this cannot be an instance field.
				return false;
			}
		}
		return true;
	}

	@Override
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
		startLine+="enum ";
			startLine += localName+" ";
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
}