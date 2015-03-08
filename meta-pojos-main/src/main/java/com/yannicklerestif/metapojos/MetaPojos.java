package com.yannicklerestif.metapojos;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.yannicklerestif.metapojos.elements.streams.ClassStream;

@Service
public class MetaPojos {

	public static final String MP_HOME = "mp.home";

	@Autowired
	private DataContainer dc;

	@Autowired
	Environment env;

	public ClassStream allClasses() {
		return dc.allClasses();
	}
	
	public ClassStream singleClass(String className) {
		return dc.singleClass(className);
	}
	
	public static MetaPojos start() throws Exception {
		System.out.println("starting...");
		long start = System.currentTimeMillis();
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		System.out.println("...started - took " + (System.currentTimeMillis() - start) + " ms");
		return context.getBean(MetaPojos.class);
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
		System.out.println("starting reading...");
		long start = System.currentTimeMillis();
		dc.readClasses(locations);
		System.out.println("...done - took " + (System.currentTimeMillis() - start) + " ms");
		//		System.in.read();
		System.out.println("-----------------------");
	}
	
	private MetaPojos() {
	}

}
