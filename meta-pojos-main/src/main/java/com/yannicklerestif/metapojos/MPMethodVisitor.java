package com.yannicklerestif.metapojos;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.yannicklerestif.metapojos.elements.beans.CallBean;
import com.yannicklerestif.metapojos.elements.beans.MethodBean;

public class MPMethodVisitor extends MethodVisitor {

	private int currentLine = -1;

	private DataContainer dc;

	private MethodBean methodBean;

	public MPMethodVisitor(DataContainer dc, MethodBean methodBean) {
		super(Opcodes.ASM5);
		this.dc = dc;
		this.methodBean = methodBean;
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String targetMethodName, String targetMethodDesc, boolean itf) {
		// System.out.println("\t\t" + currentLine + ":" + owner + " " + targetMethodName + " " + targetMethodDesc);
		String[] splitDesc = dc.splitDesc(targetMethodDesc);
		MethodBean targetMethodBean = dc.getOrCreateMethodBean(owner, targetMethodName, splitDesc[0]);
		targetMethodBean.setReturnType(splitDesc[1]);
		CallBean call = new CallBean(methodBean, currentLine, targetMethodBean);
		methodBean.getCalls().add(call);
		targetMethodBean.getCallsTo().add(call);
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		if(currentLine == -1)
			methodBean.setLineNumber(line);
		currentLine = line;
	}

}
