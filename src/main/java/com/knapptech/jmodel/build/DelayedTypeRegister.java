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

import java.util.ArrayList;

import com.knapptech.jmodel.model.MBPath;
import com.knapptech.jmodel.model.type.delayed.IDelayedType;

public class DelayedTypeRegister {
	
	private static ArrayList<IDelayedType> types = new ArrayList<IDelayedType>();
	
	public static void add(IDelayedType type) {
		if (!types.contains(type))
			types.add(type);
	}
	
	public static ArrayList<MBPath> getNotFound() {
		ArrayList<MBPath> paths = new ArrayList<MBPath>();
		for (IDelayedType type : types) {
			if (type.isFound()) 
				continue;
			paths.add(type.getPath());
		}
		return paths;
	}
}
