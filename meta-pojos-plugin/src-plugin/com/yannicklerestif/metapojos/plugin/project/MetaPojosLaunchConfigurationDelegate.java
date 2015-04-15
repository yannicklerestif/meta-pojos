package com.yannicklerestif.metapojos.plugin.project;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import com.yannicklerestif.metapojos.plugin.MetaPojosPluginImpl;
import com.yannicklerestif.metapojos.plugin.PluginAccessor;

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
		MetaPojosPluginImpl plugin = (MetaPojosPluginImpl) PluginAccessor.getPlugin();

		try {

			plugin.getConsole().clear();
			plugin.getConsole().println("starting...");
			long start = System.currentTimeMillis();

			IStatus status = plugin.prepareDataContainer();

			if (status.getSeverity() == IStatus.ERROR)
				throw ((Exception) status.getException());

			if (status.getSeverity() == IStatus.CANCEL) {
				plugin.getConsole().println("...canceled after " + (System.currentTimeMillis() - start) + " ms");
				plugin.getConsole().println("-----------------------");
			} else if (status.isOK()) {

				plugin.getConsole().println("...started - took " + (System.currentTimeMillis() - start) + " ms");
				plugin.getConsole().println("-----------------------");

				URL url = outputLocation.toURI().toURL();
				cl = new URLClassLoader(new URL[] { url }, Thread.currentThread().getContextClassLoader());
				clazz = cl.loadClass(mainType);
				Method mainMethod = clazz.getMethod("main", String[].class);
				mainMethod.invoke(null, new Object[] { null });
				cl.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Throwable toPrint = e;
			if (e instanceof InvocationTargetException)
				toPrint = e.getCause();
			StringWriter errors = new StringWriter();
			toPrint.printStackTrace(new PrintWriter(errors));
			plugin.getConsole().println("Error launching query :");
			plugin.getConsole().println(errors.toString());
		} finally {
			try {
				if (cl != null)
					cl.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			DebugPlugin.getDefault().getLaunchManager().removeLaunch(launch);
		}
	}
}
