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
package com.knapptech.jmodel.build.strategic;

public enum BuildStrategy {
	BEAN,
	IMMUTABLE,
	BEANANDINTERFACE,
	IMMUTABLEANDINTERFACE,
	EXTENSIBLE,
	EXTENSIBLESLAVE,
	SINGLETON,
	SINGLETONANDINTERFACE,
	MANAGED,
	FACTORY,
	LOCKING,
	UNLOCKING;
	
	private BuildStrategy() {
		
	}
	
	public static BuildStrategy parse(String s) {
		s = s.toLowerCase();
		for (BuildStrategy b : values()) {
			if (b.name().toLowerCase().equals(s))
				return b;
		}
		return null;
	}
}
