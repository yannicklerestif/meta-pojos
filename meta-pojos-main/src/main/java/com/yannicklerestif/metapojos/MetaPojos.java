package com.yannicklerestif.metapojos;

public class MetaPojos {
	DatabaseManager dbm;

	public MetaPojos() throws Exception {
		System.setProperty("derby.system.home",
				"/home/yannick/.meta-pojos/derby");
		dbm = new DatabaseManager();
	}

	public void storeSingleClass(String fileName) throws Exception {
		dbm.storeSingleClass(fileName);
	}

	public void storeClassDirectory(String dirName) throws Exception {
		dbm.storeClassDirectory(dirName);
	}
	public void storeJarFile(String jarFileName) throws Exception {
		dbm.storeJarFile(jarFileName);
	}
	
	public void bulkInsert() throws Exception {
		dbm.bulkInsert();
	}
}