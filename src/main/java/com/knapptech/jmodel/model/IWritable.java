package com.knapptech.jmodel.model;

import java.io.IOException;
import java.io.Writer;

public interface IWritable {
	int write(Writer writer,int currentIndent,ProgrammingLanguage language) throws IOException;
}