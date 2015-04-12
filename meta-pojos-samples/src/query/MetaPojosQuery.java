package query;

import com.yannicklerestif.metapojos.MetaPojos;

//TODO put some documentation in this class
public class MetaPojosQuery {
	public static void main(String[] args) throws Exception {
		MetaPojos.getConsole().println("Classes analyzed : " + MetaPojos.getClasses().stream().count());
		MetaPojos.getConsole().println("----------------------");
		MetaPojos.getClasses("java.util.ArrayList").getMethods("add").print();
		MetaPojos.getConsole().println("----------------------");
		MetaPojos.getClasses("beans.ClassBean").getMethods().getCallsTo().print();
		MetaPojos.getConsole().println("----------------------");
		MetaPojos.getClasses("com.yannicklerestif.metapojos.elements").print();
	}
}
