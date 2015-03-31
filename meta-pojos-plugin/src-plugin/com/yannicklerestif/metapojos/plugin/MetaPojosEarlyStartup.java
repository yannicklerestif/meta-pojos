package com.yannicklerestif.metapojos.plugin;

import org.eclipse.ui.IStartup;

public class MetaPojosEarlyStartup implements IStartup {

	@Override
	public void earlyStartup() {
		System.out.println("Early startup method called ---");
	}

}
