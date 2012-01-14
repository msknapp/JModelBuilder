package com.knapptech.jmodel.model;

public class MBPathPart implements Comparable<MBPathPart> {
	
	private String localName;
	private boolean pckg = false;
	
	public MBPathPart(String localName,boolean pckg) {
		if (localName == null || localName.length()<1)
			throw new IllegalArgumentException("Illegal path part name.");
		if (localName.contains("."))
			throw new IllegalArgumentException("Path part cannot contain a period.");
		this.localName = localName;
		this.pckg = pckg;
	}
	
	public String getLocalName() {
		return localName;
	}
	
	public boolean isPackage() {
		return pckg;
	}
	
	public String toString() {
		return localName;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((localName == null) ? 0 : localName.hashCode());
		result = prime * result + (pckg ? 1231 : 1237);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MBPathPart other = (MBPathPart) obj;
		if (localName == null) {
			if (other.localName != null)
				return false;
		} else if (!localName.equals(other.localName))
			return false;
		if (pckg != other.pckg)
			return false;
		return true;
	}

	public int compareTo(MBPathPart o) {
		if (o == null)
			return -1;
		if (pckg && !o.pckg)
			return -1;
		if (o.pckg && !pckg)
			return 1;
		return localName.compareTo(o.localName);
	}
}