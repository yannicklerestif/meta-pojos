package test.model;

import test.model.hierarchy.MyInterface;
import test.model.hierarchy.MyInterfaceImplChild;

public class StartingClass {
	public class SomeInnerClass {
		public void someMethodInInnerClass() {
		}
	}

	public static class SomeStaticInnerClass {
		public void someMethodInStaticInnerClass() {
			Runnable anonymousClassTest = new Runnable() {
				public void run() {
					Runnable anonymousClassTest = new Runnable() {
						public void run() {
							
						};
					};
					
				};
			};
			System.out.println(anonymousClassTest.getClass().getName());
		}

	}

	public void anotherMethod() {
		Runnable anonymousClassTest = new Runnable() {
			public void run() {
			};
		};
		System.out.println(anonymousClassTest.getClass().getName());
	}

	public void startingMethod(Integer someValue) {
		System.out.println("doing something in " + this.getClass());
		MyInterface i = new MyInterfaceImplChild();
		i.myInterfaceMethod();
	}
}
