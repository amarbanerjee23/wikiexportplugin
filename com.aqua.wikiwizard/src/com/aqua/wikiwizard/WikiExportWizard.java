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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IOConsole;

/**
 * Wizard class
 * @author Guy Arieli, Michael Oziransky
 */
public class WikiExportWizard extends Wizard implements INewWizard
{
	public static final String copyright = "(c) Copyright AQUA Software 2008.";
	
	// Wizard pages
	SystemObjectPage objectListPage;
	
	// Workbench selection when the wizard was started
	protected IStructuredSelection selection;
	
	// The workbench instance
	protected IWorkbench workbench;
	
	// The model
	protected WikiExportModel exportModel;

	/**
	 * Constructor for WikiExportWizard
	 */
	public WikiExportWizard() {
		super();
	}
	
	public void addPages()
	{
		objectListPage = new SystemObjectPage(workbench, selection);
		addPage(objectListPage);
	}

	/**
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	public void init(IWorkbench workbench, IStructuredSelection selection) 
	{
		this.workbench = workbench;
		this.selection = selection;
		
		try {
			exportModel = new WikiExportModel(new WikiFormater());
		} catch (Exception e) {}
	}

	public boolean performFinish() 
	{
		IOConsole console = new IOConsole("Wiki",null);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{console});
		console.activate();
		try {
			String content = exportModel.getWikiContent();
			console.newOutputStream().write(content);
			Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		    clipboard.setContents( new StringSelection( content ), null );

		} catch (Exception e) {}

		return true;
	}
	
	public WikiExportModel getModel() {
		return exportModel;
	}
}
