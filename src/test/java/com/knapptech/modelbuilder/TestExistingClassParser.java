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
package com.knapptech.modelbuilder;

import junit.framework.Assert;

import org.junit.Test;

import com.knapptech.jmodel.model.MBExistingClassParser;
import com.knapptech.jmodel.model.type.MBPrimitiveWrapper;
import com.knapptech.jmodel.model.type.MBType;
import com.knapptech.jmodel.model.type.np.MBClass;
import com.knapptech.jmodel.model.type.np.MBInterface;
import com.knapptech.jmodel.model.type.np.MBInterfaceWithParameters;
import com.knapptech.jmodel.model.type.np.MBNonPrimitiveType;

public class TestExistingClassParser {
	
	
	@Test
	public void testParse() {
		MBType collectionType = MBExistingClassParser.parse("java.util.Collection");
		MBType hashMapType = MBExistingClassParser.parse("java.util.HashMap");
		MBType serializableType = MBExistingClassParser.parse("java.io.Serializable");
		Assert.assertTrue(serializableType == MBInterface.SERIALIZABLE);
		MBType objectType = MBExistingClassParser.parse("java.lang.Object");
		Assert.assertTrue(objectType == MBClass.OBJECT);
		MBType iterableType = MBExistingClassParser.parse("java.lang.Iterable");
		Assert.assertTrue(iterableType == MBInterfaceWithParameters.ITERABLE);
		MBType integerType = MBExistingClassParser.parse("java.lang.Integer");
		Assert.assertTrue(integerType == MBPrimitiveWrapper.INTEGER);
//		MBType iterableType2 = MBExistingClassParser.parse("java.lang.Iterable<E>");
//		Assert.assertTrue(iterableType2 == MBInterface.ITERABLE);
		
	}
}
