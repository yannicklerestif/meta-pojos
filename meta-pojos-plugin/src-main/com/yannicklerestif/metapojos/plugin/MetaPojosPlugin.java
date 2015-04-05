package com.yannicklerestif.metapojos.plugin;


public interface MetaPojosPlugin {

	public Console getConsole();

	public String[] getClassesLocations();

	public void output(MetaPojosHyperlinkedOutput hyperlinkableOutput);

}
