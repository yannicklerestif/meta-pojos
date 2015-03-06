package com.yannicklerestif.metapojos;

public class MetaPojosTest {
	public static void main(String[] args) throws Exception {
		// new MetaPojos()
		// .storeSingleClass("/home/yannick/Projets/meta-pojos/meta-pojos-test-classes/bin/test/model/StartingClass.class");
		// new MetaPojos()
		// .storeClassDirectory("/home/yannick/Projets/meta-pojos/meta-pojos-test-classes/bin");
		// new MetaPojos()
		// .storeJarFile("/home/yannick/Projets/db-derby-10.11.1.1-bin/lib/derby.jar");
		MetaPojos mp = MetaPojos.start(null);
//		mp.storeClasses("/home/yannick/Projets/meta-pojos/meta-pojos-test-classes/bin");
		mp.storeClasses("/usr/lib/java/jdk1.8.0_31/jre/lib/rt.jar");
	}
}
