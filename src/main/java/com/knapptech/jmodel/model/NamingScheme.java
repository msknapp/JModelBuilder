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

public class NamingScheme {
	
	
	public static final boolean isAllowedForField(String name) {
		
		
		return true;
	}

	public static final boolean isAllowedForMethod(String name) {

		return true;
	}
	
	public static final boolean isAllowedForClass(String name) {

		return true;
	}

	public static final boolean isAllowedForPackage(String name) {

		return true;
	}
	
	private static final boolean isFirstLetterCapital(String name) {
		return Character.isUpperCase(name.charAt(0));
	}
}