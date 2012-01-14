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
package com.knapptech.jmodel.build.strategic.multiples;

import com.knapptech.jmodel.beans.MBFieldBean;
import com.knapptech.jmodel.model.MBModifiers;
import com.knapptech.jmodel.model.MBParameter;
import com.knapptech.jmodel.model.element.MBConstructor;
import com.knapptech.jmodel.model.element.MBField;
import com.knapptech.jmodel.model.element.MBMethod;
import com.knapptech.jmodel.model.type.MBPrimitiveType;
import com.knapptech.jmodel.model.type.np.MBClass;

public class ExtensibleSlaveClassParser extends ExtensibleClassParser {
	
	
	
	protected void buildElementsForImmutable() {
		MBConstructor cns = MBConstructor.getOrCreate(builtImmutableClass,new MBParameter(builtImmutableInterface, "slave"));
		cns.getSourceCode().add("if (slave == null)");
		cns.getSourceCode().addIndented("throw new NullPointerException(\"The slave member cannot be null.\");");
		cns.getSourceCode().add("this.slave=new "+getClassBean().getLocalName()+"(slave);");
		MBField f = new MBField("slave",builtImmutableClass,builtClass);
		f.getModifiers().setFinal(true);
		f.getModifiers().makePrivate();
		for (MBFieldBean fld : this.getClassBean().getFields().values()) {
			if (!fld.isGettable())
				continue;
			MBMethod m = new MBMethod("get"+capitalizeName(fld.getName()),builtImmutableClass,fld.getType());
			m.setModifiers(new MBModifiers(fld.getModifiers()));
			m.getModifiers().makePublic();
			m.getSourceCode().add("return this.slave.get"+capitalizeName(fld.getName())+"();");
		}
		MBMethod em = new MBMethod("equals",builtImmutableClass,MBPrimitiveType.BOOLEAN,new MBParameter(MBClass.OBJECT, "o"));
		em.getSourceCode().add("if (o == null)");
		em.getSourceCode().addIndented("return false;");
		em.getSourceCode().add("return this.slave.equals(o);");
		MBMethod hcm = new MBMethod("hashCode",builtImmutableClass,MBPrimitiveType.INT);
		hcm.getSourceCode().add("return this.slave.hashCode();");
		if (getClassBean().isComparable()) {
			MBMethod ctm = new MBMethod("compareTo",builtImmutableClass,MBPrimitiveType.INT,new MBParameter(builtImmutableInterface,"o"));
			ctm.getSourceCode().add("if (o == null)");
			ctm.getSourceCode().addIndented("return -1;");
			ctm.getSourceCode().add("return this.slave.compareTo(o);");
		}
	}
}