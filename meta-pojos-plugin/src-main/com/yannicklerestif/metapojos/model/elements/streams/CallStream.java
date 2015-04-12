package com.yannicklerestif.metapojos.model.elements.streams;

import java.util.stream.Stream;

import com.yannicklerestif.metapojos.model.elements.beans.CallBean;

public class CallStream extends JavaElementStream<CallBean, CallStream> {

	public MethodStream getSource() {
		return new MethodStream(stream.map(callBean -> callBean.getSource()));
	}
	
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
