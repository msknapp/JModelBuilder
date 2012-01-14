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
package com.knapptech.jmodel.model;

import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

import com.knapptech.jmodel.model.type.delayed.IDelayedType;

public class MBPathMapping implements Comparable<MBPathMapping> {
	private static final TreeMap<MBPath,MBPathMapping> elements = new TreeMap<MBPath, MBPathMapping>();
	public static final MBPathMapping DEFAULTPATH = createDefaultPath();
	public static final MBPathMapping JAVALANG = getOrCreatePackage("java","lang");
	public static final MBPathMapping JAVAUTIL = getOrCreatePackage("java","util");
	public static final MBPathMapping JAVAIO = getOrCreatePackage("java","io");
	public static final MBPathMapping OBJECT = getOrCreateItem("java","lang","Object");
	public static final MBPathMapping ITERABLE = getOrCreateItem("java","lang","Iterable");
	public static final MBPathMapping SERIALIZABLE = getOrCreateItem("java","io","Serializable");
	public static final MBPathMapping COMPARABLE = getOrCreateItem("java","lang","Comparable");
	public static final MBPathMapping CLONABLE = getOrCreateItem("java","lang","Clonable");
	public static final MBPathMapping READABLE = getOrCreateItem("java","lang","Readable");
	public static final MBPathMapping RUNNABLE = getOrCreateItem("java","lang","Runnable");
	
	private MBPath path;
	private MBPathMapping parent;
	private TreeMap<MBPathPart,MBPathMapping> children = new TreeMap<MBPathPart, MBPathMapping>();
	private IPathItem pathElement;
	
	public static final MBPathMapping getOrCreatePackage(String... parts) {
		MBPath pth = MBPath.createPackagePath(parts);
		return getOrCreate(pth);
	}
	
	public static final MBPathMapping getOrCreateItem(String... parts) {
		MBPath pth = MBPath.createItemPath(parts);
		return getOrCreate(pth);
	}
	
	public static final MBPathMapping getOrCreate(MBPath pth) {
		if (pth.length()<1)
			return DEFAULTPATH;
		MBPathMapping p = elements.get(pth);
		if (p != null)
			return p;
		p = new MBPathMapping();
		elements.put(pth, p);
		p.path = pth;
		MBPathPart localPart = pth.getLocalPart();
		if (pth.length()>1) {
			p.parent = getOrCreate(pth.createSuperPath());
			p.parent.children.put(localPart, p);
		} else {
			p.parent = DEFAULTPATH;
			DEFAULTPATH.children.put(localPart, p);
		}
		return p;
	}
	
	public final MBPathMapping createChild(String childLocalName,boolean pckg) {
		return getOrCreate(path.createSubPath(childLocalName, pckg));
	}
	
	public final MBPathMapping createChild(IPathItem element,String childLocalName,boolean pckg) {
		MBPathMapping child = getOrCreate(path.createSubPath(childLocalName, pckg));
		reserve(element,child);
		return child;
	}

	public static final MBPathMapping reserve(IPathItem element,MBPathMapping path) {
		if (path == null)
			throw new NullPointerException("Must define a path.");
		if (path.pathElement != null && element != path.pathElement)
			if (!(path.pathElement instanceof IDelayedType))
				throw new IllegalArgumentException("The path already has an element, you cannot redefine it.\n" +
						"Path: "+path.toString());
		if (path.pathElement == null)
			path.pathElement = element;
		return path;
	}

	public static final MBPathMapping reserve(IPathItem element,MBPath path) {
		if (element == null) 
			throw new NullPointerException("Must specify an element");
		MBPathMapping pathMapping = getOrCreate(path);
		if (pathMapping.pathElement != null && element != pathMapping.pathElement) {
			if (!(pathMapping.pathElement instanceof IDelayedType))
				throw new IllegalArgumentException("The path already has an element, you cannot redefine it.\n" +
						"Path: "+path.toString());
		}
		if (pathMapping.pathElement == null)
			pathMapping.pathElement = element;
		return pathMapping;
	}
	
	public final IPathItem getElement() {
		return pathElement;
	}
	
	public static final MBPathMapping getOrReserve(IPathItem element,MBPath pth) {
		if (pth.getFullyQualifiedName().length()<1) {
			if (DEFAULTPATH.pathElement == null) {
				if (element == null) 
					throw new NullPointerException("Must specify an element");
				DEFAULTPATH.pathElement = element;
			}
			return DEFAULTPATH;
		}
		MBPathMapping m=get(pth);
		if (m == null) {
			return reserve(element, pth);
		} else {
			if (element == null) {
				return m;
			}
			if (m.pathElement == null) {
				m.pathElement = element;
				return m;
			} else {
				if (m.pathElement == element)
					return m;
				throw new IllegalStateException("The path already has an element defined, you cannot redefine it.");
			}
		}
	}
	
	public static final MBPathMapping get(MBPath path) {
		if (path.getFullyQualifiedName().length()<1) {
			return DEFAULTPATH;
		}
		return elements.get(path);
	}
	
	private MBPathMapping() { }

	public MBPath getPath() {
		return path;
	}
	
	public MBPathPart[] getPathParts() {
		return path.getParts();
	}
	
	public String getFullyQualifiedName() {
		return path.getFullyQualifiedName();
	}
	
	public String getLocalName() {
		return path.getLocalName();
	}

	public MBPathMapping getParent() {
		return parent;
	}
	
	public Collection<MBPathMapping> getChildren() {
		return Collections.unmodifiableCollection(children.values());
	}
	
	public MBPathMapping getChild(MBPathPart path) {
		return children.get(path);
	}
	
	public MBPathMapping getChild(String localName,boolean pckg) {
		return children.get(new MBPathPart(localName,pckg));
	}

	public IPathItem getPathElement() {
		return pathElement;
	}
	
	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (o == this) 
			return true;
		if (!( o instanceof MBPathMapping))
			return false;
		MBPathMapping mbp = (MBPathMapping)o;
		return (path.equals(mbp.getPath()));
	}
	
	public int hashCode() {
		return path.hashCode()+4;
	}
	
	public int compareTo(MBPathMapping mbp) {
		if (mbp == null)
			return -1;
		if (mbp.equals(this))
			return 0;
		return path.compareTo(mbp.path);
	}
	
	public String toString() {
		return getFullyQualifiedName();
	}
	
//	public static String makeFullyQualifiedName(String... parts) {
//		if (parts == null || parts.length<1 || 
//			(parts.length == 1 &&
//				(parts[0]== null || parts[0].length()<1)
//									)) {
//			return "";
//		}
//		String s = "";
//		for (int i = 0;i<parts.length;i++) {
//			s+=parts[i]+(i<parts.length-1 ? "." : "");
//		}
//		return s;
//	}
	
	private static MBPathMapping createDefaultPath() {
		MBPathMapping p = new MBPathMapping();
		p.parent = null;
		p.path = MBPath.createPackagePath();
		// cannot force it to be associated with the default 
		// package yet, it might not be initialized.  Calling
		// that could create a circular reference, infinite loop.
		return p;
	}
}