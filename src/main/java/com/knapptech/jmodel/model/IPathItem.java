package com.knapptech.jmodel.model;

public interface IPathItem {
	MBPath getPath();
	MBPathMapping getPathMapping();
	String getLocalName();
	String getFullyQualifiedName();
	boolean isPackage();
}
