package com.knapptech.jmodel.build.strategic.single;

import com.knapptech.jmodel.beans.MBMethodBean;
import com.knapptech.jmodel.build.strategic.StrategicClassParser;
import com.knapptech.jmodel.model.element.MBConstructor;
import com.knapptech.jmodel.model.element.MBMethod;
import com.knapptech.jmodel.model.type.np.MBClass;

public class BeanClassParser extends StrategicClassParser {
	
	private MBClass builtClass;

	@Override
	public void buildTypes() {
		builtClass = makeDefaultClass(getClassBean(), getOwner());
		if (getClassBean().isComparable())
			addImplementsComparable(builtClass);
	}

	@Override
	public void buildElements() {
//		builtClass.setComment(getClassBean().getComment());
		MBConstructor dc = makeDefaultConstructor(builtClass, getClassBean().getFields().values());
		if (dc.getParameters().size()>0) {
			makeNoArgConstructor(builtClass);
		}
		makeCloneConstructor(builtClass, getClassBean().getFields().values(), builtClass);
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