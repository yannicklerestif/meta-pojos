package com.yannicklerestif.metapojos;

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
		if (mpHome == null)
			mpHome = System.getProperty("user.home");
		mpHome = mpHome.replaceAll("\\\\", "/");
		if(!mpHome.endsWith("/"))
			mpHome += "/";
		mpHome += ".meta-pojos";
		System.setProperty(MP_HOME, mpHome);
		String derbyHome = mpHome + "/derby";
		System.setProperty("derby.system.home", derbyHome);
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		return context.getBean(MetaPojos.class);
	}

	public void storeClasses(String... classesJarOrDirectories) throws Exception {
		dc.storeClasses(classesJarOrDirectories);
	}
	
	private MetaPojos() {
	}

}
