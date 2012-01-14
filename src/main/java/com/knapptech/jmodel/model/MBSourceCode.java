package com.knapptech.jmodel.model;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.np.MBException;
import com.knapptech.jmodel.model.type.np.MBNonPrimitiveType;

public class MBSourceCode implements ISourceCode {
	private List<ISourceCode> sourceCode = new ArrayList<ISourceCode>();
	private List<MBException> exceptionsThrown = new ArrayList<MBException>();
	private List<IType> requiredImports = new ArrayList<IType>();
	
	public MBSourceCode() {
		
	}
	
	public MBSourceCode(MBSourceCode other) {
		for (ISourceCode sc : other.sourceCode) {
			if (sc instanceof MBSourceCode) {
				sourceCode.add(new MBSourceCode((MBSourceCode) sc));
			} else if (sc instanceof MBSourceLine) {
				sourceCode.add(sc);
			}
		}
		for (MBException e : other.exceptionsThrown) {
			this.exceptionsThrown.add(e);
		}
	}

	public void add(String line) {
		sourceCode.add(new MBSourceLine(line));
	}
	
	public void addIndented(String line) {
		sourceCode.add(new MBSourceLine("\t"+line));
	}
	
	public void addLine(String line) {
		sourceCode.add(new MBSourceLine(line));
	}
	
	public void addBlock(MBSourceCode codeBlock) {
		sourceCode.add(codeBlock);
	}
	
	public void addBlockLine(String line) {
		sourceCode.add(new MBSourceLine(line,true,false));
	}
	
	public void addBlockFinishingLine(String line) {
		sourceCode.add(new MBSourceLine(line,false,true));
	}
	
	public Collection<ISourceCode> getCode() {
		return Collections.unmodifiableList(sourceCode);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((sourceCode == null) ? 0 : sourceCode.hashCode());
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
		MBSourceCode other = (MBSourceCode) obj;
		if (sourceCode == null) {
			if (other.sourceCode != null)
				return false;
		} else if (!sourceCode.equals(other.sourceCode))
			return false;
		return true;
	}

	public int write(Writer writer, int currentIndent,ProgrammingLanguage language) throws IOException {
		if (language == ProgrammingLanguage.JAVA) {
			return writeAsJava(writer,currentIndent);
		} else if (language == ProgrammingLanguage.JAVASCRIPT) {
			throw new UnsupportedOperationException("Can only code in java presently.");
		} else if (language == ProgrammingLanguage.CSHARP) {
			throw new UnsupportedOperationException("Can only code in java presently.");
		} else if (language == ProgrammingLanguage.PYTHON) {
			throw new UnsupportedOperationException("Can only code in java presently.");
		}
		return 0;
	}

	private int writeAsJava(Writer writer, int currentIndent) throws IOException {
		for (ISourceCode sc : this.sourceCode) {
			if (sc instanceof MBSourceCode) 
				sc.write(writer, currentIndent+1, ProgrammingLanguage.JAVA);
			else 
				sc.write(writer, currentIndent, ProgrammingLanguage.JAVA);
		}
		return currentIndent;
	}

	public Set<MBException> getExceptions() {
		HashSet<MBException> ss = new HashSet<MBException>();
		for (ISourceCode sc : sourceCode) {
			ss.addAll(sc.getExceptions());
		}
		for (MBException e : this.exceptionsThrown) {
			ss.add(e);
		}
		return ss;
	}

	public void createImports(MBNonPrimitiveType owner) {
		for (IType t : requiredImports) {
			MBImport.create(t.getPathMapping(), owner);
		}
	}

	public int lines() {
		int lns = 0;
		for (ISourceCode sc : this.sourceCode) {
			if (sc instanceof MBSourceCode) {
				lns+=((MBSourceCode) sc).lines();
			} else {
				lns+=1;
			}
		}
		return lns;
	}

	public void add(ISourceCode sc) {
		this.sourceCode.add(sc);
	}

	public void addException(MBException e) {
		if (e == null)
			throw new NullPointerException("Must define a non null exception.");
		this.exceptionsThrown.add(e);
	}

	public void addRequiredImport(IType t) {
		if (!this.requiredImports.contains(t))
			this.requiredImports.add(t);
	}
}