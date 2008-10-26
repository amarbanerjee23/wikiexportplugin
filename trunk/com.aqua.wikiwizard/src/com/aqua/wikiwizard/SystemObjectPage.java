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

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IWorkbench;

/**
 * Represents the first page of the wizard
 * @author Guy Arieli, Michael Oziransky
 */
public class SystemObjectPage extends WizardPage implements Listener,
		SelectionListener, KeyListener {
	public static final String copyright = "(c) Copyright AQUA Software 2008.";

	public IWorkbench workbench;
	public IStructuredSelection selection;
	private Table table;

	public SystemObjectPage(IWorkbench workbench, IStructuredSelection selection) {
		super("Page 1");
		setTitle("System objects");
		setDescription("Available system objects");
		this.workbench = workbench;
		this.selection = selection;
		setPageComplete(false);
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	public void createControl(Composite parent) {

		// create the composite to hold the widgets
		Composite composite = new Composite(parent, SWT.NULL);

		// create the desired layout for this wizard page
		composite.setLayout(new FillLayout());
		final WikiExportWizard wizard = (WikiExportWizard) getWizard();

		table = new Table(composite, SWT.VIRTUAL | SWT.V_SCROLL | SWT.SINGLE);
		table.setItemCount(wizard.exportModel.getObjects().size());
		table.addListener(SWT.SetData, new Listener() {
			public void handleEvent(Event event) {
				TableItem item = (TableItem) event.item;
				int index = table.indexOf(item);
				item.setText(getMemberName(wizard.exportModel.getObjects().get(
						index)));
			}
		});
		table.addSelectionListener(this);
		table.addKeyListener(this);

		// Set the composite as the control for this page
		setControl(composite);
	}

	private static String getMemberName(IMember member) {
		String className = member.getElementName();
		String packageName = null;
		IJavaElement parent = member.getParent();
		while (parent != null) {
			if (parent instanceof IPackageFragment) {
				packageName = parent.getElementName();
				break;
			}
			parent = parent.getParent();
		}
		if (packageName == null) {
			return className;
		}
		
		// Build the name for easy sorting
		return className + " - " + packageName + "." + className;
	}

	/**
	 * @see Listener#handleEvent(Event)
	 */
	public void handleEvent(Event event) {
		getWizard().getContainer().updateButtons();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {}

	@Override
	public void widgetSelected(SelectionEvent e) {
		int currentSelection = table.getSelectionIndex();
		((WikiExportWizard) getWizard()).exportModel
				.setSelectedMember(((WikiExportWizard) getWizard()).exportModel
						.getObjects().get(currentSelection));
		setPageComplete(true);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		WikiExportWizard wizard = (WikiExportWizard) getWizard();
		int index = wizard.exportModel.getObjectByLetter("" + e.character);
		if (index != -1) {
			table.setSelection(index);
			wizard.exportModel.setSelectedMember(wizard.exportModel.
					getObjects().get(index));
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {}
}
