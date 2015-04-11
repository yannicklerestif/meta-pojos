package com.yannicklerestif.metapojos;

import com.yannicklerestif.metapojos.model.elements.streams.ClassStream;
import com.yannicklerestif.metapojos.plugin.MetaPojosConsole;
import com.yannicklerestif.metapojos.plugin.PluginAccessor;

public class MetaPojos {

	//FIXME documentation
	//FIXME do not reload database if nothing has happened since last query
	//FIXME tycho
	//TODO plugin dependencies 

	public static ClassStream getAllClasses() {
		return PluginAccessor.getPlugin().getDataContainer().getAllClasses();
	}
	
	public static ClassStream getSingleClass(String className) {
		return PluginAccessor.getPlugin().getDataContainer().getSingleClass(className);
	}
	
	public static MetaPojosConsole getConsole() {
		return PluginAccessor.getPlugin().getConsole();
	}

}
