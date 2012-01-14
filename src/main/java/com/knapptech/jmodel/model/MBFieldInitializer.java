package com.knapptech.jmodel.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.knapptech.jmodel.model.element.MBField;
import com.knapptech.jmodel.model.type.np.MBNonPrimitiveType;

public class MBFieldInitializer {
	private String text;
	private List<MBPathMapping> requiredImports = new ArrayList<MBPathMapping>();
	private MBField field;
	
	private MBFieldInitializer(String text,MBField field) {
		if (text == null || text.length()<1)
			throw new IllegalArgumentException("Must define input string.");
		if (field== null || field.getInitializer() != null)
			throw new IllegalStateException("Field is null, or already has an initializer.");
		this.field = field;
		this.text = text;
	}
	
	public static final MBFieldInitializer createInitializer(String text,MBField field) {
		MBFieldInitializer fi = new MBFieldInitializer(text,field);
		field.addInitializer(fi);
		return fi;
	}
	
	public MBField getField() {
		return field;
	}
	
	public String getText() {
		return text;
	}
	
	public void addRequiredImport(MBPathMapping path) {
		this.requiredImports.add(path);
	}
	
	public Collection<MBPathMapping> getRequiredImports() {
		return Collections.unmodifiableCollection(requiredImports);
	}

	public void createImports(MBNonPrimitiveType owner) {
		for (MBPathMapping mpng : this.requiredImports) {
			MBImport.create(mpng, owner);
		}
	}
}