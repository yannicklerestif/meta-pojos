package com.yannicklerestif.metapojos.model;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.objectweb.asm.ClassReader;

import com.yannicklerestif.metapojos.model.elements.beans.ClassBean;
import com.yannicklerestif.metapojos.model.elements.beans.MethodBean;
import com.yannicklerestif.metapojos.model.elements.beans.MethodBean.MethodBeanKey;
import com.yannicklerestif.metapojos.model.elements.streams.ClassStream;

public class DataContainer {

	public boolean working = false;

	private Map<String, ClassBean> classes = new HashMap<String, ClassBean>();

	// ------------------------------------------------------------------------------------
	// container
	// ------------------------------------------------------------------------------------

	public ClassStream getAllClasses() {
		return new ClassStream(classes.values().stream());
	}

	public static String classShortName(String className) {
		return className.substring(className.lastIndexOf('/') + 1);
	}

	String[] splitDesc(String desc) {
		int end = desc.indexOf(')');
		return new String[] { desc.substring(1, end), desc.substring(end + 1) };
	}

	MethodBean getOrCreateMethodBean(String className, String methodName, String desc) {
		ClassBean classBean = getOrCreateClassBean(className);
		MethodBeanKey key = new MethodBeanKey(methodName, desc);
		MethodBean methodBean = classBean.getMethods().get(key);
		if (methodBean == null) {
			methodBean = new MethodBean();
			methodBean.setClassBean(classBean);
			methodBean.setName(methodName);
			methodBean.setDesc(desc);
			classBean.getMethods().put(key, methodBean);
		}
		return methodBean;
	}

	ClassBean getOrCreateClassBean(String name) {
		ClassBean classBean = classes.get(name);
		if (classBean == null) {
			classBean = new ClassBean();
			classBean.setInternalName(name);
			classes.put(name, classBean);
		}
		return classBean;
	}

	// ------------------------------------------------------------------------------------
	// class reading
	// ------------------------------------------------------------------------------------

	public void readClasses(String location_) throws Exception {
		File location = new File(location_);
		if (!location.exists())
			throw new IllegalArgumentException("File doesn't exist : " + location.getAbsoluteFile());
		try {
			if (location.isDirectory())
				readClassDirectory(location);
			else if (location.getName().endsWith(".jar"))
				readJarFile(location);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void readClassDirectory(File root) throws Exception {
		File[] list = root.listFiles();
		if (list == null)
			return;
		for (File f : list) {
			if (f.isDirectory()) {
				readClassDirectory(f);
			} else {
				if (!f.getName().endsWith(".class"))
					continue;
				InputStream stream = new FileInputStream(f);
				processClassInputStream(stream);
			}
		}
	}

	private void readJarFile(File jarFile_) throws Exception {
		ZipFile jarFile = new ZipFile(jarFile_);
		Enumeration<? extends ZipEntry> entries = jarFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = entries.nextElement();
			if (!(entry.getName().endsWith(".class")))
				continue;
			InputStream stream = jarFile.getInputStream(entry);
			processClassInputStream(stream);
		}
		jarFile.close();
	}

	private void processClassInputStream(InputStream stream) throws Exception {
		ClassReader cr = new ClassReader(stream);
		MPClassVisitor visitor = new MPClassVisitor(this);
		cr.accept(visitor, 0);
	}

}