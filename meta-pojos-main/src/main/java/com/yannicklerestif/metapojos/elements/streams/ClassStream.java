package com.yannicklerestif.metapojos.elements.streams;

import java.util.stream.Stream;

import com.yannicklerestif.metapojos.elements.beans.DBClass;

public class ClassStream extends SourceObjectStream<DBClass, ClassStream> {

	public MethodStream getMethods() {
		return new MethodStream(stream.flatMap(dbClass -> dbClass.getMethods().stream()));
	}

	//---------------------------------------------------------------

	public ClassStream(Stream<DBClass> stream) {
		super(stream);
	}
	
	@Override
	protected ClassStream wrap(Stream<DBClass> stream) {
		return new ClassStream(stream);
	}
	
}
