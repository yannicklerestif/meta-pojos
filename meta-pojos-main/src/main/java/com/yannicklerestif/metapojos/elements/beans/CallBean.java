package com.yannicklerestif.metapojos.elements.beans;

import com.yannicklerestif.metapojos.DataContainer;

public class CallBean implements JavaElementBean {

	private MethodBean source;
	
	private int line;

	private MethodBean target;

	public CallBean(MethodBean source, int line, MethodBean target) {
		super();
		this.source = source;
		this.line = line;
		this.target = target;
	}

	public MethodBean getSource() {
		return source;
	}

	public int getLine() {
		return line;
	}

	public MethodBean getTarget() {
		return target;
	}
	
	public String toString() {
		return "Call from " + source.getClassBean().getName().replace('/', '.') + "." + source.getName() + "(" + DataContainer.classShortName(source.getClassBean().getName())
				+ ".java:" + line + ")" + " to " + target.getClassBean().getName().replace('/', '.') + "." + target.getName() + "(" + DataContainer.classShortName(target.getClassBean().getName())
				+ ".java:" + target.getLineNumber() + ")";

	}
	
}
