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

public class MBDocComment implements IWritable {
	private String text;
	
	public MBDocComment() {
		
	}
	
	public MBDocComment(String text) {
		this.setText(text);
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public void appendText(String text) {
		this.text += text;
	}

	/**
	 * @throws IOException 
	 * 
	 */
	public int write(Writer writer, int currentIndent,ProgrammingLanguage language) throws IOException {
		if (this.text == null || this.text.length()<1)
			return 0;
		String tb="";
		for (int i = 0;i<currentIndent;i++) {
			tb+="\t";
		}
		writer.write(tb+"/**\n");
		String[] lines = text.split("\n");
		for (String line : lines) {
			String tempLine;
			while (line.length()>100) {
				tempLine = line.substring(0,100);
				line = line.substring(100);
				writer.write(tb+" * "+tempLine+"\n");
			}
			writer.write(tb+" * "+line+"\n");
		}
		writer.write(tb+" */\n");
		
		return currentIndent;
	}
	
}