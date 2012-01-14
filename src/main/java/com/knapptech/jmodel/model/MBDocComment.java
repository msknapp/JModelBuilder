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