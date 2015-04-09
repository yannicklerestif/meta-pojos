package query;

import com.yannicklerestif.metapojos.MetaPojos;

//TODO put some documentation in this class
public class MetaPojosQuery {
	public static void main(String[] args) throws Exception {
		MetaPojos mp = MetaPojos.start();
		//		mp.singleClass(ClassBean.class.getName()).getMethods().matches("<init>").getCallsTo().print();
		//		mp.singleClass("test.model.hierarchy.MyInterface").getMethods().getCallsTo().print();
//		MetaPojos.getConsole().println(mp.allClasses().stream().count());
		mp.singleClass("com.yannicklerestif.metapojos.elements.beans.MethodBean").getMethods().matches("getClassBean").getCallsTo().print();
	}
}
