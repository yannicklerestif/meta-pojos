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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
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
			IType type = findClassType(bean);
			if (type == null) {
				noResultsFound();
				return;
			}
			if (bean instanceof MethodBean) {
				IMethod method = findMethodType(type);
				if(method !=null)
					JavaUI.openInEditor(method);
			} else {
				IEditorPart editorPart = JavaUI.openInEditor(type);
				if (bean instanceof ClassBean)
					return;
				else if (bean instanceof CallBean) {
					int lineNumber = ((CallBean) bean).getLine();
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
			}
		} catch (Exception e) {
			MessageDialog.openError(null, "Error opening element", "Error opening element : " + bean);
			e.printStackTrace();
		}
	}

	private IMethod findMethodType(IType type) throws JavaModelException {
		//FIXME some hyperlinks still don't work
		//TODO hyperlinks don't work for constructors
		//TODO hyperlinks don't work in anonymous classes and their methods in binary code
		//for these cases a workaround would be to get the first line of code for the methods,
		//and the first line of the first method for anonymous classes
		//TODO hyperlinks don't work for nested anonymous classes and their methods
		boolean binary = (type.getCompilationUnit() == null);
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

	//TODO let all java element beans implement sourcebean that knows enclosing type
	private IType findClassType(JavaElementBean bean2) {
		ClassBean classBean = null;
		if (bean instanceof ClassBean)
			classBean = (ClassBean) bean;
		else if (bean instanceof MethodBean)
			classBean = ((MethodBean) bean).getClassBean();
		else if (bean instanceof CallBean)
			classBean = ((CallBean) bean).getSource().getClassBean();

		//for inner types
		//TODO for anonymous inner types this doesn't work, we must go up containing tree
		String qualifiedName = classBean.toString();

		List<IJavaProject> javaProjects = Activator.getJavaProjects();

		return findTypeInProjects(qualifiedName, javaProjects);
	}

	private IType findTypeInProjects(String qualifiedName, List<IJavaProject> javaProjects) {
		IType result = null;
		for (IJavaProject javaProject : javaProjects) {
			try {
				IType type = findType(javaProject, qualifiedName);
				if (type == null)
					continue;
				if (type.getCompilationUnit() != null)
					return type;
				//if result is a binary type, we keep looking for a source type
				//in the other projects
				else
					result = type;
			} catch (JavaModelException e) {
				e.printStackTrace();
				continue;
			}
		}
		return result;
	}

	public static IType findType(IJavaProject javaProject, final String className) 
	throws JavaModelException 
	{
	    String primaryName= className;
	    int i= primaryName.lastIndexOf('$');
	    int occurence= 0;
	    if (0 < i) {
	        try {
	            occurence= Integer.parseInt(primaryName.substring(i+1));
	            primaryName= primaryName.substring(0, i);
	        }
	        catch (NumberFormatException x) {
	        }
	    }
	    
	    /*
	     * IJavaProject.findType works for top level classes and named inner 
	     * classes, but not for anonymous inner classes
	     */
	    IType primaryType= javaProject.findType(primaryName);
	    if(primaryType == null || !primaryType.exists())
	        return null;
	    if (occurence <= 0) // if not anonymous then we done
	        return primaryType;

	    /*
	     * If we're looking for an anonymous inner class then we need to look 
	     * through the primary type for it. 
	     */
	    LinkedList<IJavaElement> todo= new LinkedList<IJavaElement>();
	    todo.add(primaryType);
	    IType innerType= null;
	    while (!todo.isEmpty()) {
	        IJavaElement element= todo.removeFirst();

	        if (element instanceof IType) {
	            IType type= (IType)element;
	            String name= type.getFullyQualifiedName();
	            System.out.println("\t" + name);
	            if (name.equals(className)) {
	                innerType= type;
	                break;
	            }
	        }

	        if (element instanceof IParent) {
	            for (IJavaElement child:((IParent)element).getChildren()) {
	                todo.add(child);
	            }
	        }
	    }

	    return innerType;
	}

	private void noResultsFound() {
		MessageDialog.openError(null, "Couldn't find element", "Couldn't find element : " + bean);
	}

}
