package com.yannicklerestif.metapojos;

/**
 * Unit test for simple App.
 */
public class ImportMain {
	public static void main(String[] args) throws Exception {
		// new MetaPojos()
		// .storeSingleClass("/home/yannick/Projets/meta-pojos/meta-pojos-test-classes/bin/test/model/StartingClass.class");
		//		new MetaPojos()
//		.storeClassDirectory("/home/yannick/Projets/meta-pojos/meta-pojos-test-classes/bin");
//		new MetaPojos()
//		.storeJarFile("/home/yannick/Projets/db-derby-10.11.1.1-bin/lib/derby.jar");
		new MetaPojos()
		.bulkInsert();
	}
}
