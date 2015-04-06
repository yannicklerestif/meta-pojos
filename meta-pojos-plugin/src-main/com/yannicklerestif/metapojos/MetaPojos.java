package com.yannicklerestif.metapojos;

import com.yannicklerestif.metapojos.elements.streams.ClassStream;
import com.yannicklerestif.metapojos.plugin.Console;
import com.yannicklerestif.metapojos.plugin.PluginAccessor;

public class MetaPojos {

	//FIXME documentation
	//FIXME reorganize classes
	//FIXME do not reload database if nothing has happened since last query
	
	//TODO Data container must be moved to plugin 
	private DataContainer dc;

	public ClassStream allClasses() {
		return dc.allClasses();
	}
	
	public ClassStream singleClass(String className) {
		return dc.singleClass(className);
	}
	
	public static MetaPojos start() throws Exception {
		PluginAccessor.getPlugin().getConsole().clear();
		PluginAccessor.getPlugin().getConsole().println("starting...");
		long start = System.currentTimeMillis();
		MetaPojos mp = new MetaPojos();
		mp.dc = new DataContainer();
		PluginAccessor.getPlugin().getConsole().println("...started - took " + (System.currentTimeMillis() - start) + " ms");
		mp.readClasses(PluginAccessor.getPlugin().getClassesLocations());
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
	private void readClasses(String... locations) throws Exception {
		PluginAccessor.getPlugin().getConsole().println("starting reading...");
		long start = System.currentTimeMillis();
		dc.readClasses(locations);
		PluginAccessor.getPlugin().getConsole().println("...done - took " + (System.currentTimeMillis() - start) + " ms");
		PluginAccessor.getPlugin().getConsole().println("-----------------------");
	}
	
	private MetaPojos() {
	}
	
	public static Console getConsole() {
		return PluginAccessor.getPlugin().getConsole();
	}

}
