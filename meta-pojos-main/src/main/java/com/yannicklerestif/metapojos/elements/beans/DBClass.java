package com.yannicklerestif.metapojos.elements.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yannicklerestif.metapojos.elements.beans.DBMethod.DBMethodKey;

public class DBClass implements SourceObject {

	private Map<DBMethodKey, DBMethod> methods = new HashMap<DBMethodKey, DBMethod>();

	private String name;

	private boolean isShallow = true;
	
	private Set<DBClass> parents = new HashSet<DBClass>();
	
	private Set<DBClass> children = new HashSet<DBClass>();
	
	public void addParent(DBClass parent) {
		parents.add(parent);
	}
	
	public void addChild(DBClass child) {
		parents.add(child);
	}
	
	public boolean isShallow() {
		return isShallow;
	}

	public void setShallow(boolean isShallow) {
		this.isShallow = isShallow;
	}

	public Map<DBMethodKey, DBMethod> getMethods() {
		return methods;
	}

	public void setMethods(Map<DBMethodKey, DBMethod> methods) {
		this.methods = methods;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
