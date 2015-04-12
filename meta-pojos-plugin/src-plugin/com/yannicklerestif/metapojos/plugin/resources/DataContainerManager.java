package com.yannicklerestif.metapojos.plugin.resources;

import com.yannicklerestif.metapojos.model.DataContainer;

public class DataContainerManager {
	private DataContainer dc;

	private MetaPojosWorkspace workspace;

	private boolean dirty = true;
	
	public DataContainerManager(MetaPojosWorkspace workspace) {
		this.workspace = workspace;
	}

	public DataContainer getDataContainer() {
		return dc;
	}

	public void prepareDataContainer() throws Exception {
		if(dirty == false)
			return;
		dc = new DataContainer();
		String[] classesLocations = workspace.getClassesLocations();
		dc.readClasses(classesLocations);
		dirty = false;
	}

	public void setDirty() {
		this.dirty  = true;
	}

}
