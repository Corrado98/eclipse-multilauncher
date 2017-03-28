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
import org.eclipse.debug.internal.ui.launchConfigurations.LaunchConfigurationFilteredTree;
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
// LaunchConfigurationFilteredTree is required to get the dialog with the ILaunchConfigurations and launch icons.
// The LaunchGroupFilter provides launch filtering out of the box. 
@SuppressWarnings("restriction")
public class MultiLaunchConfigurationSelectionDialog extends TitleAreaDialog {
	//implements Listener destroys encapsulation, because anyone could add this class as a Listener! 

	private IStructuredSelection initialSelection;
	private ISelection currentSelection;
	private ViewerFilter emptyTypeFilter;
	private ComboControlledStackComposite stackComposite;

	private String launchMode = ILaunchManager.RUN_MODE;
	private PostLaunchAction postLaunchAction = PostLaunchAction.NONE;

	private Object param;
	private Label paramLabel;
	private Text paramTextWidget;

	private boolean abortLaunchOnError = false;
	private Button abortLaunchOnErrorCheckbox;

	/**
	 * <code>true</code> if the OK-button (btnOk) is enabled.
	 */
	private boolean isValid;

	/**
	 * <code>true</code> if the dialog was opened to <b>edit</b> an entry,
	 * <code>false</code> if it was opened to <b>add</b> an entry.
	 */
	private boolean editMode;

	/**
	 * Constructs a {@link MultiLaunchConfigurationSelectionDialog}.
	 * 
	 * @param shell the shell
	 */
	public MultiLaunchConfigurationSelectionDialog(Shell shell) {
		super(shell);
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

	/**
	 * Sets the dialog's edit mode.
	 * 
	 * @param editMode {@code true} if in edit mode, otherwise {@code false}
	 */
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
	protected Control createDialogArea(Composite parent) {
		Composite comp = (Composite) super.createDialogArea(parent);

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
					null);
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
			if (initialSelection != null) {
				tree.getViewer().setSelection(initialSelection, true);
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
		showParamTextWidgetConditionally();

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
				postLaunchAction = PostLaunchActionUtils.convertToPostLaunchAction(actionStr);
				if (postLaunchAction == PostLaunchAction.NONE || postLaunchAction == PostLaunchAction.WAIT_FOR_TERMINATION) {
					paramTextWidget.setText("");
				}
				showParamTextWidgetConditionally();
				validate();
			}
		});

		combo.setText(PostLaunchActionUtils.convertToName(postLaunchAction));

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
				if (postLaunchAction == PostLaunchAction.DELAY) {
					try {
						param = Integer.valueOf(userInput);
					}
					catch (NumberFormatException exc) {
						param = null;
					}
					validate();
				}
				if (postLaunchAction == PostLaunchAction.WAIT_FOR_CONSOLESTRING) {
					param = userInput;
					validate();
				}
			}
		});

		if (param instanceof Integer) {
			paramTextWidget.setText(((Integer) param).toString());
		}
	}

	private void createAbortOnExceptionControl(Composite composite) {
		abortLaunchOnErrorCheckbox = new Button(composite, SWT.CHECK);
		abortLaunchOnErrorCheckbox.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
		abortLaunchOnErrorCheckbox.setText("Abort multilaunch on exception");
	}

	//IObservableValue is not parameterized '<>' to make the plugin run on Eclipse Luna release as well.
	@SuppressWarnings("rawtypes")
	private void initActionParamDatabinding() {
		// Do the actual binding and conversion
		DataBindingContext dbc = new DataBindingContext();

		// create the observables, which should be bound
		IObservableValue fDelayAmountWidgetTarget = WidgetProperties.text(SWT.Modify).observe(paramTextWidget);
		IObservableValue actionParamModel = PojoProperties.value("actionParam").observe(this);

		// bind observables together
		dbc.bindValue(fDelayAmountWidgetTarget, actionParamModel);
	}

	//IObservableValue is not parameterized '<>' to make the plugin run on Eclipse Luna release as well.
	@SuppressWarnings("rawtypes")
	private void initAbortLaunchOnErrorDatabinding() {
		// Do the actual binding and conversion
		DataBindingContext dbc = new DataBindingContext();

		// create the observables, which should be bound
		IObservableValue abortLaunchOnErrorCheckboxTarget = WidgetProperties.selection().observe(abortLaunchOnErrorCheckbox);
		IObservableValue abortLaunchOnErrorModel = PojoProperties.value("abortLaunchOnError").observe(this);

		// bind observables together
		dbc.bindValue(abortLaunchOnErrorCheckboxTarget, abortLaunchOnErrorModel);
	}

	/**
	 * Gets the parameter text widget.
	 * 
	 * @return the parameter {@link Text} widget.
	 */
	public Text getParamTextWidget() {
		return paramTextWidget;
	}

	private void showParamTextWidgetConditionally() {
		switch (postLaunchAction) {
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

	/**
	 * Gets the selected launch configuration. 
	 * 
	 * @return the selected {@code ILaunchConfiguration} or null 
	 * if {@link #currentSelection} is empty or null.
	 */
	public ILaunchConfiguration getSelectedLaunchConfiguration() {
		if (currentSelection != null || !currentSelection.isEmpty()) {
			IStructuredSelection selection = (IStructuredSelection) currentSelection;
			return (ILaunchConfiguration) selection.getFirstElement();
		}
		return null;
	}

	/**
	 * Sets the dialog's initial selection to the specified {@link ILaunchConfiguration}.
	 * 
	 * @param launchConfiguration the launch configuration
	 */
	public void setInitialSelection(ILaunchConfiguration launchConfiguration) {
		initialSelection = new StructuredSelection(launchConfiguration);
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
			//initialSelection = null;
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
			if (postLaunchAction == PostLaunchAction.DELAY) {
				try {
					isValid = ((Integer.parseInt(param.toString()) > 0));
				}
				catch (Exception e) {
					isValid = false;
					setErrorMessage(isValid ? null : LaunchMessages.LaunchGroupConfigurationSelectionDialog_10);
				}
			}
			else if (postLaunchAction == PostLaunchAction.WAIT_FOR_CONSOLESTRING) {
				isValid = (!String.valueOf(param).trim().isEmpty());
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

	/**
	 * Gets the dialog's launch mode.
	 * 
	 * @return the launch mode
	 */
	public String getMode() {
		return launchMode;
	}

	/**
	 * Sets the dialog's launch mode. 
	 * 
	 * @param mode the launch mode
	 */
	public void setMode(String mode) {
		this.launchMode = mode;
	}

	/**
	 * Gets the dialog's {@link PostLaunchAction}.
	 * 
	 * @return the launch mode
	 */
	public PostLaunchAction getPostLaunchAction() {
		return postLaunchAction;
	}

	/**
	 * Sets the dialog's {@link PostLaunchAction}.
	 * 
	 * @param action the post launch action
	 */
	public void setPostLaunchAction(PostLaunchAction action) {
		this.postLaunchAction = action;
	}

	/**
	 * Gets the dialog's parameter. 
	 * 
	 * @return the launch mode
	 */
	public Object getParam() {
		return param;
	}

	/**
	 * Sets the dialog's parameter
	 * 
	 * @param actionParam the parameter
	 */
	public void setParam(String actionParam) {
		this.param = actionParam;
	}

	/**
	 * Gets a dialog's {@link #abortLaunchOnError} flag.
	 * 
	 * @return {@code true} when a multilaunch must abort on error {@code false} when launching continues.
	 */
	public boolean isAbortLaunchOnError() {
		return abortLaunchOnError;
	}

	/**
	 * Sets the dialog's {@link #abortLaunchOnError} flag.
	 * 
	 * @param abortLaunchOnError {@code true} when multilaunch must abort on error {@code false} when launching should continue.
	 */
	public void setAbortLaunchOnError(boolean abortLaunchOnError) {
		this.abortLaunchOnError = abortLaunchOnError;
	}

	/**
	 * Sets the dialog's current selection. 
	 * 
	 * @param currentSelection the current selection
	 */
	public void setCurrentSelection(ISelection currentSelection) {
		this.currentSelection = currentSelection;
	}
}
