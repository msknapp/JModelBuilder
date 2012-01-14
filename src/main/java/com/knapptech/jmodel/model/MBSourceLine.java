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

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import com.knapptech.jmodel.model.type.np.MBException;

public class MBSourceLine implements ISourceCode {
	private String code;
	private boolean addsIndent;
	private boolean removesIndent;
	private HashSet<MBException> exceptionsThrown = new HashSet<MBException>();
	
	public MBSourceLine(String code) {
		if (code == null)
			throw new NullPointerException("Must provide non null code.");
		this.code = code;
	}
	
	public MBSourceLine(String code,boolean addsIndent,boolean removesIndent) {
		if (code == null)
			throw new NullPointerException("Must provide non null code.");
		this.code = code;
		this.addsIndent = addsIndent;
		this.removesIndent = removesIndent;
	}
	
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public boolean isAddsIndent() {
		return addsIndent;
	}
	public void setAddsIndent(boolean addsIndent) {
		this.addsIndent = addsIndent;
	}
	public boolean isRemovesIndent() {
		return removesIndent;
	}
	public void setRemovesIndent(boolean removesIndent) {
		this.removesIndent = removesIndent;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (addsIndent ? 1231 : 1237);
		result = prime * result + ((code == null) ? 0 : code.hashCode());
		result = prime * result + (removesIndent ? 1231 : 1237);
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
		MBSourceLine other = (MBSourceLine) obj;
		if (addsIndent != other.addsIndent)
			return false;
		if (code == null) {
			if (other.code != null)
				return false;
		} else if (!code.equals(other.code))
			return false;
		if (removesIndent != other.removesIndent)
			return false;
		return true;
	}
	
	public String toString() {
		return code;
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

	private int writeAsJava(Writer writer, int currentIndent) throws IOException {
		int i = 0;
		while (i++<currentIndent)
			writer.write("\t");
		writer.write(this.code+"\n");
		return currentIndent;
	}
	
	public boolean addException(MBException e) {
		return exceptionsThrown.add(e);
	}

	public Set<MBException> getExceptions() {
		return exceptionsThrown;
	}
}