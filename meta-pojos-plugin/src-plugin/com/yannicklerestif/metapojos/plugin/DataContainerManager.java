package com.yannicklerestif.metapojos.plugin;

import com.yannicklerestif.metapojos.model.DataContainer;

public class DataContainerManager {
	private DataContainer dc;

	private MetaPojosWorkspace workspace;
	
	public DataContainerManager(MetaPojosWorkspace workspace) {
		this.workspace = workspace;
	}

	public DataContainer getDataContainer() {
		return dc;
	}

	public void prepareDataContainer() throws Exception {
		dc = new DataContainer();
		String[] classesLocations = workspace.getClassesLocations();
		dc.readClasses(classesLocations);
	}

}
