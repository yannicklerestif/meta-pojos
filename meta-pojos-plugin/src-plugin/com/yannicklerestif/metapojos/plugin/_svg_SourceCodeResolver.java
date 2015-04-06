package com.yannicklerestif.metapojos.plugin;

import java.util.LinkedList;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;

public class _svg_SourceCodeResolver {
	/**
	 * Return an IType (Source type, not Binary) for the given class name.
	 * 
	 * @return null if no such class can be found.
	 * @throws JavaModelException
	 */
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
	    if (!primaryType.exists())
	        return null;
	    if (occurence <= 0) // if not anonymous then we done
	        return primaryType;

	    /*
	     * the following snippet never works, but according to the 
	     * docs it should.  
	     */
	    IType innrType= primaryType.getType("", occurence);
	    if (innrType != null) {
	        String name= primaryType.getFullyQualifiedName();
	        if (name.equals(className)) {
	            return innrType;
	        }
	    }

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
}
