package com.yannicklerestif.metapojos;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.yannicklerestif.metapojos.elements.beans.ClassBean;
import com.yannicklerestif.metapojos.elements.beans.MethodBean;

public class MPClassVisitor extends ClassVisitor {


	private static String GET_DEBUGGED_CLASS() {
//		return "test/model/StartingClass";
//		return "java/util/ArrayList";
		return null;
	}
	
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

		//Class is already present : first one on classpath wins
		if (!classBean.isShallow()) {
			abort = true;
			return;
		}
		classBean.setShallow(false);

		if(GET_DEBUGGED_CLASS() != null && name.startsWith(GET_DEBUGGED_CLASS()))
			System.out.println(classBean.toString());

		//if class is java.lang.Object, superName is null
		if(superName != null) {
			ClassBean parent = dc.getOrCreateClassBean(superName);
			classBean.addParent(parent);
			parent.addChild(classBean);
		}
		for (String interfaceName : interfaces) {
			ClassBean parent = dc.getOrCreateClassBean(interfaceName);
			classBean.addParent(parent);
			parent.addChild(classBean);
		}
	}

	@Override
	public void visitInnerClass(String name, String outerName, String innerName, int access) {
		if(classBean.getName().equals(name) && ((access & Opcodes.ACC_STATIC) == 0))
			classBean.setRootOrInnerStatic(false);
	}
	
	@Override
	public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
		if (abort)
			return null;
		if((Opcodes.ACC_BRIDGE & access) > 0 || (Opcodes.ACC_SYNTHETIC & access) > 0)
			return null;
		
		if(GET_DEBUGGED_CLASS() != null && classBean.getName().startsWith(GET_DEBUGGED_CLASS()))
			System.out.println("\t" + Integer.toBinaryString(access) + " " + classBean.getName() + " - " + name + " - " + desc);
		
		String[] splitDesc = dc.splitDesc(desc);
		MethodBean methodBean = dc.getOrCreateMethodBean(classBean.getName(), name, splitDesc[0]);
		methodBean.setReturnType(splitDesc[1]);
		methodBean.setShallow(false);
		return new MPMethodVisitor(dc, methodBean);
	}
	
	@Override
	public void visitEnd() {
		if(GET_DEBUGGED_CLASS() != null && classBean.getName().startsWith(GET_DEBUGGED_CLASS()))
			System.out.println("\t=>" + classBean.isRootOrInnerStatic());
	}
	
}
