package query;

import com.yannicklerestif.metapojos.MetaPojos;

public class MetaPojosQuery {
	public static void main(String[] args) throws Exception {
		MetaPojos mp = MetaPojos.start();
		mp.readClasses();
		//		mp.readClasses("/home/yannick/Projets/meta-pojos/meta-pojos-samples/src/classesLocations.txt");
//		mp.readClasses("C:/eclipse_workspaces/Java/meta-pojos/meta-pojos-samples/src/classesLocations-windows.txt");
//		mp.readClasses("/usr/lib/java/jdk1.8.0_31/jre/lib/rt.jar");
//		mp.singleClass(ClassBean.class.getName()).getMethods().matches("<init>").getCallsTo().print();
//		mp.singleClass("test.model.hierarchy.MyInterface").getMethods().getCallsTo().print();
		MetaPojos.getConsole().println(mp.allClasses().stream().count());
	}
}
