package com.yannicklerestif.metapojos.elements.streams;

import java.util.stream.Stream;

import com.yannicklerestif.metapojos.elements.beans.DBCall;

public class CallStream extends SourceObjectStream<DBCall, CallStream> {

	public MethodStream getTarget() {
		return new MethodStream(stream.map(dbCall -> dbCall.getTarget()));
	}
	
	//-----------------------------------------------
	
	public CallStream(Stream<DBCall> stream) {
		super(stream);
	}
	
	@Override
	protected CallStream wrap(Stream<DBCall> stream) {
		return new CallStream(stream);
	}
	
}
