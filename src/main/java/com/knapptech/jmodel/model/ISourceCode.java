package com.knapptech.jmodel.model;

import java.util.Set;

import com.knapptech.jmodel.model.type.np.MBException;

public interface ISourceCode extends IWritable {
	Set<MBException> getExceptions();
}
