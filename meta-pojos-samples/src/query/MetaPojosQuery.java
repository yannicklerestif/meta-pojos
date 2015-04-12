package query;

import com.yannicklerestif.metapojos.MetaPojos;

//TODO put some documentation in this class
public class MetaPojosQuery {
	public static void main(String[] args) throws Exception {
		MetaPojos.getConsole().println("Classes analyzed : " + MetaPojos.getAllClasses().stream().count());
		MetaPojos.getConsole().println("----------------------");
		MetaPojos.getClassesByName("java.util.ArrayList").getMethodsByName("add").print();
		MetaPojos.getConsole().println("----------------------");
		MetaPojos.getClassesByName("beans.ClassBean").getMethods().getCallsTo().print();
		MetaPojos.getConsole().println("----------------------");
		MetaPojos.getClassesByName("com.yannicklerestif.metapojos.elements").print();
	}
}
