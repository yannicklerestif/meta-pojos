package query;

import com.yannicklerestif.metapojos.MetaPojos;
import com.yannicklerestif.metapojos.model.elements.beans.ClassBean;

//TODO put some documentation in this class
public class MetaPojosQuery {
	public static void main(String[] args) throws Exception {
//		MetaPojos.getSingleClass(ClassBean.class.getName()).getMethods().matches("<init>").getCallsTo().print();
//		MetaPojos.getSingleClass("test.model.hierarchy.MyInterface").getMethods().getCallsTo().print();
//		MetaPojos.getSingleClass("com.yannicklerestif.metapojos.elements.beans.ClassBean").getMethods().matches("toString").getCallsTo().print();
		MetaPojos.getConsole().println(MetaPojos.getAllClasses().stream().count());
	}
}
