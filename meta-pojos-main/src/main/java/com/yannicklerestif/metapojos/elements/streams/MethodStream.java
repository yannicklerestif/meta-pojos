package com.yannicklerestif.metapojos.elements.streams;

import java.util.stream.Stream;

import com.yannicklerestif.metapojos.elements.beans.DBMethod;

public class MethodStream extends SourceObjectStream<DBMethod, MethodStream> {

	//--------------------------------------------------------------
	
	public MethodStream(Stream<DBMethod> stream) {
		super(stream);
	}

	@Override
	protected MethodStream wrap(Stream<DBMethod> stream) {
		return new MethodStream(stream);
	}

}
