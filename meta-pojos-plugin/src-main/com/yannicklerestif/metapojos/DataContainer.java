package com.yannicklerestif.metapojos;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;

import com.yannicklerestif.metapojos.elements.beans.ClassBean;
import com.yannicklerestif.metapojos.elements.beans.MethodBean;
import com.yannicklerestif.metapojos.elements.beans.MethodBean.MethodBeanKey;
import com.yannicklerestif.metapojos.elements.streams.ClassStream;

public class DataContainer {

	private Map<String, ClassBean> classes = new HashMap<String, ClassBean>();

	// ------------------------------------------------------------------------------------
	// container
	// ------------------------------------------------------------------------------------

	public ClassStream allClasses() {
		return new ClassStream(classes.values().stream());
	}

	public ClassStream singleClass(String className) {
		ClassBean classBean = classes.get(className.replace('.', '/'));
		return new ClassStream(classBean == null ? Stream.empty() : Stream.of(classBean));
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
			classBean.setName(name);
			classes.put(name, classBean);
		}
		return classBean;
	}

	// ------------------------------------------------------------------------------------
	// class reading
	// ------------------------------------------------------------------------------------

	public void readClasses(String[] locations) throws Exception {
		for (int i = 0; i < locations.length; i++) {
			File location = new File(locations[i]);
			if (!location.exists())
				throw new IllegalArgumentException("File doesn't exist : " + location.getAbsoluteFile());
			if (readClassDirectoryOrJarFile(location))
				continue;
			readLocationsFile(location);
		}
	}

	private boolean readClassDirectoryOrJarFile(File location) throws Exception {
		if (location.isDirectory()) {
			readClassDirectory(location);
			return true;
		} else if (location.getName().endsWith(".jar")) {
			readJarFile(location);
			return true;
		}
		return false;
	}

	private void readLocationsFile(File location) throws Exception {
		Files.lines(location.toPath()).forEach(locationName -> {
			try {
				if (!readClassDirectoryOrJarFile(new File(locationName)))
					throw new IllegalArgumentException("Not a jar file or a folder : " + locationName);
			} catch (Exception e) {
				throw new RuntimeException("Exception reading location file " + location.getAbsolutePath(), e);
			}
		});
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
		// ClassNode cn = new ClassNode();
		// cr.accept(cn, 0);
		// if (cn.name.contains("StartingClass"))
		// System.out.println(cn.name);
	}

}