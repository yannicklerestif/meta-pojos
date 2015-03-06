package com.yannicklerestif.metapojos;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.yannicklerestif.metapojos.elements.beans.DBCall;
import com.yannicklerestif.metapojos.elements.beans.DBMethod;

public class MPMethodVisitor extends MethodVisitor {

	private int currentLine = -1;

	private DataContainer dc;

	private DBMethod dbMethod;

	public MPMethodVisitor(DataContainer dc, DBMethod dbMethod) {
		super(Opcodes.ASM5);
		this.dc = dc;
		this.dbMethod = dbMethod;
	}

	@Override
	public void visitMethodInsn(int opcode, String owner, String targetMethodName, String targetMethodDesc, boolean itf) {
		// System.out.println("\t\t" + currentLine + ":" + owner + " " + targetMethodName + " " + targetMethodDesc);
		DBMethod targetDbMethod = dc.getOrCreateDBMethod(owner, targetMethodName, targetMethodDesc);
		DBCall call = new DBCall(dbMethod, currentLine, targetDbMethod);
		dbMethod.getCalls().add(call);
		targetDbMethod.getCallsTo().add(call);
	}

	@Override
	public void visitLineNumber(int line, Label start) {
		currentLine = line;
	}

}
