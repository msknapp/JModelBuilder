package com.knapptech.jmodel.build.strategic.single;

import com.knapptech.jmodel.beans.MBFieldBean;
import com.knapptech.jmodel.beans.MBMethodBean;
import com.knapptech.jmodel.build.strategic.StrategicClassParser;
import com.knapptech.jmodel.model.MBFieldInitializer;
import com.knapptech.jmodel.model.MBParameter;
import com.knapptech.jmodel.model.element.MBConstructor;
import com.knapptech.jmodel.model.element.MBField;
import com.knapptech.jmodel.model.element.MBMethod;
import com.knapptech.jmodel.model.type.MBPrimitiveType;
import com.knapptech.jmodel.model.type.np.MBClass;

public class LockingClassParser extends StrategicClassParser {
	
	private MBClass builtClass;

	@Override
	public void buildTypes() {
		builtClass = makeDefaultClass(getClassBean(), getOwner());
		if (getClassBean().isComparable())
			addImplementsComparable(builtClass);
	}

	@Override
	public void buildElements() {
		MBConstructor dc = makeDefaultConstructor(builtClass, getClassBean().getFields().values());
		if (dc.getParameters().size()>0) {
			makeNoArgConstructor(builtClass);
		}
		makeCloneConstructor(builtClass, getClassBean().getFields().values(), builtClass);
		MBField lockField = new MBField(getLockedVariableName(),builtClass,MBPrimitiveType.BOOLEAN);
		lockField.getModifiers().makePrivate();
		MBFieldInitializer intlzr = MBFieldInitializer.createInitializer("false;", lockField);
		MBMethod lockMethod = new MBMethod(getLockMethodName(),builtClass,null);
		lockMethod.getModifiers().makePublic();
		lockMethod.getSourceCode().add("this."+getLockedVariableName()+"=true;");
		if (allowUnlock()) {
			MBMethod unlockMethod = new MBMethod(getUnlockMethodName(),builtClass,null);
			unlockMethod.getModifiers().makePublic();
			unlockMethod.getSourceCode().add("this."+getLockedVariableName()+"=false;");
		}

		manageStaticFinalFields(builtClass, getClassBean().getFields().values());
		addFields(getClassBean().getFields().values(), builtClass);
		addNonStaticGetters(builtClass, getClassBean().getFields().values());
		for (MBFieldBean fe : getClassBean().getFields().values()) {
			if (fe.isSettable() && !fe.isImmutable()) {
				MBMethodBean m = new MBMethodBean();
				m.setName("set"+capitalizeName(fe.getName()));
				m.addParameter(new MBParameter(fe.getType(), fe.getName()));
				m.getCode().add("if ("+getLockedVariableName()+")");
				addLockResponseForSet(m);
				if (!fe.isNullable() && !fe.getType().isPrimitive()) {
					m.getCode().addLine("if ("+fe.getName()+" == null)");
					m.getCode().addIndented("throw new NullPointerException(\"This field cannot be null.\");");
				}
				m.getCode().addLine("this."+fe.getName()+" = "+fe.getName()+";");
				m.getModifiers().setStatic(fe.isStatic());
				m.getModifiers().makePublic();
				MBMethod mm = makeRealMethod(m, builtClass);
			}
		}
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
	
	protected void addLockResponseForSet(MBMethodBean m) {
		m.getCode().addIndented("throw new IllegalStateException(\"Cannot use set method, this is locked.\");");
	}
	
	protected String getLockedVariableName() {
		return "_locked";
	}
	
	protected String getLockMethodName() {
		return "lock";
	}
	
	protected String getUnlockMethodName() {
		return "unlock";
	}
	
	protected boolean allowUnlock() {
		return false;
	}
}