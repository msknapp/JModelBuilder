package com.knapptech.jmodel.build.strategic.single;

import java.util.List;

import com.knapptech.jmodel.beans.MBFieldBean;
import com.knapptech.jmodel.beans.MBMethodBean;
import com.knapptech.jmodel.build.strategic.StrategicClassParser;
import com.knapptech.jmodel.model.MBParameter;
import com.knapptech.jmodel.model.element.MBConstructor;
import com.knapptech.jmodel.model.element.MBMethod;
import com.knapptech.jmodel.model.type.np.MBClass;

public class FactoryClassParser extends StrategicClassParser {

	private MBClass builtClass;

	@Override
	public void buildTypes() {
		builtClass = makeDefaultClass(getClassBean(), getOwner());
		if (getClassBean().isComparable())
			addImplementsComparable(builtClass);
	}

	@Override
	public void buildElements() {
		List<MBFieldBean> neededFields = fieldsMeetingAnyCriteria(
				getClassBean().getFields().values(), true, true, true, false, false,false);
		MBParameter[] neededParameters = convertFieldsToParameters(neededFields);
		MBConstructor dc = makeConstructorForFields(builtClass, neededFields);
		dc.getModifiers().makePrivate();
		MBMethod cm = new MBMethod("create",builtClass,builtClass,neededParameters);
		cm.getModifiers().setStatic(true);
		cm.getModifiers().setFinal(true);
		cm.getModifiers().makePublic();
		String s = "return new "+builtClass.getLocalName()+"(";
		for (int i = 0;i<neededParameters.length;i++) {
			s+=neededParameters[i].getName();
			if (i<neededParameters.length-1)
				s+=", ";
		}
		s+=");";
		cm.getSourceCode().add(s);
		manageStaticFinalFields(builtClass, getClassBean().getFields().values());
		addFields(getClassBean().getFields().values(), builtClass);
		addNonStaticGetters(builtClass, getClassBean().getFields().values());
		addNonStaticSetters(builtClass, getClassBean().getFields().values());
		addRequiredMethods(builtClass, getClassBean().getMethods().values(), true);
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
