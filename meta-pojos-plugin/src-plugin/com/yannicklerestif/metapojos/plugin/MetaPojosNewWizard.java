package com.yannicklerestif.metapojos.plugin;

import java.io.InputStream;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.wizards.JavaProjectWizard;

//TODO when project is created, open editor for Query sample, preferably in the lower part of the workbench where search views are
public class MetaPojosNewWizard extends JavaProjectWizard {
	@Override
	public boolean performFinish() {
		boolean result = super.performFinish();
		IJavaProject project = (IJavaProject) getCreatedElement();
		try {
			//adding meta-pojo nature to the project
			IProjectDescription description = project.getProject().getDescription();
			String[] natures = description.getNatureIds();
			String[] newNatures = new String[natures.length + 1];
			System.arraycopy(natures, 0, newNatures, 1, natures.length);
			newNatures[0] = MetaPojosProjectNature.META_POJOS_PLUGIN_META_POJOS_NATURE;
			description.setNatureIds(newNatures);
			project.getProject().setDescription(description, null);

			//copying the sample query java class from plugin resources to project
			IFolder sourceFolder = null;
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			IClasspathEntry[] entries = project.getResolvedClasspath(true);
			for (int i = 0; i < entries.length; i++) {
				IClasspathEntry entry = entries[i];
				if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					IPath path = entry.getPath();
					sourceFolder = root.getFolder(path);
					break;
				}
			}
			if (sourceFolder == null)
				throw new IllegalStateException("Couldn't find a source folder");
			IFolder queryFolder = sourceFolder.getFolder("query");
			queryFolder.create(IResource.NONE, true, null);
			IFile file = queryFolder.getFile("MetaPojosQuery.java");
			URL url = new URL("platform:/plugin/meta-pojos-plugin/dist/MetaPojosQuery.java");
			InputStream inputStream = url.openConnection().getInputStream();
			file.create(inputStream, IResource.NONE, null);
			inputStream.close();

			//copying api from plugin resources to the project
			IFolder folder = project.getProject().getFolder("lib");
			IFile jarFile = folder.getFile("meta-pojos-api.jar");
			if (!folder.exists())
				folder.create(IResource.NONE, true, null);
			url = new URL("platform:/plugin/meta-pojos-plugin/dist/meta-pojos-api.jar");
			inputStream = url.openConnection().getInputStream();
			jarFile.create(inputStream, IResource.NONE, null);
			inputStream.close();

			//adding the api to the classpath of the project
			IClasspathEntry[] rawClasspath = project.getRawClasspath();
			IClasspathEntry newLibraryEntry = JavaCore.newLibraryEntry(jarFile.getFullPath(), null, null);
			IClasspathEntry[] newRawClassPath = new IClasspathEntry[rawClasspath.length + 1];
			System.arraycopy(rawClasspath, 0, newRawClassPath, 0, rawClasspath.length);
			newRawClassPath[rawClasspath.length] = newLibraryEntry;
			project.setRawClasspath(newRawClassPath, null);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return result;
	}

}
