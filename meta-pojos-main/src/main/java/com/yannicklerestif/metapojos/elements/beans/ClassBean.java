package com.yannicklerestif.metapojos.elements.beans;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.yannicklerestif.metapojos.DataContainer;
import com.yannicklerestif.metapojos.elements.beans.MethodBean.MethodBeanKey;

public class ClassBean implements JavaElementBean {

	private Map<MethodBeanKey, MethodBean> methods = new HashMap<MethodBeanKey, MethodBean>();

	private String name;

	private boolean isShallow = true;
	
	private Set<ClassBean> parents = new HashSet<ClassBean>();
	
	private Set<ClassBean> children = new HashSet<ClassBean>();
	
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
	
	@Override
	public String toString() {
		return name.replace('/', '.') + ".<none>(" + DataContainer.classShortName(name) + ".java:-1)";
	}

}
