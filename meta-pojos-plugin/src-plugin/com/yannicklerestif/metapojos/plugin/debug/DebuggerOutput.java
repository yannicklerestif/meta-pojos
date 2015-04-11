package com.yannicklerestif.metapojos.plugin.debug;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.yannicklerestif.metapojos.plugin.MetaPojosPluginImpl;
import com.yannicklerestif.metapojos.plugin.PluginAccessor;

public class DebuggerOutput {

	private MetaPojosPluginImpl plugin;
	
	public DebuggerOutput(MetaPojosPluginImpl plugin) {
		this.plugin = plugin;
	}

	public void createTestThread() {
		//only for tests !
		Thread test = new Thread(new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						test();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});
		test.start();
	}

	private void test() throws Exception {
		System.in.read();
		System.out.println("input read -------------------------------");
		eclipsePrint("java.util.ArrayList");
	}

	private void eclipsePrint(String string) throws JavaModelException {
		List<IJavaProject> javaProjects = plugin.getWorkspace().getJavaProjects();
		IType primaryType = null;
		for (IJavaProject project : javaProjects) {
//			primaryType = project.findType("test.model.StartingClass");
			//			primaryType = project.findType("test.model.SomeParameterizedClass");
			//			primaryType = project.findType("test.model.SomeClass");
			//			primaryType = project.findType("java.lang.Object");
			primaryType = project.findType("java.util.ArrayList");
//			primaryType = project.findType("java.awt.EventQueue$1AWTInvocationLock");
			//			primaryType = project.findType("com.yannicklerestif.metapojos.MetaPojos");
			if (primaryType != null)
				break;
		}
		if (primaryType == null)
			return;
		print("",primaryType);
	}

	private void print(String prefix, IJavaElement element) throws JavaModelException {
       	System.out.println(prefix + element.getElementName() + (element instanceof IMethod ? "()" : ""));
        if (element instanceof IParent) {
            for (IJavaElement child: ((IParent)element).getChildren()) {
                print(prefix + "\t", child);
            }
        }
	}

}
