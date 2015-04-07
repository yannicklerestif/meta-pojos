/*******************************************************************************
 * Copyright (c) 2000, 2012 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package com.yannicklerestif.metapojos.plugin;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.objectweb.asm.Type;

import com.yannicklerestif.metapojos.elements.beans.CallBean;
import com.yannicklerestif.metapojos.elements.beans.ClassBean;
import com.yannicklerestif.metapojos.elements.beans.JavaElementBean;
import com.yannicklerestif.metapojos.elements.beans.MethodBean;

/**
 * A hyper-link from a stack trace line of the form "*(*.java:*)"
 */
public class MetaPojosConsoleHyperlink implements IHyperlink {

	private JavaElementBean bean;

	public MetaPojosConsoleHyperlink(JavaElementBean bean) {
		this.bean = bean;
	}

	/**
	 * @see org.eclipse.debug.ui.console.IConsoleHyperlink#linkEntered()
	 */
	public void linkEntered() {
	}

	/**
	 * @see org.eclipse.debug.ui.console.IConsoleHyperlink#linkExited()
	 */
	public void linkExited() {
	}

	public void linkActivated() {
		try {
			//FIXME hyperlinks don't work for constructors
			//FIXME for cases where the exact java element cannot be resolved, use approximated line number
			// TODO refactor to not have to do this switch
			ClassBean classBean = null;
			if(bean instanceof ClassBean)
				classBean = (ClassBean) bean;
			else if(bean instanceof MethodBean)
				classBean = ((MethodBean) bean).getClassBean();
			else
				classBean = ((CallBean) bean).getSource().getClassBean();
			
			String enclosingTypeName = getEnclosingTypeName(classBean);
			IType enclosingType = findType(enclosingTypeName);
			if(enclosingType == null) {
				noResultsFound();
				return;
			}

			if (bean instanceof CallBean) {
				openEditor(enclosingType, ((CallBean) bean).getLine());
				return;
			}
			
			IType type = findType(classBean, enclosingType);
			if(type == null) {
				//if type is binary, this is possible, because binary types do not have
				//local or anonymous nested types.
				//if type isn't binary, this is not normal
				if(enclosingType.isBinary())
					System.err.println("Couldn't find inner type for " + classBean.toString());
				//defaulting to an approximate line number 
				openEditor(enclosingType);
				return;
			} else {
				openClassOrMethod(type);
				return;
			}
		} catch (Exception e) {
			MessageDialog.openError(null, "Error opening element", "Error opening element : " + bean);
			e.printStackTrace();
		}
	}

	private void openEditor(IJavaElement javaElement, int lineNumber) throws CoreException, BadLocationException {
		IEditorPart editorPart = JavaUI.openInEditor(javaElement);
		if (editorPart instanceof ITextEditor && lineNumber >= 0) {
			ITextEditor textEditor = (ITextEditor) editorPart;
			IDocumentProvider provider = textEditor.getDocumentProvider();
			IEditorInput editorInput = editorPart.getEditorInput();
			provider.connect(editorInput);
			IDocument document = provider.getDocument(editorInput);
			IRegion line = document.getLineInformation(lineNumber);
			textEditor.selectAndReveal(line.getOffset(), line.getLength());
			provider.disconnect(editorInput);
		}
	}

	private String getEnclosingTypeName(ClassBean classBean) {
		String className = classBean.toString();
		int pos = className.indexOf("$");
		return pos == -1 ? className : className.substring(0, pos);
	}

	private IType findType(String typeName) throws JavaModelException {
		List<IJavaProject> javaProjects = Activator.getJavaProjects();
		IType result = null;
		for (IJavaProject javaProject : javaProjects) {
			IType type = javaProject.findType(typeName);
			if (type == null)
				continue;
			if (!(type.isBinary()))
				return type;
			//if result is a binary type, we keep looking for a source type
			//in the other projects
			else
				result = type;
		}
		return result;
	}

	private void noResultsFound() {
		MessageDialog.openError(null, "Couldn't find element", "Couldn't find element : " + bean);
	}

	private static String convertToEclipseName(ClassBean classBean) {
		//qualified name will be the same between eclipse and asm, with one difference :
		//inner classes named something like $1SomeClass will not have the "1" in eclipse
		String[] split = classBean.toString().split("\\$");
		String targetClassName = split[0];
		for (int i = 1; i < split.length; i++) {
			String string = split[i];
			try {
				Integer.parseInt(string);
				targetClassName += "$" + string;
			} catch(NumberFormatException e) {
				for (int j = 0; j < string.length(); j++) {
					if(!Character.isDigit(string.charAt(j))) {
						targetClassName += "$" + string.substring(j);
						break;
					}
				}
			}
		}
		return targetClassName;
	}
	
	private static IType findType(ClassBean classBean, IType enclosingType) throws JavaModelException {
		String targetClassName = convertToEclipseName(classBean);
		
		LinkedList<IJavaElement> todo= new LinkedList<IJavaElement>();
		todo.add(enclosingType);
	    while (!todo.isEmpty()) {
	        IJavaElement element= todo.removeFirst();

	        if (element instanceof IType) {
	            IType type= (IType)element;
	            String name= type.getFullyQualifiedName();
	            if(name.equals(targetClassName))
	            	return type;
	        }

	        if (element instanceof IParent) {
	            for (IJavaElement child: ((IParent)element).getChildren()) {
	                todo.add(child);
	            }
	        }
	    }

		return null;
	}

	private void openClassOrMethod(IType type) throws CoreException, BadLocationException {
		if(bean instanceof ClassBean)
			openEditor(type, -1);
		else {
			IMethod method = findMethodInType(type);
			if(method == null) {
				//if type is binary, it is not impossible, but very unlikely, so it's better to log.
				//if type isn't binary, this is not normal
				System.err.println("Could find type but not the method : " + bean);
				//defaulting to approximate line number
				openEditor(type);
			} else 
				openEditor(method, -1);
		}
	}

	// default method if java element could not be resolved in eclipse
	private void openEditor(IType enclosingType) throws CoreException, BadLocationException {
		// FIXME use approximate line
		openEditor(enclosingType, -1);
	}

	private IMethod findMethodInType(IType type) throws JavaModelException {
		boolean binary = type.isBinary();
		MethodBean methodBean = (MethodBean) bean;
		IMethod[] methods = type.getMethods();
		Type[] methodBeanParametersTypes = Type.getArgumentTypes(methodBean.getOriginalDesc());
		String[] methodBeanParameters = new String[methodBeanParametersTypes.length];
		for (int i = 0; i < methodBeanParametersTypes.length; i++) {
			methodBeanParameters[i] = methodBeanParametersTypes[i].toString().replace("/", ".").replace("$", ".");
		}
		methods: for (int i = 0; i < methods.length; i++) {
			IMethod method = methods[i];
			if (!(method.getElementName().equals(methodBean.getName())))
				continue;
			String[] parameterTypes = method.getParameterTypes();
			if (parameterTypes.length != methodBeanParameters.length)
				continue;
			//at this point length are identical, we must now check types are identical.
			for (int j = 0; j < parameterTypes.length; j++) {
				String eclipseParameterType = Signature.getTypeErasure(parameterTypes[j]);
				String beanParameterType = methodBeanParameters[j];
				int eclipseArrayCount = Signature.getArrayCount(eclipseParameterType);
				String eclipseElementType = Signature.getElementType(eclipseParameterType);
				int beanArrayCount = Signature.getArrayCount(beanParameterType);
				String beanElementType = Signature.getElementType(beanParameterType);

				//getting rid of array nesting
				if (beanArrayCount != eclipseArrayCount)
					continue methods;

				//getting rid of primitive types and binary "normal"(not parameterized) types
				if (eclipseElementType.equals(beanElementType))
					continue;

				if (binary) {
					//only case we haven't ruled out yet is when eclipse has a parameterized type
					//and bean "java.lang.Object". In this case eclipse type will start with "T"
					if (eclipseElementType.startsWith("T") && beanElementType.contains("java.lang.Object"))
						continue;
					else
						continue methods;
				} else {
					//getting rid of the first (Q, T, L...) and the last (;) characters
					eclipseElementType = eclipseElementType.substring(1, eclipseElementType.length() - 2);
					beanElementType = beanElementType.substring(1, beanElementType.length() - 2);

					//it's legal for eclipse parameter not to be qualified
					if (beanElementType.endsWith(eclipseElementType)) {
						if (beanElementType.length() == eclipseElementType.length())
							continue;
						if (beanElementType.substring(0, eclipseElementType.length()).endsWith("."))
							continue;
					}

					//it can also be a parameterized type. In this case, bean type will be we hope it's one character long...
					if (beanElementType.equals("java.land.Object") && eclipseElementType.length() == 1)
						continue;
				}
			}
			//we when through all parameters and all were ok => this is the method
			return method;
		}
		return null;
	}

}
