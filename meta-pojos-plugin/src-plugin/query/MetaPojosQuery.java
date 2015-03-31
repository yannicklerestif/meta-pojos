package query;

import com.yannicklerestif.metapojos.MetaPojos;

//FIXME change location in PDE project otherwise PDE version hides user version !!!
public class MetaPojosQuery {
	public static void main(String[] args) throws Exception {
		MetaPojos mp = MetaPojos.start();
		mp.readClasses("/home/yannick/Projets/meta-pojos/meta-pojos-plugin/src-test/classesLocations.txt");
//		mp.readClasses("C:/eclipse_workspaces/Java/meta-pojos/meta-pojos-main/src/test/java/classesLocations-windows.txt");
//		mp.readClasses("/usr/lib/java/jdk1.8.0_31/jre/lib/rt.jar");
//		mp.singleClass(ClassBean.class.getName()).getMethods().matches("<init>").getCallsTo().print();
//		mp.singleClass("test.model.hierarchy.MyInterface").getMethods().getCallsTo().print();
		
		//TODO output in target eclipse
		MetaPojos.getConsole().println(mp.allClasses().stream().count());
	}
}
