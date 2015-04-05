package com.yannicklerestif.metapojos.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
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
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.ui.console.FileLink;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.yannicklerestif.metapojos.plugin.MetaPojosConsole.MetaPojosHyperLink;
import com.yannicklerestif.metapojos.plugin.MetaPojosHyperlinkedOutput.MetaPojosOutputPart;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin implements MetaPojosPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "meta-pojos-plugin"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	private MetaPojosConsole console = null;
	
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
		PluginAccessor.setPlugin(this);
		console = MetaPojosConsole.createConsole(); 
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IResourceChangeListener listener = new IResourceChangeListener() {
			public void resourceChanged(IResourceChangeEvent event) {
				System.out.println(event);
			}
		};
		workspace.addResourceChangeListener(listener);
		
		//FIXME only for testing !!
		createTestThread();
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		console.closeIfNecessary();
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
		return console;
	}

	@Override
	public String[] getClassesLocations() {
		try {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceRoot root = workspace.getRoot();
			Set<String> outputLocations = new HashSet<>();
			Set<String> librairiesLocations = new HashSet<>();

			IProject[] projects = root.getProjects();
			for (IProject project : projects) {
				// check if we have a Java project
				if (project.isNatureEnabled(MetaPojosProjectNature.META_POJOS_PLUGIN_META_POJOS_NATURE)
						|| (!(project.isNatureEnabled("org.eclipse.jdt.core.javanature"))))
					continue;
				IJavaProject javaProject = JavaCore.create(project);
				IClasspathEntry[] resolvedClasspath = javaProject.getResolvedClasspath(true);
				for (int i = 0; i < resolvedClasspath.length; i++) {
					IClasspathEntry entry = resolvedClasspath[i];
					if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						IPath outputLocation = entry.getOutputLocation();
						if (outputLocation != null)
							//source folder has a specific output location
							checkAndAdd(outputLocations, root.getFolder(outputLocation).getLocation().toFile());
						else
							//otherwise output is project default output folder
							checkAndAdd(outputLocations, root.getFolder(javaProject.getOutputLocation()).getLocation()
									.toFile());
					} else if (entry.getEntryKind() == IClasspathEntry.CPE_LIBRARY) {
						IFile file = root.getFile(entry.getPath());
						if (file.exists())
							//location is in the workspace
							checkAndAdd(librairiesLocations, file.getLocation().toFile());
						else
							//location is not in the workspace => should be an external library
							checkAndAdd(librairiesLocations, entry.getPath().toFile());
					} else if (entry.getEntryKind() == IClasspathEntry.CPE_PROJECT) {
						//TODO right now it is not necessary to recurse into project, but it might be necessary in the future.
						//The reason it is not necessary right now is that we analyse all projects anyway, so the project dependencies *will*
						//be analyzed anyway. 
					} else {
						getConsole()
								.println(
										"WARNING : unsupported classpath entry in project " + project.getName() + " : "
												+ entry);
					}

				}
			}
			List<String> result = new ArrayList<>();
			result.addAll(outputLocations);
			result.addAll(librairiesLocations);
			return result.toArray(new String[0]);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void checkAndAdd(Set<String> dest, File file) {
		if(dest.contains(file.getAbsolutePath()))
			return;
		if(!file.exists())
			throw new IllegalArgumentException("File doesn't exist : " + file);
		if(!(file.isDirectory() || file.getName().endsWith(".jar")))
			throw new IllegalArgumentException("Unsupported file type : " + file);
		dest.add(file.getAbsolutePath());
	}
	
	@Override
	public void output(MetaPojosHyperlinkedOutput hyperlinkableOutput) {
		for(MetaPojosOutputPart part : hyperlinkableOutput.outputParts) {
			if(part.bean == null)
				console.print(part.text);
			else {
				MetaPojosConsoleHyperlink link = new MetaPojosConsoleHyperlink(part.bean); 
				console.printHyperLink(part.text, link);
			}
		}
		console.println();
	}

	//FIXME only for testing ! -------------------------------------------------------
	
	protected void createTestThread() {
		Thread test = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					try {
						test();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		test.start();
	}
	
	protected void test() throws Exception {
		System.in.read();
		System.out.println("input read ---------------------------");
		IPath path = Path.fromOSString("/home/yannick/runtime-EclipseApplication/eee/src/query/MetaPojosQuery.java");
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFileForLocation(path);
		FileLink fileLink = new FileLink(file, null, -1, -1, -1);
		console.print("some text ");
		console.print("**");
		console.printHyperLink(new Date() + " continued" , fileLink);
		console.println(" some more text");
	}

}
