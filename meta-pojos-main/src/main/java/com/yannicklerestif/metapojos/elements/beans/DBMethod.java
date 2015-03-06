package com.yannicklerestif.metapojos.elements.beans;

import java.util.ArrayList;
import java.util.List;


public class DBMethod implements SourceObject {

	private DBClass dbClass;

	private String name;
	
	private String desc;
	
	private List<DBCall> calls = new ArrayList<DBCall>();
	
	public List<DBCall> getCalls() {
		return calls;
	}

	private List<DBCall> callsTo = new ArrayList<DBCall>();
	
	public List<DBCall> getCallsTo() {
		return callsTo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
	public DBClass getDBClass() {
		return dbClass;
	}

	public void setDBClass(DBClass dbClass) {
		this.dbClass = dbClass;
	}

	public static class DBMethodKey {
		public String methodName;
		public String desc;
		public DBMethodKey(String methodName, String desc) {
			super();
			this.methodName = methodName;
			this.desc = desc;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = desc.hashCode();
			result = prime * result + methodName.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DBMethodKey other = (DBMethodKey) obj;
			if (!desc.equals(other.desc))
				return false;
			if (!methodName.equals(other.methodName))
				return false;
			return true;
		}
		
	}

}
