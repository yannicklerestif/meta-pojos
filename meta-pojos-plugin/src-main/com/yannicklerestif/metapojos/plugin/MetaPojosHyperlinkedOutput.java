package com.yannicklerestif.metapojos.plugin;

import java.util.ArrayList;
import java.util.List;

import com.yannicklerestif.metapojos.elements.beans.JavaElementBean;

public class MetaPojosHyperlinkedOutput {
	
	public static class MetaPojosOutputPart {
		String text;
		JavaElementBean bean;
		public MetaPojosOutputPart(String text, JavaElementBean bean) {
			super();
			this.text = text;
			this.bean = bean;
		}
	}
	
	List<MetaPojosOutputPart> outputParts = new ArrayList<>();
	
	public MetaPojosHyperlinkedOutput add(String text) {
		return add(text, null);
	}
	
	public MetaPojosHyperlinkedOutput add(String text, JavaElementBean bean) {
		outputParts.add(new MetaPojosOutputPart(text, bean));
		return this;
	}
}
