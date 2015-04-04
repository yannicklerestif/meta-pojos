package com.yannicklerestif.metapojos.plugin;

import java.util.ArrayList;
import java.util.List;

import com.yannicklerestif.metapojos.elements.beans.JavaElementBean;

public class MetaPojosHyperlinkableOutput {
	
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
	
	public void add(String text) {
		add(text, null);
	}
	
	public void add(String text, JavaElementBean bean) {
		outputParts.add(new MetaPojosOutputPart(text, bean));
	}
}
