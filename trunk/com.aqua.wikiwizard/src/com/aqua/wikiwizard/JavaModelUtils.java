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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.Signature;
import org.eclipse.jdt.internal.ui.text.javadoc.JavadocContentAccess2;

/**
 * Utilities
 * @author Guy Arieli, Michael Oziransky
 */
public class JavaModelUtils {
	
	/**
	 * Returns the name of the class
	 * @param target
	 * 			The given type
	 * @return
	 * 			String
	 * @throws Exception
	 */
	public static String getClassName(IType target) throws Exception {
		String signature = target.getElementName();
		String[][] rt = target.resolveType(signature);
		if (rt != null && rt.length > 0 && rt[0] != null && rt[0].length == 2) {
			return rt[0][0] + "." + rt[0][1];
		}
		return null;
	}
	
	/**
	 * Returns the javadoc for given type. Performs some cleaning on the javadoc
	 * @param member
	 * 			The given type
	 * @return
	 * 			String
	 * @throws JavaModelException
	 */
	public static String getJavaDoc(IMember member) throws JavaModelException{
		String doc = JavadocContentAccess2.getHTMLContent(member, true, true);
		
		if (doc == null) {
			return null;
		}
		
		// Change to \n
		String[] lines = doc.split("\r\n|\r|\n");
		StringBuffer buf = new StringBuffer();
		for (String line: lines) {
			buf.append(line.trim());
			buf.append("\n");
		}
		
		// Remove all the links from the doc
		String newDoc = buf.toString();
		Matcher m = Pattern.compile("\\<a.*\\>(\\S+)\\</a\\>").matcher(newDoc);
		if (m.find()) {
			newDoc = m.replaceAll(m.group(1));
		}		

		return newDoc;		
	}
	
	/**
	 * Returns the method return type
	 * @param method
	 * 			The given method
	 * @return
	 * 			String
	 * @throws JavaModelException
	 */
	public static String getMethodReturnTypeFullName(IMethod method) throws JavaModelException{
		String signature = method.getReturnType();
		String packageName = Signature.getSignatureQualifier(signature);
		return (packageName.trim().equals("")?"":packageName+".") + Signature.getSignatureSimpleName(signature);
	}
}
