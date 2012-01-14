package com.knapptech.jmodel.build.strategic.pairs;

import java.util.ArrayList;
import java.util.List;

import com.knapptech.jmodel.beans.MBFieldBean;
import com.knapptech.jmodel.beans.MBMethodBean;
import com.knapptech.jmodel.build.strategic.StrategicClassParser;
import com.knapptech.jmodel.model.MBParameter;
import com.knapptech.jmodel.model.Visibility;
import com.knapptech.jmodel.model.element.MBConstructor;
import com.knapptech.jmodel.model.element.MBMethod;
import com.knapptech.jmodel.model.type.np.MBClass;
import com.knapptech.jmodel.model.type.np.MBInterface;
import com.knapptech.jmodel.model.type.np.MBInterfaceWithParameters;
import com.knapptech.jmodel.model.type.parametered.MBParameteredInterface;

public class ImmutableAndInterfaceClassParser extends StrategicClassParser {
	
	private MBClass builtClass;
	private MBInterface builtInterface;

	@Override
	public void buildTypes() {
		builtClass = makeDefaultClass(getClassBean(), getOwner());
		builtInterface = MBInterface.getOrCreate("I"+getClassBean().getLocalName(),getOwner());
		builtClass.addImplementedInterface(builtInterface);
		MBParameteredInterface cprbl = MBInterfaceWithParameters.createComparableTo(builtInterface);
		if (getClassBean().isComparable() && builtInterface.getImplementedInterfaces().contains(cprbl)) {
			builtInterface.addImplementedInterface(cprbl);
		}
	}

	@Override
	public void buildElements() {
		List<MBParameter> prms = new ArrayList<MBParameter>();
		for (MBFieldBean fld : getClassBean().getFields().values()) {
			fld.setImmutable(true);
			fld.setFinal(true);
			fld.setSettable(false);
			prms.add(new MBParameter(fld.getType(), fld.getName()));
		}
		MBConstructor cns = MBConstructor.getOrCreate(builtClass,prms.toArray(new MBParameter[prms.size()]));
		cns.getModifiers().setVisibility(Visibility.PUBLIC);
		for (MBFieldBean fld : getClassBean().getFields().values()) {
			cns.getSourceCode().add("this."+fld.getName()+"="+fld.getName()+";");
		}
		manageStaticFinalFields(builtClass, getClassBean().getFields().values());
		addFields(getClassBean().getFields().values(), builtClass);
		addNonStaticGetters(builtClass, getClassBean().getFields().values());
		addGetters(builtInterface, getClassBean().getFields().values());
		addRequiredMethods(builtClass, getClassBean().getMethods().values(), false);
		addRequiredMethods(builtInterface, getClassBean().getMethods().values(), false);
		MBMethodBean em = createEqualsMethod(getClassBean().getFields().values(), builtClass);
		MBMethodBean hcm = createHashCodeMethod(getClassBean().getFields().values());
		MBMethod eqm = makeRealMethod(em, builtClass);
		MBMethod hscm = makeRealMethod(hcm,builtClass);
		if (getClassBean().isComparable()) {
			MBMethodBean ctm = createCompareToMethod(getClassBean().getFields().values(), builtInterface);
			MBMethod cmtd = makeRealMethod(ctm,builtClass);
		}
	}
}