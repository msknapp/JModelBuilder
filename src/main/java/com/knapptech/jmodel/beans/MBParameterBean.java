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
package com.knapptech.jmodel.beans;

import com.knapptech.jmodel.model.type.MBType;

public class MBParameterBean {
	private String name;
	private MBType type;
	private String typePath;
	private boolean _final;
	
	public MBParameterBean() {
		
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MBType getType() {
		return type;
	}
	public void setType(MBType type) {
		this.type = type;
	}
	public String getTypePath() {
		return typePath;
	}
	public void setTypePath(String typePath) {
		this.typePath = typePath;
	}
	public boolean is_final() {
		return _final;
	}
	public void set_final(boolean _final) {
		this._final = _final;
	}
}
