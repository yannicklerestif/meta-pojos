package com.yannicklerestif.metapojos.plugin.resources;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

import com.yannicklerestif.metapojos.plugin.MetaPojosPluginImpl;
import com.yannicklerestif.metapojos.plugin.debug.DebuggerOutput;
import com.yannicklerestif.metapojos.plugin.project.MetaPojosProjectNature;

public class MetaPojosWorkspace {

	private static final String JAVA_NATURE = "org.eclipse.jdt.core.javanature";

	private MetaPojosPluginImpl plugin;

	private DataContainerManager dcm;

	public MetaPojosWorkspace init(MetaPojosPluginImpl plugin, DataContainerManager dcm) {
		this.plugin = plugin;
		this.dcm = dcm;
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		workspace.addResourceChangeListener(new MetaPojosWorkspaceListener());
		return this;
	}

	class MetaPojosWorkspaceListener implements IResourceChangeListener {

		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			DebuggerOutput.get().debugEvent(event);
			if (IResourceChangeEvent.POST_CHANGE != event.getType())
				return;
			IResourceDelta delta = event.getDelta();
			if (delta == null || delta.getAffectedChildren() == null || delta.getAffectedChildren().length == 0) {
				//the events we're interested in will allways have a project
				return;
			}
			for (IResourceDelta projectResourceDelta : delta.getAffectedChildren()) {
				IResource resource = projectResourceDelta.getResource();
				if (!(resource.getType() == IResource.PROJECT)) {
					System.err.println("not a project !?");
					DebuggerOutput.get().debugEvent(event, true);
					dcm.setDirty();
					return;
				}
				IProject project = (IProject) resource;
				try {
					//not a java project : skipping.
					if (!(project.isNatureEnabled(JAVA_NATURE)))
						continue;
					//skipping meta pojos projects
					if (project.isNatureEnabled(MetaPojosProjectNature.META_POJOS_PLUGIN_META_POJOS_NATURE))
						continue;
					//otherwise me must reload database
					dcm.setDirty();
					return;
				} catch (CoreException e) {
					//TODO this happens when project was deleted => check it instead of catching an exception
					dcm.setDirty();
					System.err.println("Error retrieving project nature for project " + project);
					e.printStackTrace();
				}
			}

		}
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
							checkAndAdd(outputLocations, skipped, root.getFolder(project.getOutputLocation())
									.getLocation().toFile());
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
						|| (!(project.isNatureEnabled(JAVA_NATURE))))
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
