package com.yannicklerestif.metapojos.plugin.debug;

import java.util.Date;
import java.util.List;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

import com.yannicklerestif.metapojos.plugin.MetaPojosPluginImpl;
import com.yannicklerestif.metapojos.plugin.PluginAccessor;

public class DebuggerOutput {

	private boolean DEBUG_MODE() {
		//set to true only in dev !
		return false;
	}

	private boolean DEBUG_EVENTS() {
		return true;
	}

	private String DEBUGGED_ECLIPSE_CLASS_NAME() {
		//		return "test.model.StartingClass";
		//		return "test.model.SomeParameterizedClass";
		return "java.util.ArrayList";
	}

	// print eclipse class -------------------------------------------------------------------------

	private void printEclipseClass() throws JavaModelException {
		List<IJavaProject> javaProjects = plugin.getWorkspace().getJavaProjects();
		IType primaryType = null;
		for (IJavaProject project : javaProjects) {
			primaryType = project.findType(DEBUGGED_ECLIPSE_CLASS_NAME());
			if (primaryType != null)
				break;
		}
		if (primaryType == null)
			return;
		print("", primaryType);
	}

	private void print(String prefix, IJavaElement element) throws JavaModelException {
		System.out.println(prefix + element.getElementName() + (element instanceof IMethod ? "()" : ""));
		if (element instanceof IParent) {
			for (IJavaElement child : ((IParent) element).getChildren()) {
				print(prefix + "\t", child);
			}
		}
	}

	// print event content -------------------------------------------------

	public void debugEvent(IResourceChangeEvent event) {
		debugEvent(event, false);
	}
	
	public void debugEvent(IResourceChangeEvent event, boolean force) {
		if(!force) {
			if(!(DEBUG_MODE()))
				return;
			if(!(DEBUG_EVENTS()))
				return;
		}
		System.out.println("---------------------------------");
		System.out.println(new Date());
		System.out.println("POST_CHANGE event : " + (((event.getType() & IResourceChangeEvent.POST_CHANGE) > 0) ? "TRUE" : ""));
		System.out.println("PRE_CLOSE event : " + (((event.getType() & IResourceChangeEvent.PRE_CLOSE) > 0) ? "TRUE" : ""));
		System.out.println("PRE_DELETE event : " + (((event.getType() & IResourceChangeEvent.PRE_DELETE) > 0) ? "TRUE" : ""));
		System.out.println("PRE_BUILD event : " + (((event.getType() & IResourceChangeEvent.PRE_BUILD) > 0) ? "TRUE" : ""));
		System.out.println("POST_BUILD event : " + (((event.getType() & IResourceChangeEvent.POST_BUILD) > 0) ? "TRUE" : ""));
		System.out.println("PRE_REFRESH event : " + (((event.getType() & IResourceChangeEvent.PRE_REFRESH) > 0) ? "TRUE" : ""));
		IResourceDelta delta = event.getDelta();
		if(delta == null)
			System.out.println("delta is null");
		else
			try {
				delta.accept(new MetaPojosResourceDeltaVisitor());
			} catch (CoreException e) {
				System.out.println("Error trying to browse delta :");
				e.printStackTrace();
			}
	}
 
	private class MetaPojosResourceDeltaVisitor implements IResourceDeltaVisitor {

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			switch(delta.getKind()) {
			case IResourceDelta.ADDED :
				System.out.print("[ADDED]   ");
				break;
			case IResourceDelta.REMOVED:
				System.out.print("[REMOVED] ");
				break;
			case IResourceDelta.CHANGED :
				System.out.print("[CHANGED] ");
				break;
			default :
				System.out.print("[OTHER]   ");
				break;
			}
			int segmentCount = delta.getFullPath().segmentCount();
			for (int i = 0; i < segmentCount; i++) {
				System.out.print("\t");
			}
			System.out.print(delta.getResource());
			System.out.print(" - flags : 0x" + Integer.toHexString(delta.getFlags()));
			System.out.println();
			return true;
		}
		
	}
	

	//-------------------------------------------------------------------------

	private static DebuggerOutput instance;

	public static void init(MetaPojosPluginImpl metaPojosPluginImpl) {
		instance = new DebuggerOutput(metaPojosPluginImpl);
		instance.createTestThread();
	}

	public static DebuggerOutput get() {
		return instance;
	}

	private MetaPojosPluginImpl plugin;

	public DebuggerOutput(MetaPojosPluginImpl plugin) {
		this.plugin = plugin;
	}

	public void createTestThread() {
		if (DEBUG_MODE()) {
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
	}

	private void test() throws Exception {
		System.in.read();
		System.out.println("input read -------------------------------");
		printEclipseClass();
	}

}
