/**
 * Copyright 2008, AQUA Software
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aqua.wikiwizard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotatable;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;

/**
 * Model to get all information from the object
 * @author Guy Arieli, Michael Oziransky
 */
public class ObjectsJavaModel  {
	
	private IType javaObject;
	private IJavaElement[] allChildrens;
	
	/**
	 * Create an object from a given java type
	 * @param javaObject
	 * 			Any given java type
	 * @throws Exception
	 */
	public ObjectsJavaModel(IType javaObject) throws Exception {
		this.javaObject = javaObject;

		if (javaObject.getTypeQualifiedName().equals("Linux")) {
			System.out.println("");
		}
		
		ITypeHierarchy hierarchy = javaObject.newTypeHierarchy(null);
		IType[] superTypes = hierarchy.getAllSuperclasses(javaObject);
		IType[] allTypes = new IType[superTypes.length + 1];
		
		// Create a new array and add this type to the end of the list
		System.arraycopy(superTypes, 0, allTypes, 0, superTypes.length);		
		allTypes[superTypes.length] = javaObject;
		
		ArrayList<IJavaElement> elements = new ArrayList<IJavaElement>();
		
		for (IType curObject: allTypes) {
			// Don't need the actual SystemObjectImpl & Object fields
			if (curObject.getElementName().equals("SystemObjectImpl") || curObject.getElementName().equals("Object")){
				continue;
			}
			// Add all children of this type
			elements.addAll(Arrays.asList(curObject.getChildren()));
		}
		allChildrens = elements.toArray(new IJavaElement[elements.size()]);
	}

	/**
	 * Returns the name of the object
	 * @return
	 * 		String
	 */
	public String getObjectName() {
		String packageName = "";
		IJavaElement parent = javaObject.getParent();
		while(true){
			if(parent == null){
				break;
			}
			if(parent instanceof IPackageFragment){
				packageName = ((IPackageFragment)parent).getElementName() + ".";
				break;
			}
			parent = parent.getParent();
		}
		return packageName + javaObject.getTypeQualifiedName();
	}
	
	/**
	 * Returns a short name
	 * @return
	 * 			String
	 */
	public String getShortName(){
		return javaObject.getTypeQualifiedName();
	}

	/**
	 * Returns the javadoc 
	 * @return
	 * 			String
	 * @throws Exception
	 */
	public String getSystemObjectJavaDoc() throws Exception {
		return JavaModelUtils.getJavaDoc(javaObject);
	}
	
	/**
	 * Returns a full name of the superclass
	 * @return
	 * 			String
	 * @throws Exception
	 */
	public String getSuperClassFullName() throws Exception{
		String signature = javaObject.getSuperclassName();
		String[][] rt = javaObject.resolveType(signature);
		if(rt != null && rt.length > 0 && rt[0] != null && rt[0].length == 2){
			return rt[0][0] + "." + rt[0][1];
		}
        return javaObject.getSuperclassName();
    }
	
	/**
	 * Returns the name of the superclass
	 * @return
	 * 			String
	 * @throws Exception
	 */
    public String getSuperClassSimpleName() throws Exception{
        IType superType = javaObject.getType(javaObject.getSuperclassName());
        if(superType == null){
            return null;
        }
        return superType.getElementName();
    }
    
    /**
     * Returns a member full name
     * @param member
     * 			Member for which the request is
     * @return
     * 			String
     */
    public static String getMemberFullName(IMember member) {
        String className = member.getElementName();
        String packageName = getMemberPackageName(member);
        if (packageName == null) {
            return className;
        }
        return packageName + "." + className;
    }
    
    /**
     * Returns a name of the package this member belongs to
     * @param member
     * 			Member for which the request is
     * @return
     * 			String
     */
    public static String getMemberPackageName(IMember member){
        IJavaElement parent = member.getParent();
        while (parent != null) {
            if (parent instanceof IPackageFragment) {
                return parent.getElementName();
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * Returns a list of all object methods
     * @return
     * 		A list of <code>Property</code>
     * @throws Exception
     */
	public Property[] findAllObjectMethods() throws Exception {
		ArrayList<Property> propertiesList = new ArrayList<Property>();

		for (IJavaElement element : allChildrens) {
			if (element instanceof IMethod) {
				IMethod method = (IMethod)element;
				int flags = method.getFlags();
				
				boolean addWiki = true;

				// We need:
				// 1. public methods that don't have @ignorewiki annotation
				// 2. private/protected methods that have @addwiki annotation
				if (Flags.isPublic(flags)) {
					if (isAnnotationFound(method, "ignorewiki")) {
						addWiki = false;
					}
				} else if (!isAnnotationFound(method, "addwiki")) {
					addWiki = false;
				}
				
				if (addWiki) {
					Property property = new Property();
					
					property.name = method.getElementName();
					property.type = JavaModelUtils.getMethodReturnTypeFullName(method);
					property.documentation = JavaModelUtils.getJavaDoc(method);
					
					propertiesList.add(property);
				}
			}
		}
		return propertiesList.toArray(new Property[propertiesList.size()]);
	}
	
    /**
     * Return a list of all object fields
     * @return
     * 		List of <code>Property</code>
     * @throws Exception
     */
	public Property[] findAllObjectFields() throws Exception {
		ArrayList<Property> propertiesList = new ArrayList<Property>();
		
		for (IJavaElement element : allChildrens) {
			if (element instanceof IField) {
				IField field = (IField)element;
				
				int flags = field.getFlags();
				boolean addWiki = true;

				// We need:
				// 1. public fields that don't have @ignorewiki annotation
				// 2. private/protected fields that have @addwiki annotation
				if (Flags.isPublic(flags)) {
					if (isAnnotationFound(field, "ignorewiki")) {
						addWiki = false;
					}
				} else if (!isAnnotationFound(field, "addwiki")) {
					addWiki = false;
				}
				
				if (addWiki) {
					Property property = new Property();
					property.name = field.getElementName();
					property.documentation = JavaModelUtils.getJavaDoc(field);
					
					String rawCode = field.getSource();
					property.defaultValue = "N/A";
					
					// Try to get the initial value from the source code directly
					if (rawCode != null) {
						int equalsIndex = rawCode.lastIndexOf('=');						
						if (equalsIndex != -1) {
							Matcher m = Pattern.compile("=\\s*(\\S+)[\\s|\\;]").matcher(rawCode);
							if (m.find(equalsIndex)) {
								property.defaultValue = m.group(1);
							}
						}
					}					
										
					String fieldType = field.getTypeSignature();
					property.type = Signature.toString(fieldType);
					
					propertiesList.add(property);
				}
			}
		}
			
		return propertiesList.toArray(new Property[propertiesList.size()]);
	}
		    
	/**
	 * Check if annotation is found
	 */
	private boolean isAnnotationFound(IAnnotatable type, String annotationName) {
		IAnnotation annotations[];
		try {
			annotations = type.getAnnotations();
			for (IAnnotation annotation : annotations) {
				if (annotation.getElementName().equals(annotationName)) {
					return true;
				}
			}
		} catch (JavaModelException e) {}
				
		return false;
	}
}
