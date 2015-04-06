package test.model;

import java.util.Collection;
import java.util.List;

public class SomeParameterizedClass<T> {
	public void doSomething(T someParameter) {
		Object object = new Object();
		boolean equals = object.equals(this);
	}
	
	public <E> void doSomethingElse(T someParameter, E b) {
		
	}
	
	public void doSomething() {
		Object object = new Object();
		boolean equals = object.equals(this);
	}
	
	public void someMethod(test.model.StartingClass a, StartingClass b, StartingClass.SomeStaticInnerClass c, test.model.StartingClass.SomeStaticInnerClass d) {
	}
	
	public void someMethod2(Collection<String> test) {
	}
	
	public void someOtherMethod(String[] strings, int test, float f, int[][] ints, String[][] strings_, List... lists) {
	}

}
