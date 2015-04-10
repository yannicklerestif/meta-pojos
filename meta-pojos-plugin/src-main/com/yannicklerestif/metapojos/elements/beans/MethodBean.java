package com.yannicklerestif.metapojos.elements.beans;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.Signature;
import org.objectweb.asm.Type;

import com.yannicklerestif.metapojos.plugin.MetaPojosHyperlinkedOutput;

public class MethodBean extends JavaElementBean {

	private boolean shallow = true;

	public boolean isShallow() {
		return shallow;
	}

	public void setShallow(boolean isShallow) {
		this.shallow = isShallow;
	}

	private ClassBean classBean;

	private String name;

	private String desc;

	private List<CallBean> calls = new ArrayList<CallBean>();

	public List<CallBean> getCalls() {
		return calls;
	}

	private List<CallBean> callsTo = new ArrayList<CallBean>();

	private String returnType;

	private int lineNumber = -1;

	public List<CallBean> getCallsTo() {
		return callsTo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getInternalDesc() {
		return "(" + desc + ")" + returnType;
	}

	public ClassBean getClassBean() {
		return classBean;
	}

	public void setClassBean(ClassBean classBean) {
		this.classBean = classBean;
	}

	public static class MethodBeanKey {
		public String methodName;
		public String desc;

		public MethodBeanKey(String methodName, String desc) {
			super();
			this.methodName = methodName;
			this.desc = desc;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = desc.hashCode();
			result = prime * result + methodName.hashCode();
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			MethodBeanKey other = (MethodBeanKey) obj;
			if (!desc.equals(other.desc))
				return false;
			if (!methodName.equals(other.methodName))
				return false;
			return true;
		}

	}

	public static class MethodDesc {
		public boolean isConstructor;

		public List<MethodArgument> arguments = new ArrayList<>();

		public MethodDesc(MethodBean methodBean) {
			isConstructor = methodBean.getName().equals("<init>");
			Type[] argumentTypes = Type.getArgumentTypes(methodBean.getInternalDesc());
			for (int i = 0; i < argumentTypes.length; i++)
				arguments.add(new MethodArgument(argumentTypes[i]));
		}
	}

	public static class MethodArgument {
		public int arrayCount;
		public String typeName;

		public MethodArgument(Type type) {
			String typeName_ = type.toString();
			arrayCount = Signature.getArrayCount(typeName_);
			typeName = Signature.getElementType(typeName_);
		}

		public String getPrettyTypeName() {
			String prettyQualifiedTypeName = getDottedElementTypeName();
			int pos = prettyQualifiedTypeName.lastIndexOf('.');
			switch (prettyQualifiedTypeName) {
			case "B":
				return "byte";
			case "C":
				return "char";
			case "D":
				return "double";
			case "F":
				return "float";
			case "I":
				return "int";
			case "J":
				return "long";
			case "S":
				return "short";
			case "Z":
				return "boolean";
			case "V":
				return "void";
			}
			if (pos == -1)
				return prettyQualifiedTypeName.substring(0, prettyQualifiedTypeName.length() - 1);
			else
				return prettyQualifiedTypeName.substring(pos + 1, prettyQualifiedTypeName.length() - 1);
		}

		public String getDottedElementTypeName() {
			return typeName.replace('/', '.').replace('$', '.');
		}

		public String getPrettyIdentifier() {
			StringBuilder result = new StringBuilder(getPrettyTypeName());
			for (int i = 0; i < arrayCount; i++)
				result.append("[]");
			return result.toString();
		}
	}

	public MethodDesc getMethodDesc() {
		return new MethodDesc(this);
	}

	public void setReturnType(String string) {
		this.returnType = string;
	}

	public void setLineNumber(int line) {
		this.lineNumber = line;
	}

	@Override
	public String toString() {
		return getPrettyName();
	}

	//TODO this is costly and is used for sorting => store result in a "QueryResultLine" object
	public String getPrettyName() {
		MethodDesc methodDesc = getMethodDesc();
		StringBuilder builder = new StringBuilder(classBean.getPrettyName()).append(".");
		if (!(methodDesc.isConstructor))
			builder.append(name);
		else
			builder.append("<init>");
		builder.append("(");
		boolean firstArg = true;
		boolean constructorFirstArg = !classBean.isRootOrInnerStatic();
		for (MethodArgument methodArgument : methodDesc.arguments) {
			if (constructorFirstArg) {
				constructorFirstArg = false;
				continue;
			}
			if (!firstArg)
				builder.append(",");
			else
				firstArg = false;
			builder.append(methodArgument.getPrettyIdentifier());
		}
		MethodArgument returnType2 = new MethodArgument(Type.getReturnType(getInternalDesc()));
		builder.append("):").append(returnType2.getPrettyIdentifier());
		return builder.toString();
	}

	@Override
	public MetaPojosHyperlinkedOutput getHyperlinkedOutput() {
		MetaPojosHyperlinkedOutput result = new MetaPojosHyperlinkedOutput().add("Method ");
		if (shallow)
			result.add(getPrettyName() + " [shallow]");
		else
			result.add(getPrettyName(), this);
		return result;
	}

	public int getLineNumber() {
		return lineNumber;
	}
}
