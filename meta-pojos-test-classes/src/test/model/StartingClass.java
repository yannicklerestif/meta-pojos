package test.model;

import test.model.hierarchy.MyInterface;
import test.model.hierarchy.MyInterfaceImplChild;

public class StartingClass {
	public void startingMethod(Integer someValue) {
		System.out.println("doing something in " + this.getClass());
		MyInterface i = new MyInterfaceImplChild();
		i.myInterfaceMethod();
	}
}
