package ch.parisi.e4.advancedlaunch.tabs;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.internal.ui.DebugPluginImages;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jface.layout.GridDataFactory;
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
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

import ch.parisi.e4.advancedlaunch.EnumController;
import ch.parisi.e4.advancedlaunch.LaunchConfigurationBean;
import ch.parisi.e4.advancedlaunch.LaunchUtils;
import ch.parisi.e4.advancedlaunch.dialog.MultiLaunchConfigurationSelectionDialog;
import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;

/**
 * The LaunchConfiguration Tab, which contains the user-customizable
 * TableViewer.
 * 
 */
public class LaunchTab extends AbstractLaunchConfigurationTab {

	private Button btnAdd;
	private Button btnRemove;
	private Button btnUp;
	private Button btnDown;
	private Button btnEdit;
	private TableViewer viewer;
	private LaunchConfigurationBean selectedConfiguration;
	private Composite mainComposite;
	private Composite buttonComposite;
	private List<LaunchConfigurationBean> launchConfigurationDataList = new ArrayList<>();

	@Override
	public void createControl(Composite parent) {
		initMainComposite(parent);
		initTableViewer();
		initButtonComposite();
		createAddBtnWithListener();
		createRemoveButtonWithListener();
		createUpButtonWithListener();
		createDownButtonWithListener();
		createEditButtonWithListener();
	}

	private void initMainComposite(Composite parent) {
		mainComposite = new Group(parent, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(mainComposite);
		setControl(mainComposite);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(mainComposite);
	}

	private void initButtonComposite() {
		buttonComposite = new Composite(mainComposite, SWT.BORDER_DOT);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(buttonComposite);
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.spacing = 5;
		buttonComposite.setLayout(fillLayout);
	}

	private void initTableViewer() {
		viewer = new TableViewer(mainComposite,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(viewer.getTable());

		// gets user selected element in the table and works with it.
		addTableViewerSelectionChangedListener();

		// set the content provider
		viewer.setContentProvider(ArrayContentProvider.getInstance());

		// create the columns
		createColumns();

		// make lines and header visible
		final Table table = viewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private void createAddBtnWithListener() {
		btnAdd = new Button(buttonComposite, SWT.None);
		btnAdd.setText(LaunchMessages.LaunchGroupConfiguration_Add);
		btnAdd.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				MultiLaunchConfigurationSelectionDialog multiLaunchConfigurationSelectionDialog = new MultiLaunchConfigurationSelectionDialog(
						getShell());
				initAddConfigurationSelectionDialog(multiLaunchConfigurationSelectionDialog);

				if (multiLaunchConfigurationSelectionDialog.open() == Window.OK) {
					launchConfigurationDataList.add(new LaunchConfigurationBean(
							multiLaunchConfigurationSelectionDialog.getSelectedLaunchConfiguration().getName(),
							multiLaunchConfigurationSelectionDialog.getMode(),
							EnumController.actionEnumToStr(multiLaunchConfigurationSelectionDialog.getAction()),
							String.valueOf(multiLaunchConfigurationSelectionDialog.getActionParam())));

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

	private void initAddConfigurationSelectionDialog(
			MultiLaunchConfigurationSelectionDialog multiLaunchConfigurationSelectionDialog) {
		multiLaunchConfigurationSelectionDialog.setForEditing(false);
		multiLaunchConfigurationSelectionDialog.setMode("debug");
		multiLaunchConfigurationSelectionDialog.setAction(EnumController.strToActionEnum("none"));
		multiLaunchConfigurationSelectionDialog.setActionParam("");
	}

	private void createEditButtonWithListener() {
		btnEdit = new Button(buttonComposite, SWT.None);
		btnEdit.setText(LaunchMessages.LaunchGroupConfiguration_Edit);
		btnEdit.setEnabled(false);
		btnEdit.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				MultiLaunchConfigurationSelectionDialog multiLaunchConfigurationSelectionDialog = new MultiLaunchConfigurationSelectionDialog(
						getShell());
				loadExistingConfigurationData(multiLaunchConfigurationSelectionDialog);

				ILaunchConfiguration launchConfiguration;
				try {
					launchConfiguration = LaunchUtils.findLaunchConfiguration(selectedConfiguration.getName());

					if (!LaunchUtils.isValidLaunchReference(launchConfiguration)) {
						// select nothing
					} else {
						multiLaunchConfigurationSelectionDialog.setInitialSelection(launchConfiguration);
					}
				} catch (CoreException e) {
					e.printStackTrace();
				}

				if (multiLaunchConfigurationSelectionDialog.open() == Window.OK) {
					ILaunchConfiguration configuration = multiLaunchConfigurationSelectionDialog
							.getSelectedLaunchConfiguration();

					launchConfigurationDataList.add(launchConfigurationDataList.indexOf(selectedConfiguration),
							new LaunchConfigurationBean(configuration.getName(),
									multiLaunchConfigurationSelectionDialog.getMode(),
									EnumController.actionEnumToStr(multiLaunchConfigurationSelectionDialog.getAction()),
									String.valueOf(multiLaunchConfigurationSelectionDialog.getActionParam())));
					launchConfigurationDataList.remove(selectedConfiguration);

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

	private void loadExistingConfigurationData(
			MultiLaunchConfigurationSelectionDialog multiLaunchConfigurationSelectionDialog) {
		multiLaunchConfigurationSelectionDialog.setForEditing(true);
		multiLaunchConfigurationSelectionDialog.setMode(selectedConfiguration.getMode());
		multiLaunchConfigurationSelectionDialog
				.setAction(EnumController.strToActionEnum(selectedConfiguration.getPostLaunchAction()));
		multiLaunchConfigurationSelectionDialog.setActionParam(selectedConfiguration.getParam());
	}

	private void addTableViewerSelectionChangedListener() {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = viewer.getStructuredSelection();
				Object selectedElement = selection.getFirstElement();
				selectedConfiguration = (LaunchConfigurationBean) selectedElement;

				if (selectedElement != null) {
					btnRemove.setEnabled(true);
					btnUp.setEnabled(true);
					btnDown.setEnabled(true);
					btnEdit.setEnabled(true);
				} else {
					btnRemove.setEnabled(false);
					btnUp.setEnabled(false);
					btnDown.setEnabled(false);
					btnEdit.setEnabled(false);
				}
			}
		});

	}

	private void createRemoveButtonWithListener() {
		btnRemove = new Button(buttonComposite, SWT.None);
		btnRemove.setText(LaunchMessages.LaunchGroupConfiguration_Remove);
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
		btnUp = new Button(buttonComposite, SWT.None);
		btnUp.setText(LaunchMessages.LaunchGroupConfiguration_Up);
		btnUp.setEnabled(false);
		btnUp.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (selectedConfiguration != null) {
					int index = launchConfigurationDataList.indexOf(selectedConfiguration);
					// If first element of table is selected, cant move it
					// further
					// up than that, therefore return.
					if (index > 0) {
						int indexBefore = index - 1;
						LaunchConfigurationBean temp = launchConfigurationDataList.get(index);
						LaunchConfigurationBean temp2 = launchConfigurationDataList.get(indexBefore);
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
					}
				}
			}
		});

	}

	private void createDownButtonWithListener() {
		btnDown = new Button(buttonComposite, SWT.None);
		btnDown.setText(LaunchMessages.LaunchGroupConfiguration_Down);
		btnDown.setEnabled(false);
		btnDown.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (selectedConfiguration != null) {
					int index = launchConfigurationDataList.indexOf(selectedConfiguration);
					// If last element of table is selected, cant move it
					// further
					// down than that, therefore return.
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
					}
				}
			}
		});
	}

	private void createColumns() {
		// create a column for the launchconfiguration NAME
		addTableColumn("Name", 175, lcb -> lcb.getName());

		// create a column for the launchconfiguration MODE
		addTableColumn("Mode", 175, lcb -> lcb.getMode());

		// create a column for the launchconfiguration POST-ACTION
		addTableColumn("Action", 175, lcb -> lcb.getPostLaunchAction());

		// create a column for the launchconfiguration PARAM
		addTableColumn("Param", 120, lcb -> lcb.getParam());
	}

	private void addTableColumn(String name, int width, Function<LaunchConfigurationBean, String> actionTextFetcher) {
		TableViewerColumn colLaunchConfigurationAction = new TableViewerColumn(viewer, SWT.NONE);
		colLaunchConfigurationAction.getColumn().setWidth(width);
		colLaunchConfigurationAction.getColumn().setText(name);
		colLaunchConfigurationAction.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				LaunchConfigurationBean lcb = (LaunchConfigurationBean) element;
				return actionTextFetcher.apply(lcb);
			}
		});
	}

	@Override
	public String getName() {
		return LaunchMessages.LaunchGroupConfiguration_Launches;
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
			launchConfigurationDataList = LaunchUtils.loadLaunchConfigurations(configuration);
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

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		/*
		 * This method decides recursively whether a custom-configuration can
		 * launch or not. An invalid launch-reference or an infinite loop at any
		 * nesting-depth will cause this method to return <code>false</code>.
		 */

		setMessage(null);
		setErrorMessage(null);

		List<LaunchConfigurationBean> launchConfigurationDataList;
		try {
			// all sublaunches of method-param: 'launchConfig'
			launchConfigurationDataList = LaunchUtils.loadLaunchConfigurations(launchConfig);

			if (isRecursiveLaunchConfiguration(launchConfig.getName(), launchConfigurationDataList)) {
				setErrorMessage(
						MessageFormat.format(LaunchMessages.LaunchGroupConfigurationDelegate_Loop, launchConfig.getName()));
				return false;
			}

			for (LaunchConfigurationBean bean : launchConfigurationDataList) {
				ILaunchConfiguration launchConfiguration = LaunchUtils.findLaunchConfiguration(bean.getName());

				// invalid launch reference.
				if (launchConfiguration == null) {
					setErrorMessage(MessageFormat.format(LaunchMessages.LaunchGroupConfiguration_14, bean.getName()));
					return false;
					// invalid launch reference.
				} else if (!LaunchUtils.isValidLaunchReference(launchConfiguration)) {
					setErrorMessage(MessageFormat.format(LaunchMessages.LaunchGroupConfiguration_15, bean.getName()));
					return false;
				}

				// simple infinite loop detection. If configurationA tries to
				// call itself.
//				if (launchConfig.getName().equals(bean.getName())) {
//					setErrorMessage(
//							MessageFormat.format(LaunchMessages.LaunchGroupConfigurationDelegate_Loop, bean.getName()));
//					return false;
//				}
//
//				// look for possible, nested infinite loop
//				else if (!(launchConfig.getName().equals(bean.getName()))) {
//					List<LaunchConfigurationBean> tempLaunchConfigurationDataList = LaunchUtils
//							.loadLaunchConfigurations(LaunchUtils.findLaunchConfiguration(bean.getName()));
//
//					for (LaunchConfigurationBean launchConfigurationBean : tempLaunchConfigurationDataList) {
//						// if a configurationA stores a configurationB, which
//						// could call configurationA again.
//						if (launchConfig.getName().equals(launchConfigurationBean.getName())) {
//							setErrorMessage(MessageFormat.format(LaunchMessages.LaunchGroupConfigurationDelegate_Loop,
//									bean.getName()));
//							return false;
//						}
//					}
//				}
//				// if configurationA stores an already invalid
//				// LaunchConfiguration.
//				if (!isValid(launchConfiguration)) {
//					setErrorMessage(MessageFormat.format(LaunchMessages.LaunchGroupConfigurationDelegate_Error,
//							launchConfiguration.getName()));
//					return false;
//				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return true;
	}

	private boolean isRecursiveLaunchConfiguration(String launchName, List<LaunchConfigurationBean> launchConfigurationBeans) throws CoreException {
		for (LaunchConfigurationBean launchConfigurationBean : launchConfigurationBeans) {
			if (launchName.equals(launchConfigurationBean.getName())) {
				return true;
			}
			
			List<LaunchConfigurationBean> childLaunchConfigurationBeans = LaunchUtils.loadLaunchConfigurations(LaunchUtils.findLaunchConfiguration(launchConfigurationBean.getName()));
			if (isRecursiveLaunchConfiguration(launchName, childLaunchConfigurationBeans)) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Image getImage() {
		return DebugPluginImages.getImage(IDebugUIConstants.IMG_ACT_RUN);
	}
}
