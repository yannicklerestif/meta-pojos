package com.yannicklerestif.metapojos.plugin;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.model.LaunchConfigurationDelegate;

public class MetaPojosLaunchConfigurationDelegate extends LaunchConfigurationDelegate {

	public static final String LAUNCHER_ID = "com.yannicklerestif.metapojos.Launcher";
	
	@Override
	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {
		System.out.println();
		System.out.println("Launching -------------------------------");
		for(String key : configuration.getAttributes().keySet()) {
			Object value = configuration.getAttributes().get(key);
			System.out.println(key + " => " + value);
		}
		System.out.println("---------------------");
		
		Class clazz = null;
		
        //TODO replace hard-coded strings by values from config
		try {
            URL url = new File("C:/eclipse_workspaces/runtime-EclipseApplication/TestProject/bin").toURI().toURL();
			ClassLoader cl = new URLClassLoader(new URL[]{url}, null);
			clazz = cl.loadClass("com.yannick.MyOtherClass");
			
			//TODO passing args to the methods might have some interest 
			Method mainMethod = clazz.getMethod("main", String[].class);
			mainMethod.invoke(null, new Object[]{null});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

}
