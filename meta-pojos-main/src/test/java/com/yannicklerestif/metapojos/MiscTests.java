package com.yannicklerestif.metapojos;


public class MiscTests {

	public static void main(String[] args) {
		String desc = "([BLjava/io/InputStream;I)V";
		int end = desc.indexOf(')');
		System.out.println(desc.substring(1,end));
		System.out.println(desc.substring(end + 1));
		System.out.println("java.lang.Object.foo(Object.java:-2)");
		System.out.println("blablabla java.lang(Object.java:123)");
	}

}
