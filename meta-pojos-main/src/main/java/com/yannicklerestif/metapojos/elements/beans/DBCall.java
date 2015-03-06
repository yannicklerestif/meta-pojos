package com.yannicklerestif.metapojos.elements.beans;

public class DBCall implements SourceObject {

	private DBMethod source;
	
	private int line;

	private DBMethod target;

	public DBCall(DBMethod source, int line, DBMethod target) {
		super();
		this.source = source;
		this.line = line;
		this.target = target;
	}

	public DBMethod getSource() {
		return source;
	}

	public int getLine() {
		return line;
	}

	public DBMethod getTarget() {
		return target;
	}
	
	
	
}
