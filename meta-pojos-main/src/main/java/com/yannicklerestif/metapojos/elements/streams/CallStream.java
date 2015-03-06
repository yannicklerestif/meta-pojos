package com.yannicklerestif.metapojos.elements.streams;

import java.util.stream.Stream;

import com.yannicklerestif.metapojos.elements.beans.CallBean;

public class CallStream extends JavaElementStream<CallBean, CallStream> {

	public MethodStream getTarget() {
		return new MethodStream(stream.map(callBean -> callBean.getTarget()));
	}
	
	//-----------------------------------------------
	
	public CallStream(Stream<CallBean> stream) {
		super(stream);
	}
	
	@Override
	protected CallStream wrap(Stream<CallBean> stream) {
		return new CallStream(stream);
	}
	
}
