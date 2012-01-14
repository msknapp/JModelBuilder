package com.knapptech.jmodel.build;

import java.util.ArrayList;

import com.knapptech.jmodel.model.MBPath;
import com.knapptech.jmodel.model.type.delayed.IDelayedType;

public class DelayedTypeRegister {
	
	private static ArrayList<IDelayedType> types = new ArrayList<IDelayedType>();
	
	public static void add(IDelayedType type) {
		if (!types.contains(type))
			types.add(type);
	}
	
	public static ArrayList<MBPath> getNotFound() {
		ArrayList<MBPath> paths = new ArrayList<MBPath>();
		for (IDelayedType type : types) {
			if (type.isFound()) 
				continue;
			paths.add(type.getPath());
		}
		return paths;
	}
}
