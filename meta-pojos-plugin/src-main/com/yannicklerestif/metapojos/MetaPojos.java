package com.yannicklerestif.metapojos;

import com.yannicklerestif.metapojos.model.elements.streams.ClassStream;
import com.yannicklerestif.metapojos.plugin.MetaPojosConsole;
import com.yannicklerestif.metapojos.plugin.PluginAccessor;

public class MetaPojos {

	public static ClassStream getClasses() {
		return PluginAccessor.getPlugin().getDataContainer().getAllClasses();
	}
	
	public static ClassStream getClasses(String className) {
		return PluginAccessor.getPlugin().getDataContainer().getAllClasses().filterByName(className);
	}
	
	public static MetaPojosConsole getConsole() {
		return PluginAccessor.getPlugin().getConsole();
	}

}
