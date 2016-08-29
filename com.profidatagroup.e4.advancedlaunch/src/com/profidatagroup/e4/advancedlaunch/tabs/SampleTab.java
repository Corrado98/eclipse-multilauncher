package com.profidatagroup.e4.advancedlaunch.tabs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.launch.internal.ui.MultiLaunchConfigurationSelectionDialog;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.core.LaunchConfiguration;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;

import com.profidatagroup.e4.advancedlaunch.Activator;
import com.profidatagroup.e4.advancedlaunch.SampleLaunchConfigurationAttributes;

/**
 * 
 * @author PaCo
 */
public class SampleTab extends AbstractLaunchConfigurationTab {

	private Text text;
	private Button btnAdd;
	private Button btnRemove;
	private Button btnUp;
	private Button btnDown;
	private TableViewer viewer;
	private String selectedConfiguration;
	private Composite comp; // can be local in create control.
	public static List<String> configurationNameList = new ArrayList<>();

	@Override
	public void createControl(Composite parent) {
		// isFileAlreadyCreated(file);
		initComp(parent);
		createAddBtnWithListener();
		initTableViewer();
		createRemoveButtonWithListener();
		insertPlaceHolder();
		createUpButtonWithListener();
		insertPlaceHolder();
		createDownButtonWithListener();
	}

	private void initComp(Composite parent) {
		comp = new Group(parent, SWT.BORDER);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		comp.setLayout(new FillLayout());
		setControl(comp);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(comp);
	}

	private void initTableViewer() {
		viewer = new TableViewer(comp, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		Table table3 = viewer.getTable();
		table3.setLayoutData(new GridData(GridData.FILL_BOTH));

		// gets user selected element in the table and works with it.
		addTableViewerSelectionChangedListener();

		// set the content provider
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		// create the columns
		createColumns(viewer);

		// make lines and header visible
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private void createAddBtnWithListener() {
		btnAdd = new Button(comp, SWT.None);
		btnAdd.setText("Add..");
		btnAdd.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				MultiLaunchConfigurationSelectionDialog multiLaunchConfigurationSelectionDialog = new MultiLaunchConfigurationSelectionDialog(
						getShell(), "debug", false);
				if (multiLaunchConfigurationSelectionDialog.open() == Window.OK) {
					// gets selected launchconfigurations from chooser
					// (selection dialog)
					for (ILaunchConfiguration a : multiLaunchConfigurationSelectionDialog
							.getSelectedLaunchConfigurations()) {
						System.out.println(a.getName());
						configurationNameList.add(a.getName());
					}
					if (configurationNameList != null) {
						viewer.setInput(configurationNameList);
						viewer.refresh();
						setDirty(true);
						updateLaunchConfigurationDialog();
					}

				}
			}
		});

	}

	private void addTableViewerSelectionChangedListener() {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = viewer.getStructuredSelection();
				Object selectedElement = selection.getFirstElement();
				// do something with it
				selectedConfiguration = (String) selectedElement;
				if (selectedElement != null) {
					btnRemove.setEnabled(true);
					btnUp.setEnabled(true);
					btnDown.setEnabled(true);
				} else {
					btnRemove.setEnabled(false);
					btnUp.setEnabled(false);
					btnDown.setEnabled(false);
				}
			}
		});

	}

	private void createRemoveButtonWithListener() {
		btnRemove = new Button(comp, SWT.None);
		btnRemove.setText("Remove..");
		btnRemove.setEnabled(false);
		btnRemove.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (selectedConfiguration != null && configurationNameList != null)
					configurationNameList.remove(selectedConfiguration);
				viewer.refresh();
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});
	}

	private void createUpButtonWithListener() {
		btnUp = new Button(comp, SWT.None);
		btnUp.setText("Up");
		btnUp.setEnabled(false);
		btnUp.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (selectedConfiguration != null) {
					int index = configurationNameList.indexOf(selectedConfiguration);
					// If first element of table is selected, cant move it more
					// up than that, therefore return
					if (index > 0) {
						int indexBefore = index - 1;
						String temp = configurationNameList.get(index); // 5
						String temp2 = configurationNameList.get(indexBefore); // 10
						String temp3 = null; //
						temp3 = temp;
						temp = temp2;
						temp2 = temp3;
						configurationNameList.set(index, temp);
						configurationNameList.set(indexBefore, temp2);
						viewer.setInput(configurationNameList);
						viewer.refresh();
						setDirty(true);
						updateLaunchConfigurationDialog();

					} else {
						System.out.println("UP ERROR");
						return;
					}
				} else {
					System.out.println("selectedConfiguration IS NULL");
				}

			}
		});

	}

	private void createColumns(TableViewer viewer2) {

		// create a column for the launchconfiguration NAME
		TableViewerColumn colLaunchConfigurationName = new TableViewerColumn(viewer, SWT.NONE);
		colLaunchConfigurationName.getColumn().setWidth(200);
		colLaunchConfigurationName.getColumn().setText("Name");
		colLaunchConfigurationName.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// ILaunchConfiguration ilc = (ILaunchConfiguration) element;
				// return ilc.getName();
				return (String) element;
			}
		});

		// create a column for the launchconfiguration MODE
		TableViewerColumn colLaunchConfigurationMode = new TableViewerColumn(viewer, SWT.NONE);
		colLaunchConfigurationMode.getColumn().setWidth(200);
		colLaunchConfigurationMode.getColumn().setText("Mode");
		colLaunchConfigurationMode.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// ILaunchConfiguration ilc = (ILaunchConfiguration) element;
				return "";
			}
		});

		// create a column for the launchconfiguration ACTION
		TableViewerColumn colLaunchConfigurationAction = new TableViewerColumn(viewer, SWT.NONE);
		colLaunchConfigurationAction.getColumn().setWidth(200);
		colLaunchConfigurationAction.getColumn().setText("Action");
		colLaunchConfigurationAction.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				// ILaunchConfiguration ilc = (ILaunchConfiguration) element;
				// ilc.getWorkingCopy().setAttribute(ACTION, ssss);
				return "";
			}
		});

	}

	private void createDownButtonWithListener() {
		btnDown = new Button(comp, SWT.None);
		btnDown.setText("Down");
		btnDown.setEnabled(false);
		btnDown.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (selectedConfiguration != null) {
					int index = configurationNameList.indexOf(selectedConfiguration);
					// If last element of table is selected, cant move it more
					// down than that, therefore return
					if (index + 1 < configurationNameList.size()) {
						int indexAfter = index + 1;
						String temp = configurationNameList.get(index); // 5
						String temp2 = configurationNameList.get(indexAfter); // 10
						String temp3 = null; //
						temp3 = temp;
						temp = temp2;
						temp2 = temp3;
						configurationNameList.set(index, temp);
						configurationNameList.set(indexAfter, temp2);
						viewer.setInput(configurationNameList);
						viewer.refresh();
						setDirty(true);
						updateLaunchConfigurationDialog();
					} else {
						System.out.println("DOWN ERROR");
						return;
					}
				} else {
					System.out.println("selectedConfiguration IS NULL");
				}

			}
		});
	}

	@Override
	public String getName() {
		return "Launches";
	}

	private void insertPlaceHolder() {
		new Label(comp, SWT.None);
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		List<String> empty = new ArrayList<>();
		try {
			configurationNameList = configuration.getAttribute("configs", empty);
		} catch (CoreException e1) {
			e1.printStackTrace();
		}

		try {
			viewer.setInput(configuration.getAttribute("configs", configurationNameList));
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute("configs", configurationNameList);
	}
}
