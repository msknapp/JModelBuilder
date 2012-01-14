package com.knapptech.jmodel.build.strategic.multiples;

import com.knapptech.jmodel.beans.MBMethodBean;
import com.knapptech.jmodel.build.strategic.StrategicClassParser;
import com.knapptech.jmodel.model.element.MBConstructor;
import com.knapptech.jmodel.model.element.MBMethod;
import com.knapptech.jmodel.model.type.np.MBClass;
import com.knapptech.jmodel.model.type.np.MBInterface;

public class ExtensibleClassParser extends StrategicClassParser {

	protected MBClass builtClass;
	protected MBInterface builtInterface;
	protected MBClass builtImmutableClass;
	protected MBInterface builtImmutableInterface;
	private boolean builtTypes = false;
	
	@Override
	public void buildTypes() {
		if (builtTypes) {
			System.err.println("Calling build types more than once!");
		}
		builtTypes=true;
		builtClass = makeDefaultClass(getClassBean(), getOwner());
		builtInterface = MBInterface.getOrCreate("I"+getClassBean().getLocalName(),getOwner());
		builtClass.addImplementedInterface(builtInterface);
		builtImmutableClass = makeClass(getClassBean(),getOwner(),getClassBean().getLocalName()+"Immutable",MBClass.OBJECT);
		builtImmutableInterface = MBInterface.getOrCreate("I"+getClassBean().getLocalName()+"Readable", getOwner());
		builtInterface.addImplementedInterface(builtImmutableInterface);
		builtImmutableClass.addImplementedInterface(builtImmutableInterface);
		if (getClassBean().isComparable())
			addImplementsComparable(builtImmutableInterface);
	}
	
	@Override
	public void buildElements() {
		buildElementsForImmutableInterface();
		buildElementsForMainInterface();
		buildElementsForMain();
		buildElementsForImmutable();
	}
	
	protected void buildElementsForImmutable() {
		makeConstructorForImmutable(builtImmutableClass, getClassBean().getFields().values());
		makeCloneConstructorForImmutable(builtImmutableClass, getClassBean().getFields().values(), builtImmutableInterface);
		manageStaticFinalFields(builtImmutableClass, getClassBean().getFields().values());
		addFields(getClassBean().getFields().values(), builtImmutableClass);
		addNonStaticGetters(builtImmutableClass, getClassBean().getFields().values());
		addRequiredMethods(builtImmutableClass, getClassBean().getMethods().values(), false);
		MBMethodBean em = createEqualsMethod(getClassBean().getFields().values(), builtImmutableClass);
		MBMethod eqm = makeRealMethod(em, builtImmutableClass);
		MBMethodBean hcm = createHashCodeMethod(getClassBean().getFields().values());
		MBMethod hscm = makeRealMethod(hcm, builtImmutableClass);
		if (getClassBean().isComparable()) {
			MBMethodBean ctm = createCompareToMethod(getClassBean().getFields().values(), builtImmutableInterface);
			MBMethod cmtd = makeRealMethod(ctm,builtImmutableClass);
		}
	}
	
	protected void buildElementsForImmutableInterface() {
		// built immutable interface
		addGetters(builtImmutableInterface, getClassBean().getFields().values());
		addRequiredMethods(builtImmutableInterface, getClassBean().getMethods().values(), false);
	}
	
	protected void buildElementsForMainInterface() {
		addSetters(builtInterface, getClassBean().getFields().values());
		addRequiredMethods(builtInterface, getClassBean().getMethods().values(), true);
	}
	
	protected void buildElementsForMain() {
		MBConstructor dc = makeDefaultConstructor(builtClass, getClassBean().getFields().values());
		if (dc.getParameters().size()>0) {
			makeNoArgConstructor(builtClass);
		}
		makeCloneConstructor(builtClass, getClassBean().getFields().values(), builtImmutableInterface);
		manageStaticFinalFields(builtClass, getClassBean().getFields().values());
		addFields(getClassBean().getFields().values(), builtClass);
		// built class.
		addNonStaticGetters(builtClass, getClassBean().getFields().values());
		addNonStaticSetters(builtClass, getClassBean().getFields().values());
		addRequiredMethods(builtClass, getClassBean().getMethods().values(), true);
		MBMethodBean em = createEqualsMethod(getClassBean().getFields().values(), builtClass);
		MBMethod eqm = makeRealMethod(em, builtClass);
		MBMethodBean hcm = createHashCodeMethod(getClassBean().getFields().values());
		MBMethod hscm = makeRealMethod(hcm, builtClass);

		if (getClassBean().isComparable()) {
			MBMethodBean ctm = createCompareToMethod(getClassBean().getFields().values(), builtImmutableInterface);
			MBMethod cmtd = makeRealMethod(ctm,builtClass);
		}
	}
}