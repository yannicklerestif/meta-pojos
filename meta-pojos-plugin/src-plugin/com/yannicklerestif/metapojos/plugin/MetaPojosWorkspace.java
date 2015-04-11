package com.yannicklerestif.metapojos.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.yannicklerestif.metapojos.plugin.project.MetaPojosProjectNature;

public class MetaPojosWorkspace {

	private MetaPojosPluginImpl plugin = null;
	
	public MetaPojosWorkspace(MetaPojosPluginImpl plugin) {
		this.plugin = plugin;
	}

	public MetaPojosWorkspace init() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IResourceChangeListener listener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				System.out.println(event);
			}
		};
		workspace.addResourceChangeListener(listener);
		return this;
	}
	
	public String[] getClassesLocations() {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			Set<String> outputLocations = new HashSet<>();
			Set<String> skipped = new HashSet<>();
			Set<String> librairiesLocations = new HashSet<>();
			List<IJavaProject> projects = getJavaProjects();

			for (IJavaProject project : projects) {
				IClasspathEntry[] resolvedClasspath = project.getResolvedClasspath(true);
				for (int i = 0; i < resolvedClasspath.length; i++) {
					IClasspathEntry entry = resolvedClasspath[i];
					if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						IPath outputLocation = entry.getOutputLocation();
						if (outputLocation != null)
							//source folder has a specific output location
							checkAndAdd(outputLocations, skipped, root.getFolder(outputLocation).getLocation().toFile());
						else
							//otherwise output is project default output folder
							checkAndAdd(outputLocations, skipped, root.getFolder(project.getOutputLocation()).getLocation()
									.toFile());
					} else if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
						IFile file = root.getFile(entry.getPath());
						if (file.exists())
							//location is in the workspace
							checkAndAdd(librairiesLocations, skipped, file.getLocation().toFile());
						else
							//location is not in the workspace => should be an external library
							checkAndAdd(librairiesLocations, skipped, entry.getPath().toFile());
					} else if (entry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
						//TODO right now it is not necessary to recurse into project, but it might be necessary in the future.
						//The reason it is not necessary right now is that we analyse all projects anyway, so the project dependencies *will*
						//be analyzed anyway. 
					} else {
						plugin.getConsole().println(
								"WARNING : unsupported classpath entry in project " + project.getProject().getName()
										+ " : " + entry);
					}

				}
			}
			
			//TODO return skipped instead of printing it ?
			List<String> skippedSorted = new ArrayList<>();
			skippedSorted.addAll(skipped);
			Collections.sort(skippedSorted);
			for (String string : skippedSorted) {
				plugin.getConsole().println("WARN : location skipped : " + string);
			}
			
			List<String> result = new ArrayList<>();
			result.addAll(outputLocations);
			result.addAll(librairiesLocations);
			return result.toArray(new String[0]);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void checkAndAdd(Set<String> dest, Set<String> skipped, File file) {
		if (dest.contains(file.getAbsolutePath()))
			return;
		if (!file.exists()) {
			skipped.add("File doesn't exist : " + file);
			return;
		}
		if (!(file.isDirectory() || file.getName().endsWith(".jar"))) {
			skipped.add("Unsupported file type : " + file);
			return;
		}
		dest.add(file.getAbsolutePath());
	}

	//------------------------------------------------------------------------------------------------

	public List<IJavaProject> getJavaProjects() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		List<IJavaProject> javaProjects = new ArrayList<>();
		IProject[] projects = root.getProjects();
		for (IProject project : projects) {
			if (!project.isOpen())
				continue;
			// check if we have a Java project
			try {
				if (project.isNatureEnabled(MetaPojosProjectNature.META_POJOS_PLUGIN_META_POJOS_NATURE)
						|| (!(project.isNatureEnabled("org.eclipse.jdt.core.javanature"))))
					continue;
			} catch (CoreException e) {
				System.err.println("Couldn't retrieve nature for project : " + project.getName());
				e.printStackTrace();
				continue;
			}
			javaProjects.add(JavaCore.create(project));
		}
		return javaProjects;
	}


}
