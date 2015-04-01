package com.yannicklerestif.metapojos.plugin;

import java.io.File;

import org.eclipse.core.resources.IFolder;
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
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements MetaPojosPlugin {

	public static final String META_POJOS_CONSOLE_NAME = "Meta Pojos";
	// The plug-in ID
	public static final String PLUGIN_ID = "meta-pojos-plugin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private static class ConsoleHandle implements Console {

		public MessageConsoleStream metaPojosConsoleOutputStream = null;

		public ConsoleHandle(MessageConsoleStream metaPojosConsoleOutputStream) {
			this.metaPojosConsoleOutputStream = metaPojosConsoleOutputStream;
		}

		public void println(Object message) {
			metaPojosConsoleOutputStream.println(message == null ? "null" : message.toString());
		}

		public void println() {
			metaPojosConsoleOutputStream.println();
		}

		public void print(Object message) {
			metaPojosConsoleOutputStream.print(message == null ? "null" : message.toString());
		}

	}

	private ConsoleHandle consoleHandle;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("Plugin startup method called ---");
		super.start(context);
		plugin = this;
		createConsole();
		PluginAccessor.setPlugin(this);
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IResourceChangeListener listener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				System.out.println("Something changed!");
			}
		};
		workspace.addResourceChangeListener(listener);
	}

	private void createConsole() {
		ConsolePlugin consolePlugin = ConsolePlugin.getDefault();
		IConsoleManager conMan = consolePlugin.getConsoleManager();
		MessageConsole myConsole = new MessageConsole(META_POJOS_CONSOLE_NAME, null);
		conMan.addConsoles(new IConsole[] { myConsole });
		MessageConsoleStream metaPojosConsoleOutputStream = myConsole.newMessageStream();
		metaPojosConsoleOutputStream.setActivateOnWrite(true);
		this.consoleHandle = new ConsoleHandle(metaPojosConsoleOutputStream);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		if (consoleHandle.metaPojosConsoleOutputStream.isClosed())
			System.out.println("console stream is already closed.");
		else
			consoleHandle.metaPojosConsoleOutputStream.close();
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 *
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	//------------------------ MetaPojosPlugin interface -------------------------------

	@Override
	public Console getConsole() {
		return consoleHandle;
	}

	@Override
	public File[] getClassesLocations() {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IWorkspaceRoot root = workspace.getRoot();
		IProject[] projects = root.getProjects();
		for (IProject project : projects) {
			try {
				System.out.println("Working in project " + project.getName() + " ---------------");
				// check if we have a Java project
				if (project.isNatureEnabled("org.eclipse.jdt.core.javanature")) {
					IJavaProject javaProject = JavaCore.create(project);
					IClasspathEntry[] resolvedClasspath = javaProject.getResolvedClasspath(true);
					for (int i = 0; i < resolvedClasspath.length; i++) {
						IClasspathEntry entry = resolvedClasspath[i];
						String kind = "";
						switch ((entry.getEntryKind())) {
						case IClasspathEntry.CPE_SOURCE:
							kind = "CPE_SOURCE";
							break;
						case IClasspathEntry.CPE_CONTAINER:
							kind = "CPE_CONTAINER";
							break;
						case IClasspathEntry.CPE_LIBRARY:
							kind = "CPE_LIBRARY";
							break;
						case IClasspathEntry.CPE_PROJECT:
							kind = "CPE_PROJECT";
							break;
						case IClasspathEntry.CPE_VARIABLE:
							kind = "CPE_VARIABLE";
							break;
						default:
							break;
						}
						System.out.println(kind + " => " + entry.getPath());
//						IPath path = project.getOutputLocation();
//						IFolder folder = root.getFolder(path);
//						File outputLocation = folder.getLocation().toFile();
//						System.out.println(entry);
					}
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		return new File[] {};
	}

}
