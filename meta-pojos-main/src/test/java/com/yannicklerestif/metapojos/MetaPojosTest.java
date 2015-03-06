package com.yannicklerestif.metapojos;


public class MetaPojosTest {
	public static void main(String[] args) throws Exception {
		MetaPojos mp = MetaPojos.start();
		mp.readClasses("/home/yannick/Projets/meta-pojos/meta-pojos-test-classes/bin");
//		mp.readClasses("/usr/lib/java/jdk1.8.0_31/jre/lib/rt.jar");
		mp.allClasses().getMethods().getCalls().print();
//		mp.singleClass("test.model.hierarchy.MyInterface").getMethods().getCallsTo().print();
	}
}
