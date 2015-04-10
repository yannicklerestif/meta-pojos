package com.yannicklerestif.metapojos.elements.beans;

import com.yannicklerestif.metapojos.plugin.MetaPojosHyperlinkedOutput;

public abstract class JavaElementBean {
	//FIXME do not rely on toString() method for computations. Instead, delegate toString() calls to a getCanonicalName() method
	public abstract MetaPojosHyperlinkedOutput getHyperlinkedOutput();
}
