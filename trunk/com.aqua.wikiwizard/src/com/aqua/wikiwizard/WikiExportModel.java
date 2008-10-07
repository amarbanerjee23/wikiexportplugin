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
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;

/**
 * Export class
 * @author Guy Arieli, Michael Oziransky
 */
public class WikiExportModel {
	private ArrayList<IType> resultsList;

	private IMember selectedMemeber = null;
	private Formater formater;

	public WikiExportModel(Formater formater) throws Exception {
		resultsList = getOptionalTypes();
		this.formater = formater;
	}
	
	/**
	 * Get all types in all projects
	 * @return
	 * 		ArrayList<IType>
	 * @throws Exception
	 */
	public ArrayList<IType> getOptionalTypes() throws Exception {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		// Get the Java model
		IJavaModel javaModel = JavaCore.create(workspace.getRoot());

		// Get all projects
		IJavaProject javaProjects[] = javaModel.getJavaProjects();
		
		ArrayList<IType> allTypes = new ArrayList<IType>();
		
		// Go over all projects and collect all types
		for (final IJavaProject proj: javaProjects) {
			collectAllTypesInProject(allTypes, proj);
		}
		return allTypes;
	}
	
	private void collectAllTypesInProject(ArrayList<IType> allTypes, IParent element) throws JavaModelException {
		IJavaElement[] elements = element.getChildren();
		
		for(IJavaElement je: elements) {
			// Skip the jars
			if (je.isReadOnly()){
				continue;
			}
			
			// End of the road, we found a type
			if (je instanceof IType) {
				allTypes.add((IType)je);
				continue;
			}
			
			// There are more, enter recursion
			if (je instanceof IParent) {
				collectAllTypesInProject(allTypes, (IParent)je);
			}
		}
	}

	/**
	 * Sets the selected by the user member
	 * @param member
	 * 			IMember
	 */
	public void setSelectedMember(IMember member) {
		this.selectedMemeber = member;
	}

	/**
	 * Returns the wiki content for chosen member
	 * @return
	 * 			String
	 * @throws Exception
	 */
	public String getWikiContent() throws Exception {
		if (selectedMemeber == null) {
			return null;
		}
		ObjectsJavaModel javaModel = new ObjectsJavaModel(
				(IType) selectedMemeber);
		StringBuffer buf = new StringBuffer();

		// add the header
		buf.append(formater.h1(javaModel.getShortName() + " System Object"));
		
		buf.append(formater.bullet()); 
		buf.append(formater.formatText("Super class: "));
		buf.append(formater.link(javaModel.getSuperClassFullName(), javaModel.getSuperClassSimpleName()));
		buf.append(formater.lineBreak());

		buf.append(formater.h2("Documentation"));
		buf.append(formater.formatText(javaModel.getSystemObjectJavaDoc()));
		buf.append(formater.lineBreak());
		
		Property[] fields = javaModel.findAllObjectFields();
		
		if (fields != null && fields.length > 0) {
			buf.append(formater.h2("Public fields"));
			for (Property property: fields) {
				buf.append(formater.h3(property.name));
				
				buf.append(formater.formatText(property.documentation));
				buf.append(formater.lineBreak());
				buf.append(formater.tab());
				buf.append(formater.bold("Type: "));
				buf.append(formater.formatText(property.type));
				buf.append(formater.lineBreak());
				buf.append(formater.tab());
				buf.append(formater.bold("Default Value: "));
				buf.append(formater.formatText(property.defaultValue));
				buf.append(formater.lineBreak());
			}
		}		

		buf.append(formater.h2("Public Methods"));
		Property[] properties = javaModel.findAllObjectMethods();

		for (Property property : properties) {
			buf.append(formater.h3(property.name));
			
			buf.append(formater.formatText(property.documentation));
			buf.append(formater.lineBreak());
		}

		return buf.toString();
	}

	/**
	 * Returns the matching results
	 * 
	 * @return ArrayList containing strings
	 */
	public ArrayList<IType> getObjects() {
		return resultsList;
	}
}
