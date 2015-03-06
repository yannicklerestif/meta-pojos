package com.yannicklerestif.metapojos;

import java.util.ListIterator;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.yannicklerestif.metapojos.elements.beans.DBClass;

public class MPClassVisitor extends ClassVisitor {

	private DataContainer dc;

	private DBClass dbClass;
	
	public MPClassVisitor(DataContainer dataContainer) {
		super(Opcodes.ASM5);
		this.dc = dataContainer;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		dbClass = dc.getOrCreateDBClass(name);

		//Class is already present : first one one classpath wins
		if(!dbClass.isShallow())
			return;
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
		dc.getOrCreateDBMethod(dbClass.getName(), name, desc);
		return null;
	}

}
