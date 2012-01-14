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

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.TreeMap;

public class MBProject {
	private MBDefaults defaults = null;
	private TreeMap<MBPathMapping,MBPackage> packages = new TreeMap<MBPathMapping, MBPackage>();
	private String name;
	
	private static final MBProject project = new MBProject();
	
	public static final MBProject getProject() {
		return project;
	}
	
	public static final void setProjectName(String name) {
		project.name = name;
	}
	
	public static final String getProjectName(String name) {
		return project.name;
	}
	
	private MBProject() {
		
	}

	public MBDefaults getDefaults() {
		return defaults;
	}

	public void setDefaults(MBDefaults defaults) {
		this.defaults = defaults;
	}
	
	public String toString() {
		return name;
	}
	
	public String getName() {
		return name;
	}
	
	public Collection<MBPackage> getPackages() {
		return Collections.unmodifiableCollection(packages.values());
	}

	public void write(File outputDirectory,ProgrammingLanguage language) {
		if (!outputDirectory.exists()) {
			outputDirectory.mkdirs();
		}
		File pdir = new File(outputDirectory.getAbsolutePath()+File.separator+getName()+File.separator+"src"+
				File.separator+"main"+File.separator+"java");
		pdir.mkdirs();
		for (MBPackage sp : this.getPackages()) {
			sp.write(pdir,language);
		}
		File dd = new File(outputDirectory.getAbsolutePath()+File.separator+getName()+File.separator+"src"+
				File.separator+"main"+File.separator+"resources");
		if (!dd.exists())
			dd.mkdir();
		dd = new File(outputDirectory.getAbsolutePath()+File.separator+getName()+File.separator+"src"+
				File.separator+"test"+File.separator+"java");
		if (!dd.exists())
			dd.mkdir();
		dd = new File(outputDirectory.getAbsolutePath()+File.separator+getName()+File.separator+"src"+
					File.separator+"test"+File.separator+"resources");
		if (!dd.exists())
			dd.mkdir();
	}

	public void addPackage(MBPackage k) {
		if (this.packages.containsKey(k.getPathMapping())) {
			MBPackage p = this.packages.get(k.getPathMapping());
			if (p == k)
				return;
			throw new IllegalArgumentException("This already has a package with that name, you cannot redefine it.");
		}
		this.packages.put(k.getPathMapping(), k);
	}

	public boolean hasPackage(MBPackage k) {
		return (this.packages.containsKey(k.getPathMapping()));
	}
	public void print() {
		for (MBPackage p : this.packages.values()) {
			p.print(0);
		}
	}
}