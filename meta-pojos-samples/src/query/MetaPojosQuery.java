package query;

import com.yannicklerestif.metapojos.MetaPojos;

public class MetaPojosQuery {
	public static void main(String[] args) throws Exception {
		MetaPojos mp = MetaPojos.start();
		//		mp.singleClass(ClassBean.class.getName()).getMethods().matches("<init>").getCallsTo().print();
		//		mp.singleClass("test.model.hierarchy.MyInterface").getMethods().getCallsTo().print();
		MetaPojos.getConsole().println(mp.allClasses().stream().count());
	}
}
