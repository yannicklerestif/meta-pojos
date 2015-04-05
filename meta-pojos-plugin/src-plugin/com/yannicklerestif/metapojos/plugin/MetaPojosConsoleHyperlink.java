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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.internal.debug.ui.JDIDebugUIPlugin;
import org.eclipse.jdt.internal.debug.ui.actions.OpenTypeAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.console.IHyperlink;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import com.yannicklerestif.metapojos.elements.beans.CallBean;
import com.yannicklerestif.metapojos.elements.beans.ClassBean;
import com.yannicklerestif.metapojos.elements.beans.JavaElementBean;
import com.yannicklerestif.metapojos.elements.beans.MethodBean;

/**
 * A hyper-link from a stack trace line of the form "*(*.java:*)"
 */
public class MetaPojosConsoleHyperlink implements IHyperlink {

	private String typeName;
	private int lineNumber;
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

	/**
	 * @see org.eclipse.debug.ui.console.IConsoleHyperlink#linkActivated()
	 */
	public void linkActivated() {
		//FIXME get line for class and methods declarations
		if(bean instanceof ClassBean) {
			ClassBean classBean = (ClassBean) bean;
			typeName = classBean.toString();
			lineNumber = -1;
		} else if(bean instanceof MethodBean) {
			MethodBean methodBean = (MethodBean) bean;
			typeName = methodBean.getClassBean().toString();
			lineNumber = -1;
		} else if(bean instanceof CallBean) {
			CallBean callBean = (CallBean) bean;
			typeName = callBean.getSource().getClassBean().toString();
			lineNumber = callBean.getLine();
		}
		
		// documents start at 0
		if (lineNumber > 0) {
			lineNumber--;
		}
		Object result = null;
		// search for the type in the workspace
		try {
			result = OpenTypeAction.findTypeInWorkspace(typeName, true);
			if (result == null) {
				result = OpenTypeAction.findTypeInWorkspace(typeName, false);
			}
			if (result == null) {
				noResultsFound();
			} else {
				processSearchResult(result, typeName, lineNumber);
			}
		} catch (CoreException e) {
			noResultsFound();
		}
	}

	private void noResultsFound() {
		MessageDialog.openError(null, "Couldn't find class", "Couldn't find class : " + typeName);
	}

	protected void processSearchResult(Object source, String typeName, int lineNumber) {
		IType type = (IType) source;
		IClassFile classFile = null;
		IDebugModelPresentation presentation = JDIDebugUIPlugin.getDefault().getModelPresentation();
		IEditorInput editorInput = presentation.getEditorInput(source);
		if (editorInput != null) {
			String editorId = presentation.getEditorId(editorInput, source);
			if (editorId != null) {
				try {
					IEditorPart editorPart = JDIDebugUIPlugin.getActivePage().openEditor(editorInput, editorId);
					if (editorPart instanceof ITextEditor && lineNumber >= 0) {
						ITextEditor textEditor = (ITextEditor) editorPart;
						IDocumentProvider provider = textEditor.getDocumentProvider();
						provider.connect(editorInput);
						IDocument document = provider.getDocument(editorInput);
						try {
							IRegion line = document.getLineInformation(lineNumber);
							textEditor.selectAndReveal(line.getOffset(), line.getLength());
						} catch (BadLocationException e) {
							MessageDialog.openError(null, "Line not found", "Line not found : " + lineNumber
									+ " in class " + typeName);

						}
						provider.disconnect(editorInput);
					}
				} catch (CoreException e) {
					JDIDebugUIPlugin.statusDialog(e.getStatus());
				}
			}
		}
	}
}
