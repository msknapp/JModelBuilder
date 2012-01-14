/*
  Copyright 2011 Michael Scott Knapp
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
  	http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

*/
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