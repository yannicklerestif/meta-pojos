package com.yannicklerestif.metapojos.plugin;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
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
		if(consoleHandle.metaPojosConsoleOutputStream.isClosed())
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
	
}
