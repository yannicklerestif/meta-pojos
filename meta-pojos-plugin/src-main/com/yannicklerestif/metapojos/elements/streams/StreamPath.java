package com.yannicklerestif.metapojos.elements.streams;

import com.yannicklerestif.metapojos.elements.beans.JavaElementBean;

public class StreamPath<T extends JavaElementBean> {
	private StreamPath previousSteps;
	private T sourceObject;

	public StreamPath getPreviousSteps() {
		return previousSteps;
	}

	public T getSourceObject() {
		return sourceObject;
	}

	public StreamPath(StreamPath previousSteps, T sourceObject) {
		this.previousSteps = previousSteps;
		this.sourceObject = sourceObject;
	}
	
	@Override
	public int hashCode() {
		return sourceObject.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof StreamPath))
			return false;
		StreamPath o = (StreamPath) obj; 
		return sourceObject.equals(o.sourceObject);
	}
}
