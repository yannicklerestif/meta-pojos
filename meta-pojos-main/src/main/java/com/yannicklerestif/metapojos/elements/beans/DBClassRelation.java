package com.yannicklerestif.metapojos.elements.beans;

public class DBClassRelation {
	public Integer getParentClassId() {
		return parentClassId;
	}
	public void setParentClassId(Integer parentClassId) {
		this.parentClassId = parentClassId;
	}
	public Integer getChildClassId() {
		return childClassId;
	}
	public void setChildClassId(Integer childClassId) {
		this.childClassId = childClassId;
	}
	private Integer parentClassId;
	private Integer childClassId;
	public DBClassRelation(Integer parentClassId, Integer childClassId) {
		this.parentClassId = parentClassId;
		this.childClassId = childClassId;
	}
}
