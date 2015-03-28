package com.yannicklerestif.metapojos.elements.streams;

import java.util.stream.Stream;

import com.yannicklerestif.metapojos.elements.beans.ClassBean;

public class ClassStream extends JavaElementStream<ClassBean, ClassStream> {

	//TODO getDerivedClasses
	//TODO getRecursiveDerivedClasses
	//TODO getBaseClasses
	//TODO getRecursiveBaseClasses
	//TODO getReferences
	//TODO getReferrers
	
	public MethodStream getMethods() {
		return new MethodStream(stream.flatMap(classBean -> classBean.getMethods().values().stream()));
	}

	//---------------------------------------------------------------

	public ClassStream(Stream<ClassBean> stream) {
		super(stream);
	}
	
	@Override
	protected ClassStream wrap(Stream<ClassBean> stream) {
		return new ClassStream(stream);
	}
	
}
