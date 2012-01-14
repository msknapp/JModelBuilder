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
