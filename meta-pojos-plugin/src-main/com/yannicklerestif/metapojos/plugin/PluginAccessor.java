package com.yannicklerestif.metapojos.plugin;


public class PluginAccessor {
	private static MetaPojosPlugin plugin = null;

	public static MetaPojosPlugin getPlugin() {
		return plugin;
	}

	public static void setPlugin(MetaPojosPlugin plugin) {
		PluginAccessor.plugin = plugin;
	}
	
}
