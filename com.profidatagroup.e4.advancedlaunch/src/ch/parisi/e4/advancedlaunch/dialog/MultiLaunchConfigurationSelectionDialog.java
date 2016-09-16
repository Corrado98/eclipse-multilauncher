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

import java.util.HashMap;
import java.util.Iterator;

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
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
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
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

import ch.parisi.e4.advancedlaunch.EnumController;
import ch.parisi.e4.advancedlaunch.EnumController.PostLaunchAction;
import ch.parisi.e4.advancedlaunch.LaunchUtils;
import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;

/**
 * Dialog to select launch configuration(s)
 * This class was taken from CDT and was modified by the author of this project.
 */
public class MultiLaunchConfigurationSelectionDialog extends TitleAreaDialog implements ISelectionChangedListener {
	private LaunchConfigurationFilteredTree fTree;
	private ViewerFilter[] filters = null;
	private ISelection fSelection;
	private String mode = "run";
	private PostLaunchAction action = PostLaunchAction.NONE;
	private Object actionParam;
	private ViewerFilter emptyTypeFilter;
	private IStructuredSelection fInitialSelection;
	private ComboControlledStackComposite stackComposite;
	private Label paramLabel;
	private Text paramTextWidget;

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
					} catch (CoreException e) {
						return false;
					}
				} else if (element instanceof ILaunchConfiguration) {
					try {
						return LaunchUtils.isValidLaunchReference((ILaunchConfiguration) element);
					} catch (CoreException e) {
						e.printStackTrace();
					}
				}
				return true;
			}
		};
	}

	public void setForEditing(boolean editMode) {
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

		stackComposite = new ComboControlledStackComposite(comp, SWT.NONE);
		HashMap<String, ILaunchGroup> modes = new HashMap<String, ILaunchGroup>();
		LaunchConfigurationManager manager = DebugUIPlugin.getDefault().getLaunchConfigurationManager();
		ILaunchGroup[] launchGroups = manager.getLaunchGroups();
		for (ILaunchGroup launchGroup : launchGroups) {
			if (!modes.containsKey(launchGroup.getMode())) {
				modes.put(launchGroup.getMode(), launchGroup);
			}
		}
		for (Iterator<String> iterator = modes.keySet().iterator(); iterator.hasNext();) {
			String mode = iterator.next();
			ILaunchGroup launchGroup = modes.get(mode);
			fTree = new LaunchConfigurationFilteredTree(stackComposite.getStackParent(), SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.BORDER, new PatternFilter(), launchGroup, filters);
			stackComposite.addItem(mode, fTree);
			fTree.createViewControl();
			ViewerFilter[] filters = fTree.getViewer().getFilters();
			for (ViewerFilter viewerFilter : filters) {
				if (viewerFilter instanceof LaunchGroupFilter) {
					fTree.getViewer().removeFilter(viewerFilter);
				}
			}
			fTree.getViewer().addFilter(emptyTypeFilter);
			fTree.getViewer().addSelectionChangedListener(this);

			if (launchGroup.getMode().equals(this.mode)) {
				stackComposite.setSelection(mode);
			}
			if (fInitialSelection != null) {
				fTree.getViewer().setSelection(fInitialSelection, true);
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
				mode = ((Combo) e.widget).getText();
			}
		});

		createPostLaunchControl(comp);
		return comp;
	}

	private void createPostLaunchControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(4, false));
		comp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Label label = new Label(comp, SWT.NONE);
		label.setText(LaunchMessages.LaunchGroupConfigurationSelectionDialog_8);
		Combo combo = new Combo(comp, SWT.READ_ONLY);
		combo.add(EnumController.actionEnumToStr(PostLaunchAction.NONE));
		combo.add(EnumController.actionEnumToStr(PostLaunchAction.WAIT_FOR_TERMINATION));
		combo.add(EnumController.actionEnumToStr(PostLaunchAction.DELAY));
		combo.add(EnumController.actionEnumToStr(PostLaunchAction.WAIT_FOR_CONSOLESTRING));

		combo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final String actionStr = ((Combo) e.widget).getText();
				action = EnumController.strToActionEnum(actionStr);
				showHideDelayAmountWidgets();
				validate();
			}
		});

		combo.setText(EnumController.actionEnumToStr(action));

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
					} catch (NumberFormatException exc) {
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

		initActionParamDatabinding();
		showHideDelayAmountWidgets();
	}

	@SuppressWarnings("unchecked")
	private void initActionParamDatabinding() {
		// Do the actual binding and conversion
		DataBindingContext dbc = new DataBindingContext();

		// create the observables, which should be bound
		IObservableValue<Text> fDelayAmountWidgetTarget = WidgetProperties.text(SWT.Modify).observe(paramTextWidget);
		IObservableValue<Object> actionParamModel = PojoProperties.value("actionParam").observe(this);

		// bind observables together
		dbc.bindValue(fDelayAmountWidgetTarget, actionParamModel);
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
		if (fSelection != null && !fSelection.isEmpty()) {
			IStructuredSelection selection = (IStructuredSelection) fSelection;
			return (ILaunchConfiguration) selection.getFirstElement();
		}
		return null;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
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

	public LaunchConfigurationFilteredTree getfTree() {
		return fTree;
	}

	public ISelection getfSelection() {
		return fSelection;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ISelectionChangedListener#selectionChanged(org.
	 * eclipse.jface.viewers.SelectionChangedEvent)
	 */
	@Override
	public void selectionChanged(SelectionChangedEvent event) {

		// This listener gets called for a selection change in the launch
		// configuration viewer embedded in the dialog. Problem is, there are
		// numerous viewers--one for each platform debug ILaunchGroup (run,
		// debug, profile). These viewers are stacked, so only one is ever
		// visible to the user. During initialization, we get a selection change
		// notification for every viewer. We need to ignore all but the one that
		// matters--the visible one.

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

		fSelection = event.getSelection();
		validate();
	}

	protected void validate() {
		Button btnOk = getButton(IDialogConstants.OK_ID);
		boolean isValid = true;

		if (isValid) {
			if (action == PostLaunchAction.DELAY) {
				isValid = (actionParam instanceof Integer) && ((Integer) actionParam > 0);
				setErrorMessage(isValid ? null : LaunchMessages.LaunchGroupConfigurationSelectionDialog_10);
			}
			if (action == PostLaunchAction.WAIT_FOR_CONSOLESTRING) {
				if (actionParam.toString().isEmpty()) {
					isValid = false;
				}
				setErrorMessage(isValid ? null : LaunchMessages.LaunchGroupConfigurationSelectionDialog_10_2);
			}
			
			if (fSelection == null) {
				//nothing selected
				isValid = false;
			}
			
			IStructuredSelection selection = (IStructuredSelection) fSelection;
			if (selection != null) {
				if (selection.getFirstElement() instanceof ILaunchConfigurationType) {
					//launchconfigurationtype selected
					isValid = false;
				}
			}
		}

		if (btnOk != null)
			btnOk.setEnabled(isValid);
	}

	public void setInitialSelection(ILaunchConfiguration launchConfiguration) {
		fInitialSelection = new StructuredSelection(launchConfiguration);
	}
}
