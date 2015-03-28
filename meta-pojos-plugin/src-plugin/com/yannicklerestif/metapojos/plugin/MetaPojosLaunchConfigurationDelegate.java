package com.yannicklerestif.metapojos.plugin;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

public class MetaPojosLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	public static final String LAUNCHER_ID = "com.yannicklerestif.metapojos.Launcher";

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		String mainType = (String) configuration.getAttributes().get(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME);
		String projectName = (String) configuration.getAttributes().get(
				IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME);
		IJavaProject project = JavaCore.create(ResourcesPlugin.getWorkspace().getRoot().getProject(projectName));
		
		IPath path = project.getOutputLocation();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFolder folder = root.getFolder(path);
		File outputLocation = folder.getLocation().toFile();
		
		Class clazz = null;
		URLClassLoader cl = null;

		try {
			URL url = outputLocation.toURI().toURL();
			cl = new URLClassLoader(new URL[] { url }, Thread.currentThread().getContextClassLoader());
			clazz = cl.loadClass(mainType);
			Method mainMethod = clazz.getMethod("main", String[].class);
			mainMethod.invoke(null, new Object[] { null });
			cl.close();
		} catch (Exception e) {
			try {
				cl.close();
			} catch (IOException e1) {
				//we do not want to hide the original exception
				e1.printStackTrace();
			}
			throw new RuntimeException(e);
		}

	}

}
