package com.yannicklerestif.metapojos.elements.beans;

import com.yannicklerestif.metapojos.DataContainer;
import com.yannicklerestif.metapojos.plugin.MetaPojosHyperlinkedOutput;

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
		return "Call from " + source.toString() + "(l:" + line + ") to " + target.toString();
	}

	@Override
	public MetaPojosHyperlinkedOutput getHyperlinkedOutput() {
		MetaPojosHyperlinkedOutput result = new MetaPojosHyperlinkedOutput().add("Call from ")
				.add(source.toString() + "(l:" + line + ")", this).add(" to ");
		if (target.isShallow())
			result.add(target.toString() + " [shallow]");
		else
			result.add(target.toString(), target);
		return result;
	}

}
