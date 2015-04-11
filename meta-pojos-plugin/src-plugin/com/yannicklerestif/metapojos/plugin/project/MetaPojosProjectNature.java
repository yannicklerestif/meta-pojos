package com.yannicklerestif.metapojos.plugin.project;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectNature;
import org.eclipse.core.runtime.CoreException;

public class MetaPojosProjectNature implements IProjectNature {

	private IProject project;
	public static final String META_POJOS_PLUGIN_META_POJOS_NATURE = "meta-pojos-plugin.metaPojosNature";

	public void configure() throws CoreException {
	}

	public void deconfigure() throws CoreException {
		// Remove the nature-specific information here.
	}

	public IProject getProject() {
		return project;
	}

	public void setProject(IProject value) {
		project = value;
	}

}
