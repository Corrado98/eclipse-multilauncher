/*******************************************************************************
 *  Copyright (c) 2009 QNX Software Systems and others.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      QNX Software Systems - initial API and implementation
 *      Freescale Semiconductor
 *******************************************************************************/
package ch.parisi.e4.advancedlaunch.dialog;

import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

/**
 * Stack Composite - Switch between panes controlled by combo box
 * 
 * @since 6.0
 * 
 * This class was taken from CDT in order to make the
 * {@link MultiLaunchConfigurationSelectionDialog} work.
 */
public class ComboControlledStackComposite extends Composite {
	private Composite fArea;
	private Combo fCombo;
	private Map<String, Composite> tabMap; // label ==> tab
	private StackLayout layout;
	private Label fLabel;
	private MultiLaunchConfigurationSelectionDialog multiLaunchConfigurationSelectionDialog;

	/**
	 * Constructs a {@link ComboControlledStackComposite}.
	 * 
	 * @param parent the parent composite
	 * @param style the style
	 * @param dialog the dialog
	 */
	public ComboControlledStackComposite(Composite parent, int style, MultiLaunchConfigurationSelectionDialog dialog) {
		super(parent, style);
		multiLaunchConfigurationSelectionDialog = dialog;
		tabMap = new LinkedHashMap<String, Composite>();
		setLayout(new GridLayout(2, false));
		createContents(this);
	}

	/**
	 * Sets this composite's label text
	 * 
	 * @param label the label text
	 */
	public void setLabelText(String label) {
		fLabel.setText(label);
	}

	/**
	 * Adds a specified label to a combobox and tabMap.
	 * 
	 * The specified tab is only added to the tabMap.	 
	 * 
	 * @param label the label text
	 * @param tab the tab composite
	 */
	public void addItem(String label, Composite tab) {
		tabMap.put(label, tab);
		fCombo.add(label);
		if (layout.topControl == null) {
			layout.topControl = tab;
			fCombo.setText(label);
		}
	}

	/**
	 * Deleted an item with the specified label. 
	 * 
	 * @param label the label text
	 */
	public void deleteItem(String label) {
		if (fCombo.getText().equals(label)) {
			setSelection(fCombo.getItem(0));
		}
		Composite tab = tabMap.get(label);
		if (tab != null) {
			tab.dispose();
			tabMap.remove(label);
		}
	}

	/**
	 * Sets the combobox's selection by the specified label.
	 * 
	 * @param label the label text
	 */
	public void setSelection(String label) {
		fCombo.setText(label);
		setPage(label);
	}

	protected void createContents(Composite parent) {
		fLabel = createLabel(this);
		fCombo = createCombo(this);
		GridData cgd = new GridData(GridData.FILL_HORIZONTAL);

		fCombo.setLayoutData(cgd);
		fArea = createTabArea(this);
		GridData agd = new GridData(GridData.FILL_BOTH);
		agd.horizontalSpan = 2;
		fArea.setLayoutData(agd);
	}

	/**
	 * Gets this Composite's stack parent.
	 * 
	 * @return the parent composite
	 */
	public Composite getStackParent() {
		return fArea;
	}

	/**
	 * Gets this composite's label.
	 * 
	 * @return the label
	 */
	public Label getLabel() {
		return fLabel;
	}

	/**
	 * Gets this composite's Combobox.
	 * 
	 * @return the combobox
	 */
	public Combo getCombo() {
		return fCombo;
	}

	protected Composite createTabArea(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		layout = new StackLayout();
		comp.setLayout(layout);

		return comp;
	}

	protected Label createLabel(Composite parent) {
		Label label = new Label(parent, SWT.WRAP);
		return label;
	}

	protected Combo createCombo(Composite parent) {
		Combo box = new Combo(parent, SWT.READ_ONLY);
		box.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				String name = fCombo.getText();
				comboSelected(name);
				multiLaunchConfigurationSelectionDialog.setCurrentSelection(null);
				multiLaunchConfigurationSelectionDialog.validate();
			}
		});
		return box;
	}

	protected void comboSelected(String label) {
		setPage(label);
	}

	protected void setPage(String label) {
		layout.topControl = tabMap.get(label);
		getStackParent().layout();
	}

	/**
	 * Gets this composite's top control. 
	 * 
	 * @return the top control if {@link ComboControlledStackComposite#layout} is not null. Otherwise returns null.
	 */
	public Control getTopControl() {
		return layout != null ? layout.topControl : null;
	}
}
