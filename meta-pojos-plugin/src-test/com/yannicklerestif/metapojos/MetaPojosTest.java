package com.yannicklerestif.metapojos;

import com.yannicklerestif.metapojos.elements.beans.ClassBean;



public class MetaPojosTest {
	public static void main(String[] args) throws Exception {
		MetaPojos mp = MetaPojos.start();
//		mp.readClasses("/home/yannick/Projets/meta-pojos/meta-pojos-main/src/test/java/classesLocations.txt");
		mp.readClasses("C:/eclipse_workspaces/Java/meta-pojos/meta-pojos-main/src/test/java/classesLocations-windows.txt");
//		mp.readClasses("/usr/lib/java/jdk1.8.0_31/jre/lib/rt.jar");
//		mp.singleClass(ClassBean.class.getName()).getMethods().matches("<init>").getCallsTo().print();
//		mp.singleClass("test.model.hierarchy.MyInterface").getMethods().getCallsTo().print();

		//TODO output in target eclipse
		System.out.println(mp.allClasses().stream().count());
	}
}
