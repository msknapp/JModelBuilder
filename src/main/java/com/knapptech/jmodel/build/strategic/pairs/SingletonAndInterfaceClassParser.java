package com.knapptech.jmodel.build.strategic.pairs;

import com.knapptech.jmodel.beans.MBMethodBean;
import com.knapptech.jmodel.build.strategic.StrategicClassParser;
import com.knapptech.jmodel.model.MBFieldInitializer;
import com.knapptech.jmodel.model.element.MBConstructor;
import com.knapptech.jmodel.model.element.MBField;
import com.knapptech.jmodel.model.element.MBMethod;
import com.knapptech.jmodel.model.type.np.MBClass;
import com.knapptech.jmodel.model.type.np.MBInterface;

public class SingletonAndInterfaceClassParser extends StrategicClassParser {

	private MBClass builtClass;
	private MBInterface builtInterface;
	
	@Override
	public void buildTypes() {
		builtClass = makeDefaultClass(getClassBean(), getOwner());
		builtClass.getModifiers().setFinal(true);
		builtInterface = MBInterface.getOrCreate("I"+getClassBean().getLocalName(),getOwner());
		if (getClassBean().isComparable())
			addImplementsComparable(builtInterface);
	}
	
	@Override
	public void buildElements() {
		MBConstructor cns = MBConstructor.getOrCreate(builtClass);
		cns.getModifiers().makePrivate();
		MBField nst = new MBField("INSTANCE", builtClass, builtClass);
		nst.getModifiers().makePrivate();
		nst.getModifiers().setStatic(true);
		nst.getModifiers().setFinal(true);
		MBFieldInitializer.createInitializer("new "+builtClass.getLocalName()+"()", nst);
		MBMethod nstm = new MBMethod("getInstance",builtClass,builtClass);
		nstm.getSourceCode().add("return INSTANCE;");
		nstm.getModifiers().setFinal(true);
		nstm.getModifiers().setStatic(true);
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
		MBMethod hscm = makeRealMethod(hcm,builtClass);
		hscm.setSourceCode(hcm.getSourceCode());
		if (getClassBean().isComparable()) {
			MBMethodBean ctm = createCompareToMethod(getClassBean().getFields().values(), builtClass);
			MBMethod cmtd = makeRealMethod(ctm,builtClass);
		}
	}
}