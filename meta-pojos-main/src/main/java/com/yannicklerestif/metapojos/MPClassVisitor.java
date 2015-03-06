package com.yannicklerestif.metapojos;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import com.yannicklerestif.metapojos.elements.beans.ClassBean;
import com.yannicklerestif.metapojos.elements.beans.MethodBean;

public class MPClassVisitor extends ClassVisitor {

	private DataContainer dc;

	private ClassBean classBean;

	private boolean abort = false;

	public MPClassVisitor(DataContainer dataContainer) {
		super(Opcodes.ASM5);
		this.dc = dataContainer;
	}

	@Override
	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		//		System.out.println(name);
		classBean = dc.getOrCreateClassBean(name);

		//Class is already present : first one one classpath wins
		if (!classBean.isShallow()) {
			abort = true;
			return;
		}
		classBean.setShallow(false);

		ClassBean parent = dc.getOrCreateClassBean(superName);
		classBean.addParent(parent);
		parent.addChild(classBean);
		for (String interfaceName : interfaces) {
			parent = dc.getOrCreateClassBean(interfaceName);
			classBean.addParent(parent);
			parent.addChild(classBean);
		}
	}

	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (abort)
			return null;
		if((Opcodes.ACC_BRIDGE & access) > 0 || (Opcodes.ACC_SYNTHETIC & access) > 0)
			return null;
//		System.out.println("\t" + classBean.getName() + " - " + name + " - " + desc);
		String[] splitDesc = dc.splitDesc(desc);
		MethodBean methodBean = dc.getOrCreateMethodBean(classBean.getName(), name, splitDesc[0]);
		methodBean.setReturnType(splitDesc[1]);
		return new MPMethodVisitor(dc, methodBean);
	}

}
