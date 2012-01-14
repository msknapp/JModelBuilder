package com.knapptech.jmodel.model.type.parametered;

import com.knapptech.jmodel.model.MBGenericParameter;
import com.knapptech.jmodel.model.type.IType;
import com.knapptech.jmodel.model.type.np.IHasGenericParameters;
import com.knapptech.jmodel.model.type.np.IInterface;
import com.knapptech.jmodel.model.type.np.MBInterfaceWithParameters;

public class MBParameteredInterface extends MBParameteredType implements IInterface, IHasGenericParameters {
	
	public MBParameteredInterface(MBInterfaceWithParameters intrface) {
		if (intrface == null)
			throw new NullPointerException("Must define intrface.");
		this.type = intrface;
		init();
	}
	
	public MBParameteredInterface(MBInterfaceWithParameters intrface,IType... type) {
		if (intrface == null)
			throw new NullPointerException("Must define intrface.");
		this.type = intrface;
		init();
		int i = 0;
		for (MBGenericParameter prm : intrface.getParameters()) {
			String rep = prm.getRepresentation();
			setType(rep, type[i]);
			i++;
			if (i>=type.length)
				break;
		}
	}
	
	public static MBParameteredInterface createComparableTo(IType t) {
		MBParameteredInterface mpi = new MBParameteredInterface(MBInterfaceWithParameters.COMPARABLE);
		mpi.setType("E", t);
		return mpi;
	}
	
	private MBInterfaceWithParameters getInterfaceWithParameters() {
		return (MBInterfaceWithParameters)type;
	}
	
	public void refreshImports() {
		getInterfaceWithParameters().refreshImports();
	}
}