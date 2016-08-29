package com.profidatagroup.e4.advancedlaunch.tabs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.Configuration;

import org.eclipse.cdt.launch.internal.MultiLaunchConfigurationDelegate.LaunchElement;
import org.eclipse.cdt.launch.internal.MultiLaunchConfigurationDelegate.LaunchElement.EPostLaunchAction;
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
import org.eclipse.swt.SWTException;
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
import com.profidatagroup.e4.advancedlaunch.LaunchConfigurationBean;
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
	private LaunchConfigurationBean selectedConfiguration;
	private Composite comp; // can be local in create control.
	private MultiLaunchConfigurationSelectionDialog multiLaunchConfigurationSelectionDialog = new MultiLaunchConfigurationSelectionDialog(
			getShell(), "debug", false);
	// public static List<String> selectedConfigurationsNameList = new
	// ArrayList<>();
	public static List<LaunchConfigurationBean> launchConfigurationDataList = new ArrayList<>();

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

				if (multiLaunchConfigurationSelectionDialog.open() == Window.OK) {
					// gets selected launchconfigurations from chooser
					// (selection dialog)
					for (ILaunchConfiguration a : multiLaunchConfigurationSelectionDialog
							.getSelectedLaunchConfigurations()) {
						System.out.println(a.getName());
						launchConfigurationDataList.add(new LaunchConfigurationBean(a.getName(),
								multiLaunchConfigurationSelectionDialog.getMode(),
								LaunchElement.actionEnumToStr(multiLaunchConfigurationSelectionDialog.getAction()),
								String.valueOf(multiLaunchConfigurationSelectionDialog.getActionParam())));

					}
					if (launchConfigurationDataList != null) {
						viewer.setInput(launchConfigurationDataList);
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
				selectedConfiguration = (LaunchConfigurationBean) selectedElement;
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
				if (selectedConfiguration != null && launchConfigurationDataList != null)
					launchConfigurationDataList.remove(selectedConfiguration);
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
					int index = launchConfigurationDataList.indexOf(selectedConfiguration);
					// If first element of table is selected, cant move it more
					// up than that, therefore return
					if (index > 0) {
						int indexBefore = index - 1;
						LaunchConfigurationBean temp = launchConfigurationDataList.get(index); // 5
						LaunchConfigurationBean temp2 = launchConfigurationDataList.get(indexBefore); // 10
						LaunchConfigurationBean temp3 = null; //
						temp3 = temp;
						temp = temp2;
						temp2 = temp3;
						launchConfigurationDataList.set(index, temp);
						launchConfigurationDataList.set(indexBefore, temp2);
						viewer.setInput(launchConfigurationDataList);
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
				LaunchConfigurationBean lcb = (LaunchConfigurationBean) element;
				return lcb.getName();
			}
		});

		// create a column for the launchconfiguration MODE
		TableViewerColumn colLaunchConfigurationMode = new TableViewerColumn(viewer, SWT.NONE);
		colLaunchConfigurationMode.getColumn().setWidth(200);
		colLaunchConfigurationMode.getColumn().setText("Mode");
		colLaunchConfigurationMode.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				LaunchConfigurationBean lcb = (LaunchConfigurationBean) element;
				return lcb.getMode();
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
				LaunchConfigurationBean lcb = (LaunchConfigurationBean) element;
				return lcb.getPostLaunchAction();
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
					int index = launchConfigurationDataList.indexOf(selectedConfiguration);
					// If last element of table is selected, cant move it more
					// down than that, therefore return
					if (index + 1 < launchConfigurationDataList.size()) {
						int indexAfter = index + 1;
						LaunchConfigurationBean temp = launchConfigurationDataList.get(index); // 5
						LaunchConfigurationBean temp2 = launchConfigurationDataList.get(indexAfter); // 10
						LaunchConfigurationBean temp3 = null; //
						temp3 = temp;
						temp = temp2;
						temp2 = temp3;
						launchConfigurationDataList.set(index, temp);
						launchConfigurationDataList.set(indexAfter, temp2);
						viewer.setInput(launchConfigurationDataList);
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
		createBeansFromAttributes(configuration);
	}

	private void createBeansFromAttributes(ILaunchConfiguration configuration) {
		try {

			List<String> names = new ArrayList<>();
			List<String> modes = new ArrayList<>();
			List<String> postLaunchActions = new ArrayList<>();
			List<String> params = new ArrayList<>();

			launchConfigurationDataList.clear();

			names = configuration.getAttribute("names", new ArrayList<String>());
			modes = configuration.getAttribute("modes", new ArrayList<String>());
			postLaunchActions = configuration.getAttribute("postLaunchActions", new ArrayList<String>());
			params = configuration.getAttribute("params", new ArrayList<String>());

			for (int i = 0; i < names.size(); i++) {
				launchConfigurationDataList.add(new LaunchConfigurationBean(names.get(i), modes.get(i),
						postLaunchActions.get(i), params.get(i)));
			}

			viewer.setInput(launchConfigurationDataList);
			viewer.refresh();

		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		List<String> names = new ArrayList<>();
		List<String> modes = new ArrayList<>();
		List<String> postLaunchActions = new ArrayList<>();
		List<String> params = new ArrayList<>();

		for (LaunchConfigurationBean launchConfigurationBean : launchConfigurationDataList) {
			names.add(launchConfigurationBean.getName());
			modes.add(launchConfigurationBean.getMode());
			postLaunchActions.add(launchConfigurationBean.getPostLaunchAction());
			params.add(launchConfigurationBean.getParam());
		}

		configuration.setAttribute("names", names);
		configuration.setAttribute("modes", modes);
		configuration.setAttribute("postLaunchActions", postLaunchActions);
		configuration.setAttribute("params", params);
	}
}
