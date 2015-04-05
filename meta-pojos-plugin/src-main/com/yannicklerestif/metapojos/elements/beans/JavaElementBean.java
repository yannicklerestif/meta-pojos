package com.yannicklerestif.metapojos.elements.beans;

import com.yannicklerestif.metapojos.plugin.MetaPojosHyperlinkedOutput;

public abstract class JavaElementBean {
	//FIXME toString method for all JavaElementBeans : use the most common form (qualified name for class but not for parameters) 
	public abstract MetaPojosHyperlinkedOutput getHyperlinkedOutput();
}
