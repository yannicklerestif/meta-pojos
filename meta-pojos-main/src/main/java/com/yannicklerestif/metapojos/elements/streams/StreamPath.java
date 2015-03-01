package com.yannicklerestif.metapojos.elements.streams;

import com.yannicklerestif.metapojos.elements.beans.SourceObject;

public class StreamPath<T extends SourceObject> {
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
