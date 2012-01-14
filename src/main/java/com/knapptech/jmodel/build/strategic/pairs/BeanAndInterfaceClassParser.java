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
package com.knapptech.jmodel.build.strategic.pairs;

import com.knapptech.jmodel.beans.MBMethodBean;
import com.knapptech.jmodel.build.strategic.StrategicClassParser;
import com.knapptech.jmodel.model.element.MBConstructor;
import com.knapptech.jmodel.model.element.MBMethod;
import com.knapptech.jmodel.model.type.np.MBClass;
import com.knapptech.jmodel.model.type.np.MBInterface;

public class BeanAndInterfaceClassParser extends StrategicClassParser {
	
	private MBClass builtClass;
	private MBInterface builtInterface;

	@Override
	public void buildTypes() {
		builtClass = makeDefaultClass(getClassBean(), getOwner());
		builtInterface = MBInterface.getOrCreate("I"+getClassBean().getLocalName(),getOwner());
		builtClass.addImplementedInterface(builtInterface);
		if (getClassBean().isComparable())
			addImplementsComparable(builtInterface);
	}
	
	@Override
	public void buildElements() {
//		builtClass.setComment(getClassBean().getComment());
//		builtInterface.setComment(getClassBean().getComment());
		MBConstructor dc = makeDefaultConstructor(builtClass, getClassBean().getFields().values());
		if (dc.getParameters().size()>0) {
			makeNoArgConstructor(builtClass);
		}
		makeCloneConstructor(builtClass, getClassBean().getFields().values(), builtClass);
		manageStaticFinalFields(builtClass, getClassBean().getFields().values());
		addFields(getClassBean().getFields().values(), builtClass);
		addNonStaticGetters(builtClass, getClassBean().getFields().values());
		addNonStaticSetters(builtClass, getClassBean().getFields().values());
		addGetters(builtInterface, getClassBean().getFields().values());
		addSetters(builtInterface, getClassBean().getFields().values());
		addRequiredMethods(builtClass, getClassBean().getMethods().values(), true);
		addRequiredMethods(builtInterface, getClassBean().getMethods().values(), true);
		MBMethodBean em = createEqualsMethod(getClassBean().getFields().values(), builtClass);
		MBMethodBean hcm = createHashCodeMethod(getClassBean().getFields().values());
		MBMethod eqm = makeRealMethod(em, builtClass);
		MBMethod hscm = makeRealMethod(hcm, builtClass);
		if (getClassBean().isComparable()) {
			MBMethodBean ctm = createCompareToMethod(getClassBean().getFields().values(), builtInterface);
			MBMethod cmtd = makeRealMethod(ctm,builtClass);
		}
	}
}