package com.knapptech.jmodel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MBPath implements Comparable<MBPath> {
	private MBPathPart[] parts = null;
	
	private MBPath(MBPathPart[] parts) {
		this.parts = parts;
	}
	
	public static final MBPath createPackagePath(String... parts) {
		if (parts == null)
			return new MBPath(new MBPathPart[0]);
		MBPathPart[] pth = new MBPathPart[parts.length];
		for (int i = 0;i<parts.length;i++) {
			pth[i] = new MBPathPart(parts[i],true);
		}
		return new MBPath(pth);
	}
	
	public static final MBPath createItemPath(String... parts) {
		if (parts == null || parts.length<1 || 
				(parts.length == 1 && 
				(parts[0]==null || parts[0].length()<1)))
			return new MBPath(new MBPathPart[0]);
		MBPathPart[] pth = new MBPathPart[parts.length];
		for (int i = 0;i<parts.length-1;i++) {
			pth[i] = new MBPathPart(parts[i],true);
		}
		pth[parts.length-1] = new MBPathPart(parts[parts.length-1], false);
		return new MBPath(pth);
	}
	
	public static final MBPath createItemPath(int index,String... parts) {
		if (parts == null || parts.length<1 || 
				(parts.length == 1 && 
				(parts[0]==null || parts[0].length()<1)))
			return new MBPath(new MBPathPart[0]);
		MBPathPart[] pth = new MBPathPart[parts.length];
		for (int i = 0;i<parts.length-1;i++) {
			pth[i] = new MBPathPart(parts[i],i<index);
		}
		pth[parts.length-1] = new MBPathPart(parts[parts.length-1], false);
		return new MBPath(pth);
	}
	
	public static final MBPath createPath(String[] packageParts,String... classParts) {
		MBPathPart[] pth = new MBPathPart[packageParts.length+classParts.length];
		for (int i = 0;i<packageParts.length;i++) {
			pth[i] = new MBPathPart(packageParts[i],true);
		}
		for (int i = 0;i<classParts.length;i++) {
			pth[i+packageParts.length] = new MBPathPart(classParts[i],false);
		}
		return new MBPath(pth);
	}
	
	public final MBPath createSuperPath() {
		if (this.parts.length<1)
			throw new IllegalStateException("Cannot get super path of default path.");
		if (this.parts.length==1)
			return createPackagePath();
		MBPathPart[] pth = new MBPathPart[parts.length-1];
		System.arraycopy(this.parts, 0, pth, 0, pth.length);
		return new MBPath(pth);
		
	}
	
	public final MBPath createSubPath(String localName,boolean pckg) {
		return createSubPath(new MBPathPart(localName, pckg));
	}
	
	public final MBPath createSubPath(MBPathPart part) {
		if (this.parts.length>0) {
			if (!this.parts[parts.length-1].isPackage() && part.isPackage())
				throw new IllegalArgumentException("Cannot have a package in a file.");
		}
		if (part.getLocalName().contains("."))
			throw new IllegalArgumentException("Cannot have a path part with a period in it.");
		MBPathPart[] pth = new MBPathPart[parts.length+1];
		System.arraycopy(parts, 0, pth, 0, parts.length);
		pth[pth.length-1] = part;
		return new MBPath(pth);
	}
	
	public int length() {
		return this.parts.length;
	}
	
	public String getFullyQualifiedName() {
		String s = "";
		for (int i = 0;i<parts.length;i++) {
			s+=parts[i].getLocalName()+(i<parts.length-1 ? "." : "");
		}
		return s;
	}
	
	public String getPackageFullyQualifiedName() {
		String s = "";
		for (int i = 0;i<parts.length;i++) {
			if (!parts[i].isPackage())
				continue;
			s+=parts[i].getLocalName()+(i<parts.length-1 ? "." : "");
		}
		return s;
	}
	
	public String toString() {
		return getFullyQualifiedName();
	}
	
	@Override
	public int hashCode() {
		if (parts == null || parts.length<1 || 
				(parts.length == 1 && 
				(parts[0]==null || parts[0].getLocalName().length()<1)))
			return 0;
		int i = 0;
		for (int j = 0;j<parts.length;j++) {
			i+=parts[j].hashCode();
		}
		return i;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MBPath other = (MBPath) obj;
		if (!Arrays.equals(parts, other.parts))
			return false;
		return true;
	}

	public int compareTo(MBPath o) {
		if (o == null)
			return -1;
		if (o.equals(this))
			return 0;
		int lng = Math.min(parts.length, o.parts.length);
		for (int i = 0;i<lng;i++) {
			int j = parts[i].compareTo(o.parts[i]);
			if (j != 0)
				return j;
		}
		return parts.length - o.parts.length;
	}

	public MBPathPart[] getParts() {
		return Arrays.copyOf(parts, parts.length);
	}
	
	public MBPathPart[] getPackageParts() {
		List<MBPathPart> ps = new ArrayList<MBPathPart>();
		for (MBPathPart p : parts) {
			if (p.isPackage())
				ps.add(p);
		}
		MBPathPart[] r = ps.toArray(new MBPathPart[ps.size()]);
		return Arrays.copyOf(r, r.length);
	}
	
	public int getPackageCount() {
		for (int i = 0;i<parts.length;i++) {
			if (!parts[i].isPackage())
				return i+1;
		}
		return parts.length;
	}
	
	public String getLocalName() {
		return parts[parts.length-1].getLocalName();
	}

	public MBPathPart getLocalPart() {
		return parts[parts.length-1];
	}

	public MBPath getPackagePath() {
		return new MBPath(getPackageParts());
	}
}