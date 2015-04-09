package com.yannicklerestif.metapojos.elements.beans;

import java.util.ArrayList;
import java.util.List;

import com.yannicklerestif.metapojos.DataContainer;
import com.yannicklerestif.metapojos.plugin.MetaPojosHyperlinkedOutput;

public class MethodBean extends JavaElementBean {

	private boolean shallow = true;
	
	public boolean isShallow() {
		return shallow;
	}

	public void setShallow(boolean isShallow) {
		this.shallow = isShallow;
	}

	private ClassBean classBean;

	private String name;

	private String desc;

	private List<CallBean> calls = new ArrayList<CallBean>();

	public List<CallBean> getCalls() {
		return calls;
	}

	private List<CallBean> callsTo = new ArrayList<CallBean>();

	private String returnType;

	private int lineNumber = -1;

	public List<CallBean> getCallsTo() {
		return callsTo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	//TODO ugly
	public String getOriginalDesc() {
		return "(" + desc + ")" + returnType;
	}
	
	public ClassBean getClassBean() {
		return classBean;
	}

	public void setClassBean(ClassBean classBean) {
		this.classBean = classBean;
	}

	public static class MethodBeanKey {
		public String methodName;
		public String desc;

		public MethodBeanKey(String methodName, String desc) {
			super();
			this.methodName = methodName;
			this.desc = desc;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = desc.hashCode();
			result = prime * result + methodName.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MethodBeanKey other = (MethodBeanKey) obj;
			if (!desc.equals(other.desc))
				return false;
			if (!methodName.equals(other.methodName))
				return false;
			return true;
		}

	}

	public void setReturnType(String string) {
		this.returnType = string;
	}

	public void setLineNumber(int line) {
		this.lineNumber = line;
	}

	@Override
	public String toString() {
		return classBean.toString() + "." + name + "(" + desc + "):" + returnType;
	}

	@Override
	public MetaPojosHyperlinkedOutput getHyperlinkedOutput() {
		MetaPojosHyperlinkedOutput result = new MetaPojosHyperlinkedOutput().add("Method ");
		if(shallow)
			result.add(toString() + " [shallow]");
		else
			result.add(toString(), this);
		return result;
	}
	
	public int getLineNumber() {
		return lineNumber;
	}
}
