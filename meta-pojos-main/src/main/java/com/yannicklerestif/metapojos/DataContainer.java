package com.yannicklerestif.metapojos;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yannicklerestif.metapojos.elements.beans.DBClass;
import com.yannicklerestif.metapojos.elements.beans.DBClassRelation;
import com.yannicklerestif.metapojos.elements.beans.DBMethod;

@Service
public class DataContainer {

	@Autowired
	private DatabaseManager db;

	// ------------------------------------------------------------------------------------
	// container
	// ------------------------------------------------------------------------------------

	private List<Integer> shallowClasses = null;
	
	private void addClass(ClassNode cn) {
		DBClass dbClass = new DBClass();
		Integer classId = classesIdsByName.get(cn.name);
		if(classId != null && classes.get(classId).isShallow() == false)
			throw new IllegalStateException("Duplicate class : " + cn.name);
		if(classId == null)
			classId = ++classesMaxIndex;
		classesIdsByName.put(cn.name, classId);
		dbClass.setId(classId);
		dbClass.setName(cn.name);
		dbClass.setShallow(false);
		
		List<DBClassRelation> parents = new ArrayList<DBClassRelation>(); 
		parents.add(new DBClassRelation(getOrCreateClassId(cn.superName), classId));
		for(String interface_ : cn.interfaces) {
			parents.add(new DBClassRelation(getOrCreateClassId(interface_), classId));
		}
		dbClass.setParents(parents);
		
		List<DBMethod> dbMethods = new ArrayList<DBMethod>();
		for (MethodNode method : cn.methods) {
			DBMethod dbMethod = new DBMethod();
			Integer methodId = methodsIdsIndex.getOrCreateId(dbClass.getId(), method.name, method.desc);
			dbMethod.setId(methodId);
			dbMethod.setClassId(classId);
			dbMethod.setName(method.name);
			dbMethod.setDesc(method.desc);
			dbMethods.add(dbMethod);
		}
		dbClass.setMethods(dbMethods);
		
		classes.put(classId, dbClass);
		db.persist(dbClass);
	}

	// classes ----------------------------------------------------------------------------
	
	private Map<String, Integer> classesIdsByName = new HashMap<String, Integer>();
	
	private Map<Integer, DBClass> classes = new HashMap<Integer, DBClass>();

	private int classesMaxIndex = -1;
	
	private MethodsIdsIndex methodsIdsIndex = new MethodsIdsIndex(); 
	
	private Integer getOrCreateClassId(String className) {
		Integer classId = classesIdsByName.get(className);
		if(classId == null) {
			classesMaxIndex++;
			classId = classesMaxIndex;
			DBClass shallowClass = new DBClass();
			shallowClass.setId(classId);
			shallowClass.setName(className);
			classes.put(classId, shallowClass);
			classesIdsByName.put(className, classId);
			shallowClasses.add(classId);
		}
		return classId;
	}
	
	// ------------------------------------------------------------------------------------
	// class reading
	// ------------------------------------------------------------------------------------

	@Transactional
	public void storeClasses(String[] classesJarOrDirectories) throws Exception {
		shallowClasses = new ArrayList<Integer>();
		db.clear();
		db.openImportFiles();
		long start = System.currentTimeMillis();
		try {
			for (int i = 0; i < classesJarOrDirectories.length; i++) {
				File classesJarOrDirectory = new File(classesJarOrDirectories[i]);
				if (classesJarOrDirectory.isDirectory())
					storeClassDirectory(classesJarOrDirectory);
				else if (classesJarOrDirectory.getName().endsWith(".jar"))
					storeJarFile(classesJarOrDirectory);
				else
					throw new IllegalArgumentException("Not a jar file or a folder : "
							+ classesJarOrDirectory.getAbsoluteFile());
			}
			for (Integer integer : shallowClasses) {
				DBClass dbClass = classes.get(integer);
				// shallow classes may have been replaced by real ones
				if(dbClass.isShallow())
					db.persist(dbClass);
			}
			long end = System.currentTimeMillis();
			System.out.println("reading : " + (end - start));
			db.flush();
		} finally {
			db.closeImportFiles();
			shallowClasses = null;
		}
	}

	private void storeClassDirectory(File root) throws Exception {
		File[] list = root.listFiles();
		if (list == null)
			return;
		for (File f : list) {
			if (f.isDirectory()) {
				storeClassDirectory(f);
			} else {
				if (!f.getName().endsWith(".class"))
					continue;
				InputStream stream = new FileInputStream(f);
				processClassInputStream(stream);
			}
		}
	}

	private void storeJarFile(File jarFile_) throws Exception {
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
		ClassNode cn = new ClassNode();
		cr.accept(cn, 0);
		addClass(cn);
	}

}