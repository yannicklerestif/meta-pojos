package com.yannicklerestif.metapojos;

import com.yannicklerestif.metapojos.elements.streams.ClassStream;

public class MetaPojos {

	public static final String MP_HOME = "mp.home";

	private DataContainer dc;

	public ClassStream allClasses() {
		return dc.allClasses();
	}
	
	public ClassStream singleClass(String className) {
		return dc.singleClass(className);
	}
	
	public static MetaPojos start() throws Exception {
		//TODO output in target eclipse
		System.out.println("starting...");
		long start = System.currentTimeMillis();
		MetaPojos mp = new MetaPojos();
		mp.dc = new DataContainer();
		//TODO output in target eclipse
		System.out.println("...started - took " + (System.currentTimeMillis() - start) + " ms");
		return mp;
	}

	/**
	 * Loads database with classes in specified locations.<br>
	 * Locations are names of files or directories, and can be :<br>
	 * - A full path to a class directory<br>
	 * - A full path to a jar file<br> 
	 * - A full path to a file containing locations
	 * @param locations
	 * @throws Exception
	 */
	public void readClasses(String... locations) throws Exception {
		//TODO output in target eclipse
		System.out.println("starting reading...");
		long start = System.currentTimeMillis();
		dc.readClasses(locations);
		//TODO output in target eclipse
		System.out.println("...done - took " + (System.currentTimeMillis() - start) + " ms");
		//		System.in.read();
		//TODO output in target eclipse
		System.out.println("-----------------------");
	}
	
	private MetaPojos() {
	}

}
