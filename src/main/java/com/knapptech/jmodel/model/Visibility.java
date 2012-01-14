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

public enum Visibility {
	PUBLIC ("public"),
	PROTECTED ("protected"),
	PACKAGEPRIVATE (""),
	PRIVATE ("private");
	private String text = "";
	Visibility(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public static Visibility parse(String s) {
		if (s==null)
			return PACKAGEPRIVATE;
		if (s.toLowerCase().contains("public"))
			return PUBLIC;
		if (s.toLowerCase().contains("protect"))
			return PROTECTED;
		if (s.toLowerCase().contains("package"))
			return PACKAGEPRIVATE;
		if (s.trim().length()<1)
			return PACKAGEPRIVATE;
		return PRIVATE;
	}
	
	public String toString() {
		return this.name().toLowerCase();
	}
}
