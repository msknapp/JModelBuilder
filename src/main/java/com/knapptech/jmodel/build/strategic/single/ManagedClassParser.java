package com.knapptech.jmodel.build.strategic.single;

import java.util.List;

import com.knapptech.jmodel.beans.MBFieldBean;
import com.knapptech.jmodel.beans.MBMethodBean;
import com.knapptech.jmodel.build.strategic.StrategicClassParser;
import com.knapptech.jmodel.model.MBFieldInitializer;
import com.knapptech.jmodel.model.MBParameter;
import com.knapptech.jmodel.model.element.MBConstructor;
import com.knapptech.jmodel.model.element.MBField;
import com.knapptech.jmodel.model.element.MBMethod;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.MBPrimitiveType;
import com.knapptech.jmodel.model.type.MBPrimitiveWrapper;
import com.knapptech.jmodel.model.type.np.MBClass;
import com.knapptech.jmodel.model.type.parametered.MBParameteredClass;

public class ManagedClassParser extends StrategicClassParser {

	private MBClass builtClass;
	private boolean singleKey = false;
	private IType mapKeyType = MBPrimitiveWrapper.INTEGER;
	private IType hashMapType = null;
	
	@Override
	public void buildTypes() {
		builtClass = makeDefaultClass(getClassBean(), getOwner());
		if (getClassBean().isComparable())
			addImplementsComparable(builtClass);
	}

	@Override
	public void buildElements() {
		List<MBFieldBean> neededFields = getNonStaticFields(  fieldsMeetingAnyCriteria(
				getClassBean().getFields().values(), true, true, true, false, false,true) );
		List<MBFieldBean> keyFields = getNonStaticFields( fieldsMeetingAnyCriteria(
				getClassBean().getFields().values(), false, false, false, false, false,true));
		if (keyFields.size()<1) {
			throw new RuntimeException("Managed classes must have at least one field with key=\"true\"");
		}
		singleKey = keyFields.size()==1;
		if (singleKey) {
			mapKeyType = (IType) keyFields.get(0).getType();
		}
		hashMapType = MBParameteredClass.createHashMap(
				mapKeyType, 
				builtClass);
		
		MBParameter[] neededParameters = convertFieldsToParameters(neededFields);
		MBParameter[] keyParameters = convertFieldsToParameters(keyFields);
		makeGetOrCreateMethod(neededParameters,keyParameters);
		makeGetIfExistsMethod(keyParameters);
		if (!singleKey) {
			makeBuildHashCodeMethod(keyParameters);
		}
		MBConstructor dc = makeConstructorForFields(builtClass, neededFields);
		dc.getModifiers().makePrivate();
		
		MBField mngr = new MBField("MANAGEDMAP",builtClass,hashMapType);
		mngr.getModifiers().makePrivate();
		mngr.getModifiers().setStatic(true);
		mngr.getModifiers().setFinal(true);
		IType kt = mapKeyType;
		if (kt instanceof MBPrimitiveType)
			kt=((MBPrimitiveType)kt).getWrapper();
		MBFieldInitializer.createInitializer("new HashMap<"+kt.getLocalName()+","+builtClass.getLocalName()+">();",mngr);

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
	
	private void makeBuildHashCodeMethod(MBParameter[] keyParameters) {
		MBMethod bhcm = new MBMethod("buildHashCode",builtClass,MBPrimitiveType.INT,keyParameters);
		bhcm.getModifiers().setStatic(true);
		bhcm.getModifiers().makePrivate();
		bhcm.getModifiers().setFinal(true);
		bhcm.getSourceCode().add("int i = 1;");
		for (MBParameter prm : keyParameters) {
			IType t = prm.getType();
			if (t instanceof MBPrimitiveType) {
				MBPrimitiveType pt = (MBPrimitiveType)t;
				if (pt==MBPrimitiveType.BOOLEAN) {
					bhcm.getSourceCode().add("if ("+prm.getName()+")");
					bhcm.getSourceCode().addIndented("i *= 1000;");
				} else if (pt == MBPrimitiveType.CHAR) {
					bhcm.getSourceCode().add("i += (int)"+prm.getName()+";");
				} else if (pt.isFloatingPointType()) {
					bhcm.getSourceCode().add("i += (int)(1001*"+prm.getName()+");");
				} else if (pt.isNumberType()) {
					bhcm.getSourceCode().add("i += (int)"+prm.getName()+";");
				}
			} else {
				bhcm.getSourceCode().add("if ("+prm.getName()+"!=null)");
				bhcm.getSourceCode().addIndented("i += "+prm.getName()+".hashCode();");
			}
		}
		bhcm.getSourceCode().add("return i;");
	}

	private void makeGetIfExistsMethod(MBParameter[] keyParameters) {
		MBMethod gocm = new MBMethod("getIfExists",builtClass,builtClass,keyParameters);
		gocm.getModifiers().setStatic(true);
		gocm.getModifiers().setFinal(true);
		gocm.getModifiers().makePublic();
		if (singleKey) {
			gocm.getSourceCode().add("return MANAGEDMAP.get("+keyParameters[0].getName()+");");
		} else {
			String s = "int hc = buildHashCode(";
			for (int i = 0;i<keyParameters.length;i++) {
				s+=keyParameters[i].getName();
				if (i<keyParameters.length-1)
					s+=", ";
			}
			s+=");";
			gocm.getSourceCode().add(s);
			gocm.getSourceCode().add(getClassBean().getLocalName()+" existingInstance = MANAGEDMAP.get(hc);");
			gocm.getSourceCode().add("return existingInstance;");
		}
	}

	protected void makeGetOrCreateMethod(MBParameter[] neededParameters,MBParameter[] keyParameters) {
		MBMethod gocm = new MBMethod("getOrCreate",builtClass,builtClass,neededParameters);
		gocm.getModifiers().setStatic(true);
		gocm.getModifiers().setFinal(true);
		gocm.getModifiers().makePublic();
		if (!singleKey) {
			String s = "int hc = buildHashCode(";
			for (int i = 0;i<keyParameters.length;i++) {
				s+=keyParameters[i].getName();
				if (i<keyParameters.length-1)
					s+=", ";
			}
			s+=");";
			gocm.getSourceCode().add(s);
			gocm.getSourceCode().add(getClassBean().getLocalName()+" existingInstance = MANAGEDMAP.get(hc);");
		} else {
			gocm.getSourceCode().add(getClassBean().getLocalName()+" existingInstance = MANAGEDMAP.get("+keyParameters[0].getName()+");");
		}
		gocm.getSourceCode().add("if (existingInstance != null) ");
		gocm.getSourceCode().addIndented("return existingInstance;");
		String cnsc = "existingInstance = new "+getClassBean().getLocalName()+"(";
		for (int i = 0;i<neededParameters.length;i++) {
			cnsc+=neededParameters[i].getName();
			if (i<neededParameters.length-1)
				cnsc+=", ";
		}
		cnsc+=");";
		gocm.getSourceCode().add(cnsc);
		gocm.getSourceCode().add("MANAGEDMAP.put("+
				(singleKey ? keyParameters[0].getName() : "hc")
				+",existingInstance);");
		gocm.getSourceCode().add("return existingInstance;");
	}
}
