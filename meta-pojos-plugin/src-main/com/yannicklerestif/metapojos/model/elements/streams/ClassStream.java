package com.yannicklerestif.metapojos.model.elements.streams;

import java.util.stream.Stream;

import com.yannicklerestif.metapojos.model.elements.beans.ClassBean;
import com.yannicklerestif.metapojos.model.elements.beans.MethodBean;

public class ClassStream extends JavaElementStream<ClassBean, ClassStream> {

	//TODO getDerivedClasses
	//TODO getRecursiveDerivedClasses
	//TODO getBaseClasses
	//TODO getRecursiveBaseClasses
	//TODO getMethods(boolean includeInherited)
	//TODO getReferences
	//TODO getReferrers

	//TODO do not include shallow classes ?
	
	public ClassStream filterByName(String name) {
		return streamFilter(classBean -> classBean.getPrettyName().contains(name));
	}
	
	public MethodStream getMethods() {
		return new MethodStream(stream.flatMap(classBean -> classBean.getMethods().values().stream()));
	}

	public MethodStream getMethodsByName(String methodName) {
		return getMethods().streamFilter(methodBean -> methodBean.getName().contains(methodName));
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
