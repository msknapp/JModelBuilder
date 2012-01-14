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
package com.knapptech.jmodel.build;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.knapptech.jmodel.model.MBDefaults;
import com.knapptech.jmodel.model.MBPackage;
import com.knapptech.jmodel.model.MBPath;
import com.knapptech.jmodel.model.MBProject;

public class Parser {
	
	private static String inputPath = "src/main/resources/samples/Chess.xml";
	private static String outputPath = "product";
	private static File inputFile;
	private static File outputDirectory;
	private static Element docElement = null;
	
	public static File getInputFile() {
		return inputFile;
	}
	
	public static File getOutputDirectory() {
		return outputDirectory;
	}

	public static void main(String[] args) {
		inputFile = new File(inputPath);
		outputDirectory = new File(outputPath);
		try {
			Document d = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputFile);
			String fileText = getFileText(inputFile);
			docElement = d.getDocumentElement();
			MBProject p = parseProject(docElement,false);
			MBPackage.DEFAULT.refreshTypes(); // refreshes the whole tree.
			ArrayList<MBPath> missing = DelayedTypeRegister.getNotFound();
			for (MBPath path : missing) {
				PrintMissingError(fileText, path.getFullyQualifiedName());
			}
			if (missing.size()>0) {
				p.print();
			}
			parseProject(docElement,true);
			missing = DelayedTypeRegister.getNotFound();
			if (missing.size()>0) {
				System.err.println("==> Even after parsing all elements, there are missing/unknown types.");
				System.err.println("The project write process has been aborted due to having missing classes.");
				for (MBPath path : missing) {
					PrintMissingError(fileText, path.getFullyQualifiedName());
				}
				if (missing.size()>0) {
					p.print();
				}
			} else {
				p.write(outputDirectory,MBDefaults.getLanguage());
			}
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	private static void PrintMissingError(String fileText,String missingClass) {
		System.err.println("The class named \""+missingClass+"\" was never found.\n"+
				"\tThere are several possible causes of this problem:\n"+
				"\t1.  This could be a typo in the xml document.\n"+
				"\t2.  Your classpath might be missing the required class.\n"+
				"\t3.  You may have used the wrong build strategy for the class that would create this.\n"+
				"\t4.  You may have forgotten to write this class in the xml.");
		String search1 = "\""+missingClass.replace("\"", "")+"\"";
		String search2 = ">"+missingClass.replace("\"", "")+"<";
		String[] fileTextParts = fileText.split("\n");
		boolean foundInText = false;
		int lineNumber = 0;
		for (String fileTextPart : fileTextParts) {
			lineNumber++;
			if (fileTextPart.contains(search1) || fileTextPart.contains(search2)) {
				System.err.println("The missing class was found in the xml on line: "+lineNumber);
				System.err.println("\tText: "+fileTextPart);
				foundInText=true;
			}
		}
		if (!foundInText) {
			System.err.println("Could not find the missing class in the text file, this must be a bug "+
					"\nin the model builder code.  Please report this bug.");
		}
	}
	
	private static String getFileText(File file) {
		StringBuilder sb = new StringBuilder();
		FileReader fr = null;
		try {
			fr = new FileReader(file);
			int i = 0;
			while ((i = fr.read()) != -1) {
				sb.append((char)i);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (fr != null)
				try { fr.close(); } catch (Exception e) {e.printStackTrace();}
		}
		return sb.toString();
	}
	
	private static MBProject parseProject(Element item,boolean withElements) {
		MBProject p = MBProject.getProject();
		MBProject.setProjectName(item.getAttribute("name"));
		for (Element packageElement : XMLHelper.getImmediateSubElements(item, "package")) {
			MBPackage k = PackageParser.parse(packageElement,MBPackage.DEFAULT,withElements);
			if (!p.hasPackage(k)) {
				p.addPackage(k);
			}
		}
		return p;
	}
	
	
	public static Element getElementForFullyQualifiedName(String fqn) {
		String[] ps = fqn.split("\\.");
		return getElementForFullyQualifiedName(docElement,ps);
	}
	
	private static Element getElementForFullyQualifiedName(Element n,String... names) {
		NodeList nl = n.getChildNodes();
		for (int i = 0;i<nl.getLength();i++) {
			Node nnn = nl.item(i);
			if (nnn==null || nnn.getNodeType() != Node.ELEMENT_NODE)
				continue;
			if (nnn.getNodeName().equals("package")) {
				String pn = ((Element)nnn).getAttribute("name");
				if (pn.equals(names[0])) {
					if (names.length<=1)
						return (Element)nnn;
					String[] sss =  new String[names.length-1];
					System.arraycopy(names, 1, sss, 0, names.length-1);
					return getElementForFullyQualifiedName((Element)nnn, sss);
				}
			}
			if (names.length==1) {
				if (nnn.getNodeName().equals("class") || 
						nnn.getNodeName().equals("interface") || 
						nnn.getNodeName().equals("enum")) {
					String pn = ((Element)nnn).getAttribute("name");
					if (pn.equals(names[0])) {
						return (Element)nnn;
					}
				}
			}
		}
		return null;
	}
	public static Element getElement(String... path) {
		return XMLHelper.getElement(docElement, path);
	}
}