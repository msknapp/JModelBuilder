package com.knapptech.jmodel.build.strategic.single;

import java.util.ArrayList;
import java.util.List;

import com.knapptech.jmodel.beans.MBFieldBean;
import com.knapptech.jmodel.beans.MBMethodBean;
import com.knapptech.jmodel.build.strategic.StrategicClassParser;
import com.knapptech.jmodel.model.MBParameter;
import com.knapptech.jmodel.model.Visibility;
import com.knapptech.jmodel.model.element.MBConstructor;
import com.knapptech.jmodel.model.element.MBMethod;
import com.knapptech.jmodel.model.type.np.IClass;
import com.knapptech.jmodel.model.type.np.MBClass;

public class ImmutableClassParser extends StrategicClassParser {
	
	private MBClass builtClass;

	@Override
	public void buildTypes() {
		IClass prnt = (IClass) getClassBean().getExtends();
		builtClass = makeDefaultClass(getClassBean(), getOwner());
		addInterfacesImplemented(getClassBean().getImplements(), builtClass);
	}

	@Override
	public void buildElements() {
//		List<MBParameter> prms = new ArrayList<MBParameter>();
//		for (MBFieldBean fld : getClassBean().getFields().values()) {
//			if (fld.isStatic())
//				continue;
//			fld.setImmutable(true);
//			fld.setFinal(true);
//			fld.setSettable(false);
//			prms.add(new MBParameter(fld.getType(), fld.getName()));
//		}
//		MBConstructor cns = MBConstructor.getOrCreate(builtClass,prms.toArray(new MBParameter[prms.size()]));
//		cns.getModifiers().setVisibility(Visibility.PUBLIC);
//		for (MBFieldBean fld : getClassBean().getFields().values()) {
//			if (fld.isStatic())
//				continue;
//			cns.getSourceCode().add("this."+fld.getName()+"="+fld.getName()+";");
//		}

		MBConstructor cns = makeConstructorForImmutable(builtClass,getClassBean().getFields().values());

		manageStaticFinalFields(builtClass, getClassBean().getFields().values());
		addFields(getClassBean().getFields().values(), builtClass);
		addNonStaticGetters(builtClass, getClassBean().getFields().values());
		addRequiredMethods(builtClass, getClassBean().getMethods().values(), false);
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