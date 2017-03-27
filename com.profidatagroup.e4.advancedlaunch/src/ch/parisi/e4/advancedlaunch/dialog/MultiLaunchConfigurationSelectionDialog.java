/*******************************************************************************
 *  Copyright (c) 2009, 2016 QNX Software Systems and others.
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

import java.util.Map;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.beans.PojoProperties;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.internal.ui.DebugUIPlugin;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationFilteredTree;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationManager;
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchGroupFilter;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.jface.databinding.swt.WidgetProperties;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;
import ch.parisi.e4.advancedlaunch.utils.LaunchUtils;
import ch.parisi.e4.advancedlaunch.utils.PostLaunchAction;
import ch.parisi.e4.advancedlaunch.utils.PostLaunchActionUtils;

/**
 * Dialog to select launch configuration(s)
 * This class was taken from CDT and was modified by the author of this project.
 */
public class MultiLaunchConfigurationSelectionDialog extends TitleAreaDialog {
	//implements Listener destroys encapsulation, because anyone could add this class as a Listener! 

	private ViewerFilter[] filters = null;
	private ISelection currentSelection;
	private String launchMode = ILaunchManager.RUN_MODE;
	private PostLaunchAction action = PostLaunchAction.NONE;
	private boolean abortLaunchOnError = false;
	private Object actionParam;
	private ViewerFilter emptyTypeFilter;
	private IStructuredSelection fInitialSelection;
	private ComboControlledStackComposite stackComposite;
	private Label paramLabel;
	private Button abortLaunchOnErrorCheckbox;
	private Text paramTextWidget;

	/**
	 * <code>true</code> if the OK-button (btnOk) is enabled.
	 */
	private boolean isValid;

	/**
	 * <code>true</code> if the dialog was opened to <b>edit</b> an entry,
	 * <code>false</code> if it was opened to <b>add</b> an entry.
	 */
	private boolean editMode;

	public MultiLaunchConfigurationSelectionDialog(Shell shell) {
		super(shell);
		LaunchConfigurationManager manager = DebugUIPlugin.getDefault().getLaunchConfigurationManager();
		ILaunchGroup[] launchGroups = manager.getLaunchGroups();
		filters = null;
		setShellStyle(getShellStyle() | SWT.RESIZE);
		createEmptyTypeFilter();
	}

	private void createEmptyTypeFilter() {
		emptyTypeFilter = new ViewerFilter() {
			@Override
			public boolean select(Viewer viewer, Object parentElement, Object element) {
				if (element instanceof ILaunchConfigurationType) {
					try {
						ILaunchConfigurationType type = (ILaunchConfigurationType) element;
						return getLaunchManager().getLaunchConfigurations(type).length > 0;
					}
					catch (CoreException e) {
						return false;
					}
				}
				else if (element instanceof ILaunchConfiguration) {
					try {
						return LaunchUtils.isValidLaunchReference((ILaunchConfiguration) element);
					}
					catch (CoreException e) {
						e.printStackTrace();
					}
				}
				return true;
			}
		};
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	protected ILaunchManager getLaunchManager() {
		return DebugPlugin.getDefault().getLaunchManager();
	}

	@Override
	protected Control createContents(Composite parent) {
		Control x = super.createContents(parent);
		validate();
		setErrorMessage(null);
		return x;
	}

	@Override
	protected Control createDialogArea(Composite parent2) {
		Composite comp = (Composite) super.createDialogArea(parent2);

		// title bar
		getShell().setText(editMode ? LaunchMessages.LaunchGroupConfigurationSelectionDialog_13 : LaunchMessages.LaunchGroupConfigurationSelectionDialog_12);

		// dialog message area (not title bar)
		setTitle(editMode ? LaunchMessages.LaunchGroupConfigurationSelectionDialog_15 : LaunchMessages.LaunchGroupConfigurationSelectionDialog_14);

		stackComposite = new ComboControlledStackComposite(comp, SWT.NONE, this);

		Map<String, ILaunchGroup> modes = LaunchUtils.getModesMap();

		for (String mode : modes.keySet()) {
			ILaunchGroup launchGroup = modes.get(mode);
			LaunchConfigurationFilteredTree tree = new LaunchConfigurationFilteredTree(
					stackComposite.getStackParent(),
					SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER,
					new PatternFilter(),
					launchGroup,
					filters);
			stackComposite.addItem(mode, tree);
			tree.createViewControl();
			ViewerFilter[] filters = tree.getViewer().getFilters();
			for (ViewerFilter viewerFilter : filters) {
				if (viewerFilter instanceof LaunchGroupFilter) {
					tree.getViewer().removeFilter(viewerFilter);
				}
			}
			tree.getViewer().addFilter(emptyTypeFilter);
			tree.getViewer().addSelectionChangedListener(new SelectionChangedListener());
			tree.getViewer().addDoubleClickListener(new DoubleClickListener());
			if (mode.equals(this.launchMode)) {
				stackComposite.setSelection(mode);
			}
			if (fInitialSelection != null) {
				tree.getViewer().setSelection(fInitialSelection, true);
			}
		}
		stackComposite.setLabelText(LaunchMessages.LaunchGroupConfigurationSelectionDialog_4);
		stackComposite.pack();
		Rectangle bounds = stackComposite.getBounds();
		// adjust size
		GridData data = ((GridData) stackComposite.getLayoutData());
		if (data == null) {
			data = new GridData(GridData.FILL_BOTH);
			stackComposite.setLayoutData(data);
		}
		data.heightHint = Math.max(convertHeightInCharsToPixels(15), bounds.height);
		data.widthHint = Math.max(convertWidthInCharsToPixels(40), bounds.width);
		stackComposite.getCombo().addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				launchMode = ((Combo) e.widget).getText();
			}
		});

		createPostLaunchControl(comp);
		createAbortOnExceptionControl(comp);

		initActionParamDatabinding();
		initAbortLaunchOnErrorDatabinding();
		showHideDelayAmountWidgets();

		return comp;
	}

	private void createPostLaunchControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(4, false));
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label label = new Label(comp, SWT.NONE);
		label.setText(LaunchMessages.LaunchGroupConfigurationSelectionDialog_8);
		Combo combo = new Combo(comp, SWT.READ_ONLY);
		combo.add(PostLaunchActionUtils.convertToName(PostLaunchAction.NONE));
		combo.add(PostLaunchActionUtils.convertToName(PostLaunchAction.WAIT_FOR_TERMINATION));
		combo.add(PostLaunchActionUtils.convertToName(PostLaunchAction.DELAY));
		combo.add(PostLaunchActionUtils.convertToName(PostLaunchAction.WAIT_FOR_CONSOLESTRING));

		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final String actionStr = ((Combo) e.widget).getText();
				action = PostLaunchActionUtils.convertToPostLaunchAction(actionStr);
				if (action == PostLaunchAction.NONE || action == PostLaunchAction.WAIT_FOR_TERMINATION) {
					paramTextWidget.setText("");
				}
				showHideDelayAmountWidgets();
				validate();
			}
		});

		combo.setText(PostLaunchActionUtils.convertToName(action));

		paramLabel = new Label(comp, SWT.NONE);
		paramLabel.setText(LaunchMessages.LaunchGroupConfigurationSelectionDialog_9);

		paramTextWidget = new Text(comp, SWT.SINGLE | SWT.BORDER);

		GridData gridData = new GridData();
		gridData.widthHint = convertWidthInCharsToPixels(20);
		paramTextWidget.setLayoutData(gridData);
		paramTextWidget.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String userInput = ((Text) e.widget).getText();
				if (action == PostLaunchAction.DELAY) {
					try {
						actionParam = Integer.valueOf(userInput);
					}
					catch (NumberFormatException exc) {
						actionParam = null;
					}
					validate();
				}
				if (action == PostLaunchAction.WAIT_FOR_CONSOLESTRING) {
					actionParam = userInput;
					validate();
				}
			}
		});

		if (actionParam instanceof Integer) {
			paramTextWidget.setText(((Integer) actionParam).toString());
		}
	}

	private void createAbortOnExceptionControl(Composite composite) {
		abortLaunchOnErrorCheckbox = new Button(composite, SWT.CHECK);
		abortLaunchOnErrorCheckbox.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		abortLaunchOnErrorCheckbox.setText("Abort multilaunch on exception");
		abortLaunchOnErrorCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("checked");
			}
		});
	}

	/**
	 * IObservableValue is not parameterized '<>' to make the plugin run on Eclipse Luna release as well. 
	 */
	@SuppressWarnings("unchecked")
	private void initActionParamDatabinding() {
		// Do the actual binding and conversion
		DataBindingContext dbc = new DataBindingContext();

		// create the observables, which should be bound
		IObservableValue fDelayAmountWidgetTarget = WidgetProperties.text(SWT.Modify).observe(paramTextWidget);
		IObservableValue actionParamModel = PojoProperties.value("actionParam").observe(this);

		// bind observables together
		dbc.bindValue(fDelayAmountWidgetTarget, actionParamModel);
	}

	/**
	 * IObservableValue is not parameterized '<>' to make the plugin run on Eclipse Luna release as well. 
	 */
	@SuppressWarnings("unchecked")
	private void initAbortLaunchOnErrorDatabinding() {
		// Do the actual binding and conversion
		DataBindingContext dbc = new DataBindingContext();

		// create the observables, which should be bound
		IObservableValue abortLaunchOnErrorCheckboxTarget = WidgetProperties.selection().observe(abortLaunchOnErrorCheckbox);
		IObservableValue abortLaunchOnErrorModel = PojoProperties.value("abortLaunchOnError").observe(this);

		// bind observables together
		dbc.bindValue(abortLaunchOnErrorCheckboxTarget, abortLaunchOnErrorModel);
	}

	public Text getFDelayAmountWidget() {
		return paramTextWidget;
	}

	private void showHideDelayAmountWidgets() {
		switch (action) {
			case DELAY:
				paramLabel.setText("Seconds:");
				paramLabel.setVisible(true);
				paramTextWidget.setVisible(true);
				break;
			case WAIT_FOR_CONSOLESTRING:
				paramLabel.setText("RegEx:");
				paramLabel.setVisible(true);
				paramTextWidget.setVisible(true);
				break;
			case WAIT_FOR_TERMINATION:
				paramLabel.setVisible(false);
				paramTextWidget.setVisible(false);
				break;
			case NONE:
				paramLabel.setVisible(false);
				paramTextWidget.setVisible(false);
				break;
		}
	}

	public ILaunchConfiguration getSelectedLaunchConfiguration() {
		if (currentSelection != null && !currentSelection.isEmpty()) {
			IStructuredSelection selection = (IStructuredSelection) currentSelection;
			return (ILaunchConfiguration) selection.getFirstElement();
		}
		return null;
	}

	public void setInitialSelection(ILaunchConfiguration launchConfiguration) {
		fInitialSelection = new StructuredSelection(launchConfiguration);
	}

	private class SelectionChangedListener implements ISelectionChangedListener {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			// This listener gets called for a selection change in the launch
			// configuration viewer embedded in the dialog. Problem is, there are
			// numerous viewers--one for each platform debug ILaunchGroup (run,
			// debug, profile). These viewers are stacked, so only one is ever
			// visible to the user. During initialization, we get a selection change
			// notification for every viewer. We need to ignore all but the one that
			// matters--the visible one.
			//fInitialSelection = null;
			//System.out.println(getTree().getViewer().getSelection().toString());
			Tree topTree = null;
			final Control topControl = stackComposite.getTopControl();
			if (topControl instanceof FilteredTree) {
				final TreeViewer viewer = ((FilteredTree) topControl).getViewer();
				if (viewer != null) {
					topTree = viewer.getTree();
				}
			}
			if (topTree == null) {
				return;
			}
			boolean selectionIsForVisibleViewer = false;
			final Object src = event.getSource();
			if (src instanceof Viewer) {
				final Control viewerControl = ((Viewer) src).getControl();
				if (viewerControl == topTree) {
					selectionIsForVisibleViewer = true;
				}
			}
			if (!selectionIsForVisibleViewer) {
				return;
			}
			currentSelection = event.getSelection();
			validate();
		}
	}

	private class DoubleClickListener implements IDoubleClickListener {
		@Override
		public void doubleClick(DoubleClickEvent event) {
			// this method catches ENTER-PRESSED as well.
			validate();
			if (isValid) {
				okPressed();
			}
		}
	}

	protected void validate() {
		Button btnOk = getButton(IDialogConstants.OK_ID);
		isValid = true;

		if (isValid) {
			setErrorMessage(null);
			if (action == PostLaunchAction.DELAY) {
				try {
					isValid = ((Integer.parseInt(actionParam.toString()) > 0));
				}
				catch (Exception e) {
					isValid = false;
					setErrorMessage(isValid ? null : LaunchMessages.LaunchGroupConfigurationSelectionDialog_10);
				}
			}
			else if (action == PostLaunchAction.WAIT_FOR_CONSOLESTRING) {
				isValid = (!String.valueOf(actionParam).trim().isEmpty());
				setErrorMessage(isValid ? null : LaunchMessages.LaunchGroupConfigurationSelectionDialog_10_2);
			}
			if (currentSelection == null) {
				isValid = false;
			}
			else {
				IStructuredSelection selection = (IStructuredSelection) currentSelection;
				if (selection.getFirstElement() instanceof ILaunchConfigurationType) {
					isValid = false;
					setErrorMessage(isValid ? null : LaunchMessages.LaunchGroupConfiguration_NotALaunchConfiguration);
				}
			}
		}

		if (btnOk != null) {
			btnOk.setEnabled(isValid);
		}
	}

	public String getMode() {
		return launchMode;
	}

	public void setMode(String mode) {
		this.launchMode = mode;
	}

	public PostLaunchAction getAction() {
		return action;
	}

	public void setAction(PostLaunchAction action) {
		this.action = action;
	}

	public Object getActionParam() {
		return actionParam;
	}

	public void setActionParam(String actionParam) {
		this.actionParam = actionParam;
	}

	public boolean isAbortLaunchOnError() {
		return abortLaunchOnError;
	}

	public void setAbortLaunchOnError(boolean abortLaunchOnError) {
		this.abortLaunchOnError = abortLaunchOnError;
	}

	public void setCurrentSelection(ISelection currentSelection) {
		this.currentSelection = currentSelection;
	}
}
