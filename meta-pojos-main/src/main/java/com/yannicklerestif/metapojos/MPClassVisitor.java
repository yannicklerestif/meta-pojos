package com.yannicklerestif.metapojos;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.yannicklerestif.metapojos.elements.beans.DBClass;
import com.yannicklerestif.metapojos.elements.beans.DBMethod;

public class MPClassVisitor extends ClassVisitor {

	private DataContainer dc;

	private DBClass dbClass;

	private boolean abort = false;

	public MPClassVisitor(DataContainer dataContainer) {
		super(Opcodes.ASM5);
		this.dc = dataContainer;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		//		System.out.println(name);
		dbClass = dc.getOrCreateDBClass(name);

		//Class is already present : first one one classpath wins
		if (!dbClass.isShallow()) {
			abort = true;
			return;
		}
		dbClass.setShallow(false);

		DBClass parent = dc.getOrCreateDBClass(superName);
		dbClass.addParent(parent);
		parent.addChild(dbClass);
		for (String interfaceName : interfaces) {
			parent = dc.getOrCreateDBClass(interfaceName);
			dbClass.addParent(parent);
			parent.addChild(dbClass);
		}
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (abort)
			return null;
		//		System.out.println("\t"+name + " - " + desc);
		DBMethod dbMethod = dc.getOrCreateDBMethod(dbClass.getName(), name, desc);
		return new MPMethodVisitor(dc, dbMethod);
	}

}
