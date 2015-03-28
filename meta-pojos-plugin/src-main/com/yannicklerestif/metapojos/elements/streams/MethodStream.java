package com.yannicklerestif.metapojos.elements.streams;

import java.util.stream.Stream;

import com.yannicklerestif.metapojos.elements.beans.MethodBean;

public class MethodStream extends JavaElementStream<MethodBean, MethodStream> {

	public CallStream getCalls() {
		return new CallStream(stream.flatMap(methodBean -> methodBean.getCalls().stream()));
	}
	
	public CallStream getCallsTo() {
		return new CallStream(stream.flatMap(methodBean -> methodBean.getCallsTo().stream()));
	}
	
	//--------------------------------------------------------------
	
	public MethodStream(Stream<MethodBean> stream) {
		super(stream);
	}

	@Override
	protected MethodStream wrap(Stream<MethodBean> stream) {
		return new MethodStream(stream);
	}

}
