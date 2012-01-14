package com.knapptech.jmodel.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.TreeMap;

import com.knapptech.jmodel.model.type.MBPrimitiveWrapper;
import com.knapptech.jmodel.model.type.np.MBClass;
import com.knapptech.jmodel.model.type.np.MBEnum;
import com.knapptech.jmodel.model.type.np.MBInterface;

public class MBPackage implements IPathItem, Comparable<IPathItem> {
	public static final MBPackage DEFAULT = createDefaultPackage();
	public static final MBPackage JAVA = getOrCreate("java");
	public static final MBPackage JAVALANG = getOrCreate("java","lang");
	public static final MBPackage JAVAIO = getOrCreate("java","io");
	public static final MBPackage JAVAUTIL = getOrCreate("java","util");
	
	private TreeMap<MBPathMapping,MBPackage> subPackages = new TreeMap<MBPathMapping,MBPackage>();
	private TreeMap<MBPathMapping,MBClass> classes = new TreeMap<MBPathMapping,MBClass>();
	private TreeMap<MBPathMapping,MBInterface> interfaces = new TreeMap<MBPathMapping,MBInterface>();
	private TreeMap<MBPathMapping,MBPrimitiveWrapper> wrappers = new TreeMap<MBPathMapping,MBPrimitiveWrapper>();
	private MBPackage parent = null;
	private String localName;
	private MBPathMapping path;
	private MBDocComment comment = new MBDocComment();
	
	private MBPackage() {}
	
	private static final MBPackage createDefaultPackage() {
		MBPackage defaultPackage = new MBPackage();
		defaultPackage.localName = "";
		defaultPackage.path = MBPathMapping.reserve(defaultPackage,MBPath.createPackagePath());
		System.out.println("Created default package");
		return defaultPackage;
	}
	
	public MBClass getClass(String localName) {
		for (MBClass cl : classes.values()) {
			if (cl.getLocalName().equals(localName))
				return cl;
		}
		return null;
	}
	
	public MBInterface getInterface(String localName) {
		for (MBInterface cl : interfaces.values()) {
			if (cl.getLocalName().equals(localName))
				return cl;
		}
		return null;
	}
	
	public boolean hasClass(String name) {
		return getClass(name) != null;
	}
	
	public boolean hasInterface(String name) {
		return getInterface(name) != null;
	}
	
	public boolean hasItemWithName(String name) {
		return hasClass(name) || hasInterface(name);
	}
	
	public MBPathMapping getPathMapping() {
		return path;
	}

	public static final MBPackage getOrCreate(MBPath path) {
		return getOrCreate(path.getPackageParts());
	}
	
	public static final MBPackage getOrCreate(MBPathPart[] parts) {
		String[] ps = new String[parts.length];
		for (int i = 0;i<parts.length;i++) {
			ps[i]=parts[i].getLocalName();
		}
		return getOrCreate(ps);
	}
	
	public static final MBPackage getOrCreate(String... parts) {
		if (parts == null || parts.length==0 || 
				(parts.length == 1 && 
					(parts[0] == null || parts[0].length()<1))) {
				return DEFAULT;
		}
		for (String part : parts) {
			if (part.contains("."))
				throw new IllegalArgumentException("package names must not contain a period.");
		}
		MBPath path = MBPath.createPackagePath(parts);
		MBPathMapping mpng = MBPathMapping.getOrCreate(path);
		if (mpng.getElement() != null)
			return (MBPackage)mpng.getElement();
		String localName = parts[parts.length-1];
		if (!NamingScheme.isAllowedForPackage(localName))
			throw new IllegalArgumentException("Illegal name "+localName);
		if (localName == null || localName.length()<1)
			throw new IllegalArgumentException("Must not have a null/empty local name.");
		MBPackage p = new MBPackage();
		p.localName = localName;
		System.out.println("Created package "+localName);
		p.path = mpng;
		if (parts.length>1) {
			String[] parentParts = new String[parts.length-1];
			System.arraycopy(parts, 0, parentParts, 0, parentParts.length);
			p.parent = getOrCreate(parentParts);
		} else {
			p.parent = DEFAULT;
		}
		// the default package has a null parent.  
		if (p.parent != null)
			p.parent.subPackages.put(p.parent.getPathMapping().createChild(p, localName,true), p);
		return p;
	}
	
	public final MBPackage getOrCreateSubPackage(String subPackageLocalName) {
		MBPackage p = getSubPackage(subPackageLocalName);
		if (p != null)
			return p;
		if (!NamingScheme.isAllowedForPackage(subPackageLocalName))
			throw new IllegalArgumentException("Illegal name "+subPackageLocalName);
		if (subPackageLocalName == null || subPackageLocalName.length()<1)
			throw new IllegalArgumentException("Illegal name "+subPackageLocalName);
		p = new MBPackage();
		p.parent = this;
		p.path = this.getPathMapping().createChild(p, subPackageLocalName,true);
		this.subPackages.put(p.path, p);
		p.localName = subPackageLocalName;
		System.out.println("Created package "+subPackageLocalName);
		return p;
	}
	
	public static final MBPackage get(String... parts) {
		if (parts == null || parts.length==0 || 
				(parts.length == 1 && 
					(parts[0] == null || parts[0].length()<1))) {
				return DEFAULT;
		}
		for (String part : parts) {
			if (part.contains("."))
				throw new IllegalArgumentException("package names must not contain a period.");
		}
		MBPackage cp = DEFAULT;
		for (int i = 0;i<parts.length;i++) {
			cp = cp.getSubPackage(parts[i]);
			if (cp == null)
				return null;
		}
		return cp;
	}
	
	public MBPackage getSubPackage(String subPackageLocalName) {
		if (subPackageLocalName == null || subPackageLocalName.length()<1)
			throw new NullPointerException("Must provide a valid package name.");
		if (subPackageLocalName.contains("."))
			throw new IllegalArgumentException("Sub package cannot have a period in its name.");
		MBPathMapping p = path.getChild(subPackageLocalName,true);
		if (p == null)
			return null;
		return subPackages.get(p);
	}

	public Collection<MBPackage> getSubPackages() {
		return subPackages.values();
	}

	public Collection<MBClass> getClasses() {
		return classes.values();
	}

	public MBPackage getParent() {
		return parent;
	}
	
	public void addPackage(MBPackage p) {
		this.subPackages.put(p.getPathMapping(),p);
	}
	
	public void addClass(MBClass p) {
		this.classes.put(p.getPathMapping(),p);
	}
	
	public void addInterface(MBInterface mbi) {
		this.interfaces.put(mbi.getPathMapping(),mbi);
	}
	
	public String getLocalName() {
		return localName;
	}
	
	public String getFullyQualifiedName() {
		return getPathMapping().getFullyQualifiedName();
	}
	
	public String getPackageName() {
		String s = "";
		if (parent!=null)
			s+=parent.getPackageName();
		if (s.length()>0)
			s+=".";
		s+=localName;
		return s;
	}
	
	public String toString() {
		return getPackageName();
	}
	
	public boolean isPackage() {
		return true;
	}
	
	public void addWrapper(MBPrimitiveWrapper wrapper) {
		if (this != MBPackage.JAVALANG)
			throw new IllegalStateException("Only java util can contain a primitive wrapper.");
		if (wrappers.containsKey(wrapper.getPathMapping())) {
			MBPrimitiveWrapper mpw = wrappers.get(wrapper.getPathMapping());
			if (mpw == wrapper) 
				// do nothing, just abort
				return;
			throw new IllegalStateException("The package already contains a wrapper with this name, you cannot redefine it.");
		}
		wrappers.put(wrapper.getPathMapping(), wrapper);
	}
	
	public void refreshTypes() {
		for (MBPackage p : subPackages.values()) 
			p.refreshTypes();
		for (MBClass c : classes.values()) {
			c.refreshTypes();
		}
		for (MBInterface c : interfaces.values()) {
			c.refreshTypes();
		}
	}
	
	public void write(File owningDirectory,ProgrammingLanguage language) {
		if (!owningDirectory.exists()) {
			owningDirectory.mkdirs();
		}
		File pdir = null;
		if (getLocalName()!=null && getLocalName().length()>0 ) {
			pdir = new File(owningDirectory.getAbsolutePath()+File.separator+getLocalName());
			pdir.mkdirs();
		} else {
			pdir = owningDirectory;
		}
		for (MBPackage sp : this.subPackages.values()) {
			sp.write(pdir,language);
		}
		for (MBClass c : this.getClasses()) {
			String n = c.getLocalName();
			if (n.contains("<"))
				n = n.substring(0,n.indexOf("<"));
			File f = new File(pdir+File.separator+n+".java");
			Writer jsw = null;
			try {
				jsw = new FileWriter(f);
				c.write(jsw,0,language);
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (jsw != null)
					try { jsw.close(); } catch (Exception e) {e.printStackTrace();}
			}
		}
		for (MBInterface c : this.interfaces.values()) {
			File f = new File(pdir+File.separator+c.getLocalName()+".java");
			Writer jsw = null;
			try {
				jsw = new FileWriter(f);
				c.write(jsw,0,language);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (jsw != null)
					try { jsw.close(); } catch (Exception e) {e.printStackTrace();}
			}
		}
	}

	public int compareTo(IPathItem o) {
		return getPathMapping().compareTo(o.getPathMapping());
	}

	public MBPath getPath() {
		return path.getPath();
	}

	public void addEnum(MBEnum enm) {
		MBEnum localEnum = getEnum(enm.getLocalName());
		if (localEnum != null) {
			if (localEnum == enm) {
				return;
			}
			throw new IllegalStateException("We already have an enum with that name, you cannot redefine it.");
		}
		this.classes.put(enm.getPathMapping(), enm);
	}

	public MBEnum getEnum(String enumName) {
		MBPath pth = this.getPath().createSubPath(enumName, false);
		MBPathMapping mpng = MBPathMapping.get(pth);
		if (mpng == null) 
			return null;
		MBClass mbc = this.classes.get(mpng);
		if (mbc ==null)
			return null;
		if (mbc instanceof MBEnum) {
			return (MBEnum) mbc;
		} else {
			// don't throw an exception now, if it tries to 
			// create the item, that will throw the exception.
			return null;
		}
	}
	
	public boolean hasEnum(String enumName) {
		return get(enumName) != null;
	}

	public void print(int i) {
		String tb = "";
		for (int j = 0;j<i;j++) {
			tb+="\t";
		}
		System.out.println(tb+"Package \""+this.localName+"\": ");
		System.out.println(tb+"  Path: "+this.path.getFullyQualifiedName()+"");
		System.out.println(tb+"  Comment: "+this.comment.getText());
		for (MBPackage p : this.subPackages.values()) {
			p.print(i+1);
		}
		for (MBClass c : this.classes.values()) {
			c.print(i+1);
		}
		for (MBInterface c : this.interfaces.values()) {
			c.print(i+1);
		}
	}
}