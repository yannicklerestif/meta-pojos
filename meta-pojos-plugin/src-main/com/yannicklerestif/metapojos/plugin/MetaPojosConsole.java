package com.yannicklerestif.metapojos.plugin;

import com.yannicklerestif.metapojos.model.elements.beans.JavaElementBean;

public interface MetaPojosConsole {
	public void printJavaElementBean(JavaElementBean bean);
	public void printHyperlink(Object message, JavaElementBean bean);
	public void println(Object message);
	public void print(Object message);
	public void println();
	public void clear();
}
