package com.yannicklerestif.metapojos.model.elements.beans;


public class CallBean extends JavaElementBean {

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
		return "Call from " + source.getPrettyName() + "(l:" + line + ") to " + target.getPrettyName();
	}

}
