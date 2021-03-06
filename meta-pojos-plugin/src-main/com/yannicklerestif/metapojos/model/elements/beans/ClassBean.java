package com.yannicklerestif.metapojos.model.elements.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.yannicklerestif.metapojos.model.elements.beans.MethodBean.MethodBeanKey;

public class ClassBean extends JavaElementBean {

	private Map<MethodBeanKey, MethodBean> methods = new HashMap<MethodBeanKey, MethodBean>();

	private String internalName;

	private boolean isShallow = true;

	private Set<ClassBean> parents = new HashSet<ClassBean>();

	private Set<ClassBean> children = new HashSet<ClassBean>();

	private int lineNumber = -1;

	public void addParent(ClassBean parent) {
		parents.add(parent);
	}

	public void addChild(ClassBean child) {
		parents.add(child);
	}

	public boolean isShallow() {
		return isShallow;
	}

	public void setShallow(boolean isShallow) {
		this.isShallow = isShallow;
	}

	public Map<MethodBeanKey, MethodBean> getMethods() {
		return methods;
	}

	public void setMethods(Map<MethodBeanKey, MethodBean> methods) {
		this.methods = methods;
	}

	public String getInternalName() {
		return internalName;
	}

	public void setInternalName(String name) {
		this.internalName = name;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	public String getPrettyName() {
		return internalName.replace('/', '.');
	}
	
	@Override
	public String toString() {
		return getPrettyName();
	}

	private boolean rootOrInnerStatic = true;

	public boolean isRootOrInnerStatic() {
		return rootOrInnerStatic;
	}

	public void setRootOrInnerStatic(boolean rootOrInnerStatic) {
		this.rootOrInnerStatic = rootOrInnerStatic;
	}
}
