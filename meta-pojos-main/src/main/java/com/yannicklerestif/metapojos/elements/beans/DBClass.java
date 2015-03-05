package com.yannicklerestif.metapojos.elements.beans;

import java.util.ArrayList;
import java.util.List;

public class DBClass implements SourceObject {

	private List<DBMethod> methods = new ArrayList<>();

	private String name;

	private Integer id;

	private List<DBClassRelation> parents;

	public List<DBClassRelation> getParents() {
		return parents;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Integer getId() {
		return id;
	}
	
	public List<DBMethod> getMethods() {
		return methods;
	}

	public void setMethods(List<DBMethod> methods) {
		this.methods = methods;
	}

	public void setParents(List<DBClassRelation> parents) {
		this.parents = parents;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
