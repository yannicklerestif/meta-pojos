package com.yannicklerestif.metapojos.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

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

		// only for testing !!
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
			List<IJavaProject> projects = getJavaProjects();

			for (IJavaProject project : projects) {
				IClasspathEntry[] resolvedClasspath = project.getResolvedClasspath(true);
				for (int i = 0; i < resolvedClasspath.length; i++) {
					IClasspathEntry entry = resolvedClasspath[i];
					if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
						IPath outputLocation = entry.getOutputLocation();
						if (outputLocation != null)
							//source folder has a specific output location
							checkAndAdd(outputLocations, root.getFolder(outputLocation).getLocation().toFile());
						else
							//otherwise output is project default output folder
							checkAndAdd(outputLocations, root.getFolder(project.getOutputLocation()).getLocation()
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
						getConsole().println(
								"WARNING : unsupported classpath entry in project " + project.getProject().getName()
										+ " : " + entry);
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
		if (dest.contains(file.getAbsolutePath()))
			return;
		if (!file.exists())
			throw new IllegalArgumentException("File doesn't exist : " + file);
		if (!(file.isDirectory() || file.getName().endsWith(".jar")))
			throw new IllegalArgumentException("Unsupported file type : " + file);
		dest.add(file.getAbsolutePath());
	}

	@Override
	public void output(MetaPojosHyperlinkedOutput hyperlinkableOutput) {
		for (MetaPojosOutputPart part : hyperlinkableOutput.outputParts) {
			if (part.bean == null)
				console.print(part.text);
			else {
				MetaPojosConsoleHyperlink link = new MetaPojosConsoleHyperlink(part.bean);
				console.printHyperLink(part.text, link);
			}
		}
		console.println();
	}

	//------------------------------------------------------------------------------------------------

	protected static List<IJavaProject> getJavaProjects() {
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

	// only for testing ! -------------------------------------------------------

	protected void createTestThread() {
		//only for tests !
		if(true) return;
		Thread test = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						test();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		test.start();
	}

	protected void test() throws Exception {
		System.in.read();
		System.out.println("input read -------------------------------");
		eclipsePrint("java.util.ArrayList");
	}

	private void eclipsePrint(String string) throws JavaModelException {
		List<IJavaProject> javaProjects = getJavaProjects();
		IType primaryType = null;
		for (IJavaProject project : javaProjects) {
//			primaryType = project.findType("test.model.StartingClass");
			//			primaryType = project.findType("test.model.SomeParameterizedClass");
			//			primaryType = project.findType("test.model.SomeClass");
			//			primaryType = project.findType("java.lang.Object");
			primaryType = project.findType("java.util.ArrayList");
//			primaryType = project.findType("java.awt.EventQueue$1AWTInvocationLock");
			//			primaryType = project.findType("com.yannicklerestif.metapojos.MetaPojos");
			if (primaryType != null)
				break;
		}
		if (primaryType == null)
			return;
		print("",primaryType);
	}

	void print(String prefix, IJavaElement element) throws JavaModelException {
       	System.out.println(prefix + element.getElementName() + (element instanceof IMethod ? "()" : ""));
        if (element instanceof IParent) {
            for (IJavaElement child: ((IParent)element).getChildren()) {
                print(prefix + "\t", child);
            }
        }
	}

}
