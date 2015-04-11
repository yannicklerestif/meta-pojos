package com.yannicklerestif.metapojos.plugin;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.yannicklerestif.metapojos.model.DataContainer;
import com.yannicklerestif.metapojos.plugin.console.MetaPojosConsoleImpl;
import com.yannicklerestif.metapojos.plugin.debug.DebuggerOutput;

/**
 * The activator class controls the plug-in life cycle
 */
public class MetaPojosPluginImpl extends AbstractUIPlugin implements MetaPojosPlugin {

	//FIXME only in dev !
	public static final boolean DEBUG_MODE = true;
	
	// The plug-in ID
	public static final String PLUGIN_ID = "meta-pojos-plugin"; //$NON-NLS-1$

	// The shared instance
	private static MetaPojosPluginImpl plugin;

	private MetaPojosConsoleImpl console;

	private DataContainerManager dcm;

	private MetaPojosWorkspace workspace;
	
	public MetaPojosWorkspace getWorkspace() {
		return workspace;
	}

	/**
	 * The constructor
	 */
	public MetaPojosPluginImpl() {
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		PluginAccessor.setPlugin(this);
		console = MetaPojosConsoleImpl.createConsole(this);
		workspace = new MetaPojosWorkspace(this).init();
		dcm = new DataContainerManager(workspace);
		
		// only for testing !!
		if(DEBUG_MODE)
			new DebuggerOutput(this).createTestThread();
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
	public static MetaPojosPluginImpl getDefault() {
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

	public void prepareDataContainer() throws Exception {
		dcm.prepareDataContainer();
	}
	
	//------------------------ MetaPojosPlugin interface -------------------------------

	@Override
	public MetaPojosConsole getConsole() {
		return console;
	}

	@Override
	public DataContainer getDataContainer() {
		return dcm.getDataContainer();
	}


}
