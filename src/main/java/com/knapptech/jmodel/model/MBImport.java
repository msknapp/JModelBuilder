package com.knapptech.jmodel.model;

import java.io.IOException;
import java.io.Writer;

import com.knapptech.jmodel.model.type.np.MBNonPrimitiveType;

public class MBImport implements IWritable, Comparable<MBImport> {
	
	private MBPathMapping pathMapping;
	private MBNonPrimitiveType owner;
	
	private MBImport(MBPathMapping path,MBNonPrimitiveType owner) {
		if (path == null)
			throw new NullPointerException("Must specify a path to import.");
		if (owner == null)
			throw new NullPointerException("Must specify an item that owns this import.");
		this.pathMapping = path;
		this.owner = owner;
		owner.addImport(this);
	}
	
	public static final MBImport create(MBPathMapping path,MBNonPrimitiveType owner) {
		if (path == null)
			return null;
		if (owner == null)
			return null;
		if (path == MBPathMapping.JAVALANG) {
			return null;
		}
		if (owner.hasImport(path))
			return owner.getImport(path);
		return new MBImport(path,owner);
	}
	
	public MBPath getPath() {
		return pathMapping.getPath();
	}
	
	public MBPath getPackagePath() {
		return pathMapping.getPath().getPackagePath();
	}
	
	public MBPathMapping getPathMapping() {
		return pathMapping;
	}
	
	public MBNonPrimitiveType getOwner() {
		return owner;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((pathMapping == null) ? 0 : pathMapping.hashCode());
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
		MBImport other = (MBImport) obj;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (pathMapping == null) {
			if (other.pathMapping != null)
				return false;
		} else if (!pathMapping.equals(other.pathMapping))
			return false;
		return true;
	}

	public int compareTo(MBImport o) {
		if (o == null)
			return -1;
		return pathMapping.compareTo(o.pathMapping);
	}

	public int write(Writer writer, int currentIndent,ProgrammingLanguage language) throws IOException {
		writer.write("import "+pathMapping.toString()+";\n");
		return 0;
	}
	
	
	
	
}