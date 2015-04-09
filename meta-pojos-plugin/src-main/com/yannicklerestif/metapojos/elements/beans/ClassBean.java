package com.yannicklerestif.metapojos.elements.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yannicklerestif.metapojos.DataContainer;
import com.yannicklerestif.metapojos.elements.beans.MethodBean.MethodBeanKey;
import com.yannicklerestif.metapojos.plugin.MetaPojosHyperlinkedOutput;

public class ClassBean extends JavaElementBean {

	private Map<MethodBeanKey, MethodBean> methods = new HashMap<MethodBeanKey, MethodBean>();

	private String name;

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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getLineNumber() {
		return lineNumber;
	}

	public void setLineNumber(int lineNumber) {
		this.lineNumber = lineNumber;
	}

	@Override
	public String toString() {
		return name.replace('/', '.');
	}

	@Override
	public MetaPojosHyperlinkedOutput getHyperlinkedOutput() {
		return new MetaPojosHyperlinkedOutput().add("Class ").add(toString(), this);
	}

	private boolean rootOrInnerStatic = true;

	public boolean isRootOrInnerStatic() {
		return rootOrInnerStatic;
	}

	public void setRootOrInnerStatic(boolean rootOrInnerStatic) {
		this.rootOrInnerStatic = rootOrInnerStatic;
	}
}
