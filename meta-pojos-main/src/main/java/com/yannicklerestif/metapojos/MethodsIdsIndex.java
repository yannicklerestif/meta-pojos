package com.yannicklerestif.metapojos;

import java.util.HashMap;
import java.util.Map;

public class MethodsIdsIndex {

	private Map<Key, Integer> idsByKeyMap = new HashMap<Key, Integer>();

	private int maxId = -1;

	public Integer getOrCreateId(Integer classId, String methodName, String desc) {
		Key key = new Key(classId, methodName, desc);
		Integer result = idsByKeyMap.get(key);
		if (result != null)
			return result;
		maxId++;
		idsByKeyMap.put(key, maxId);
		return maxId;
	}

	private static class Key {
		Integer classId;
		String methodName;
		String desc;

		public Key(Integer classId, String methodName, String desc) {
			super();
			this.classId = classId;
			this.methodName = methodName;
			this.desc = desc;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = classId.hashCode();
			result = prime * result + desc.hashCode();
			result = prime * result + methodName.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (!(obj instanceof Key))
				return false;
			Key other = (Key) obj;
			if (!classId.equals(other.classId))
				return false;
			if (!desc.equals(other.desc))
				return false;
			if (!methodName.equals(other.methodName))
				return false;
			return true;
		}

	}

}
