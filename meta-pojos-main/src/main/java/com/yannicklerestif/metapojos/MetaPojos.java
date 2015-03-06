package com.yannicklerestif.metapojos;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class MetaPojos {

	public static final String MP_HOME = "mp.home";
	
	@Autowired
	private DataContainer dc;
	
	@Autowired
	Environment env;
	
	public static MetaPojos start(String mpHome) throws Exception {
		System.out.println("starting...");
		long start = System.currentTimeMillis();
		if (mpHome == null)
			mpHome = System.getProperty("user.home");
		mpHome = mpHome.replaceAll("\\\\", "/");
		if(!mpHome.endsWith("/"))
			mpHome += "/";
		mpHome += ".meta-pojos";
		System.setProperty(MP_HOME, mpHome);
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		System.out.println("...started - took "+(System.currentTimeMillis() - start) + " ms");
		return context.getBean(MetaPojos.class);
	}

	public void storeClasses(String... classesJarOrDirectories) throws Exception {
		System.out.println("starting storing...");
		long start = System.currentTimeMillis();
		dc.storeClasses(classesJarOrDirectories);
		System.out.println("...done - took "+(System.currentTimeMillis() - start) + " ms");
	}
	
	private MetaPojos() {
	}

}
