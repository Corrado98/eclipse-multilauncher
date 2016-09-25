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
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
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

import ch.parisi.e4.advancedlaunch.LaunchConfigurationModel;
import ch.parisi.e4.advancedlaunch.dialog.MultiLaunchConfigurationSelectionDialog;
import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;
import ch.parisi.e4.advancedlaunch.utils.LaunchUtils;
import ch.parisi.e4.advancedlaunch.utils.PostLaunchAction;
import ch.parisi.e4.advancedlaunch.utils.PostLaunchActionUtils;

/**
 * The LaunchConfiguration Tab to edit the multi launch configuration.
 */
public class LaunchTab extends AbstractLaunchConfigurationTab {

	private Button btnAdd;
	private Button btnRemove;
	private Button btnUp;
	private Button btnDown;
	private Button btnEdit;
	private TableViewer tableViewer;
	private LaunchConfigurationModel selectedConfiguration;
	private Composite mainComposite;
	private Composite buttonComposite;

	private String launchName;
	private List<LaunchConfigurationModel> launchConfigurationDataList = new ArrayList<>();

	@Override
	public void createControl(Composite parent) {
		initMainComposite(parent);
		initTableViewer();
		initButtonComposite();

		//just change this method order to reorder the buttons in the gui.
		initAddBtnWithListener();
		initEditButtonWithListener();
		initUpButtonWithListener();
		initDownButtonWithListener();
		initRemoveButtonWithListener();
	}

	private void initMainComposite(Composite parent) {
		mainComposite = new Group(parent, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(mainComposite);
		setControl(mainComposite);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(mainComposite);
	}

	private void initTableViewer() {
		tableViewer = new TableViewer(mainComposite,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
		tableViewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent event) {
				editLaunchConfiguration();
			}
		});

		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(tableViewer.getTable());

		// gets user selected element in the table and works with it.
		addTableViewerSelectionChangedListener();

		// set the content provider
		tableViewer.setContentProvider(ArrayContentProvider.getInstance());

		// create the columns
		createColumns();

		// make lines and header visible
		final Table table = tableViewer.getTable();
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
	}

	private void initButtonComposite() {
		buttonComposite = new Composite(mainComposite, SWT.BORDER_DOT);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.BEGINNING).applyTo(buttonComposite);
		FillLayout fillLayout = new FillLayout(SWT.VERTICAL);
		fillLayout.spacing = 5;
		buttonComposite.setLayout(fillLayout);
	}

	private void initAddBtnWithListener() {
		btnAdd = new Button(buttonComposite, SWT.None);
		btnAdd.setText(LaunchMessages.LaunchGroupConfiguration_Add);
		btnAdd.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				addLaunchConfigurationToModel();
			}
		});
	}
	
	private void addLaunchConfigurationToModel() {
		MultiLaunchConfigurationSelectionDialog multiLaunchConfigurationSelectionDialog = new MultiLaunchConfigurationSelectionDialog(
				getShell());
		initAddConfigurationSelectionDialog(multiLaunchConfigurationSelectionDialog);

		if (multiLaunchConfigurationSelectionDialog.open() == Window.OK) {
			launchConfigurationDataList.add(new LaunchConfigurationModel(
					multiLaunchConfigurationSelectionDialog.getSelectedLaunchConfiguration().getName(),
					multiLaunchConfigurationSelectionDialog.getMode(),
					multiLaunchConfigurationSelectionDialog.getAction(),
					String.valueOf(multiLaunchConfigurationSelectionDialog.getActionParam())));

			if (launchConfigurationDataList != null) {
				updateDirtyModel();
			}
		}
	}

	private void updateDirtyModel() {
		tableViewer.setInput(launchConfigurationDataList);
		tableViewer.refresh();
		setDirty(true);
		updateLaunchConfigurationDialog();
	}

	private void initAddConfigurationSelectionDialog(
			MultiLaunchConfigurationSelectionDialog multiLaunchConfigurationSelectionDialog) {
		multiLaunchConfigurationSelectionDialog.setForEditing(false);
		multiLaunchConfigurationSelectionDialog.setMode("debug");
		multiLaunchConfigurationSelectionDialog.setAction(PostLaunchAction.NONE);
		multiLaunchConfigurationSelectionDialog.setActionParam("");
	}

	private void initEditButtonWithListener() {
		btnEdit = new Button(buttonComposite, SWT.None);
		btnEdit.setText(LaunchMessages.LaunchGroupConfiguration_Edit);
		btnEdit.setEnabled(false);
		btnEdit.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				editLaunchConfiguration();
			}
		});
	}

	private void editLaunchConfiguration() {
		MultiLaunchConfigurationSelectionDialog multiLaunchConfigurationSelectionDialog = new MultiLaunchConfigurationSelectionDialog(
				getShell());
		loadExistingConfigurationData(multiLaunchConfigurationSelectionDialog);

		ILaunchConfiguration launchConfiguration;
		try {
			launchConfiguration = LaunchUtils.findLaunchConfiguration(selectedConfiguration.getName());
			if (launchConfiguration != null) {
				if (LaunchUtils.isValidLaunchReference(launchConfiguration)) {
					multiLaunchConfigurationSelectionDialog.setInitialSelection(launchConfiguration);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}

		if (multiLaunchConfigurationSelectionDialog.open() == Window.OK) {
			ILaunchConfiguration configuration = multiLaunchConfigurationSelectionDialog
					.getSelectedLaunchConfiguration();

			LaunchConfigurationModel model = new LaunchConfigurationModel(configuration.getName(),
					multiLaunchConfigurationSelectionDialog.getMode(),
					multiLaunchConfigurationSelectionDialog.getAction(),
					String.valueOf(multiLaunchConfigurationSelectionDialog.getActionParam()));
			launchConfigurationDataList.set(launchConfigurationDataList.indexOf(selectedConfiguration), model);

			if (launchConfigurationDataList != null) {
				updateDirtyModel();
			}
		}
	}

	private void loadExistingConfigurationData(
			MultiLaunchConfigurationSelectionDialog multiLaunchConfigurationSelectionDialog) {
		multiLaunchConfigurationSelectionDialog.setForEditing(true);
		multiLaunchConfigurationSelectionDialog.setMode(selectedConfiguration.getMode());
		multiLaunchConfigurationSelectionDialog.setAction(selectedConfiguration.getPostLaunchAction());
		multiLaunchConfigurationSelectionDialog.setActionParam(selectedConfiguration.getParam());
	}

	private void addTableViewerSelectionChangedListener() {
		tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				ISelection selection = tableViewer.getSelection();
				if (selection instanceof IStructuredSelection) {
					IStructuredSelection structuredSelection = (IStructuredSelection) selection;
					Object selectedElement = structuredSelection.getFirstElement();
					selectedConfiguration = (LaunchConfigurationModel) selectedElement;

					boolean hasSelection = selectedElement != null;
					btnRemove.setEnabled(hasSelection);
					btnUp.setEnabled(hasSelection);
					btnDown.setEnabled(hasSelection);
					btnEdit.setEnabled(hasSelection);
				}
			}
		});

	}

	private void initRemoveButtonWithListener() {
		btnRemove = new Button(buttonComposite, SWT.None);
		btnRemove.setText(LaunchMessages.LaunchGroupConfiguration_Remove);
		btnRemove.setEnabled(false);
		btnRemove.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (selectedConfiguration != null && launchConfigurationDataList != null) {
					launchConfigurationDataList.remove(selectedConfiguration);
					updateDirtyModel();
				}
			}
		});
	}

	private void initUpButtonWithListener() {
		btnUp = new Button(buttonComposite, SWT.None);
		btnUp.setText(LaunchMessages.LaunchGroupConfiguration_Up);
		btnUp.setEnabled(false);
		btnUp.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (selectedConfiguration != null) {
					int index = launchConfigurationDataList.indexOf(selectedConfiguration);
					// If first element of table is selected, cant move it further
					// up than that, therefore return.
					if (index > 0) {
						int indexBefore = index - 1;
						LaunchConfigurationModel temp = launchConfigurationDataList.get(indexBefore);
						launchConfigurationDataList.set(indexBefore, selectedConfiguration);
						launchConfigurationDataList.set(index, temp);
						updateDirtyModel();
					}
				}
			}
		});
	}

	private void initDownButtonWithListener() {
		btnDown = new Button(buttonComposite, SWT.None);
		btnDown.setText(LaunchMessages.LaunchGroupConfiguration_Down);
		btnDown.setEnabled(false);
		btnDown.addListener(SWT.Selection, new Listener() {

			@Override
			public void handleEvent(Event event) {
				if (selectedConfiguration != null) {
					int index = launchConfigurationDataList.indexOf(selectedConfiguration);
					// If last element of table is selected, cant move it further
					// down than that, therefore return.
					if (index + 1 < launchConfigurationDataList.size()) {
						int indexAfter = index + 1;
						LaunchConfigurationModel temp = launchConfigurationDataList.get(indexAfter);
						launchConfigurationDataList.set(index, temp);
						launchConfigurationDataList.set(indexAfter, selectedConfiguration);
						updateDirtyModel();
					}
				}
			}
		});
	}

	private void createColumns() {
		// create a column for the launchconfiguration NAME
		addTableColumn("Name", 175, launchConfigurationModel -> launchConfigurationModel.getName());

		// create a column for the launchconfiguration MODE
		addTableColumn("Mode", 175, launchConfigurationModel -> launchConfigurationModel.getMode());

		// create a column for the launchconfiguration POST-ACTION
		addTableColumn("Action", 175, launchConfigurationModel -> PostLaunchActionUtils
				.convertToName(launchConfigurationModel.getPostLaunchAction()));

		// create a column for the launchconfiguration PARAM
		addTableColumn("Param", 120, launchConfigurationModel -> launchConfigurationModel.getParam());
	}

	private void addTableColumn(String name, int width, Function<LaunchConfigurationModel, String> actionTextFetcher) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn.getColumn().setWidth(width);
		tableViewerColumn.getColumn().setText(name);
		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				LaunchConfigurationModel model = (LaunchConfigurationModel) element;
				return actionTextFetcher.apply(model);
			}
		});
	}

	@Override
	public String getName() {
		// Used to set the tab's name
		return LaunchMessages.LaunchGroupConfiguration_Launches;
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		launchName = configuration.getName();
		createModelsFromAttributes(configuration);
	}

	private void createModelsFromAttributes(ILaunchConfiguration configuration) {
		try {
			launchConfigurationDataList = LaunchUtils.loadLaunchConfigurations(configuration);
			tableViewer.setInput(launchConfigurationDataList);
			tableViewer.refresh();
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

		for (LaunchConfigurationModel launchConfigurationModel : launchConfigurationDataList) {
			names.add(launchConfigurationModel.getName());
			modes.add(launchConfigurationModel.getMode());
			postLaunchActions.add(PostLaunchActionUtils.convertToName(launchConfigurationModel.getPostLaunchAction()));
			params.add(launchConfigurationModel.getParam());
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
		 * be saved or launched. An invalid launch-reference or an infinite-loop at any
		 * nesting-depth will cause this method to return false.
		 */
		return canSave();
	}

	@Override
	public boolean canSave() {
		setMessage(null);
		setErrorMessage(null);

		try {
			if(((List<LaunchConfigurationModel>)tableViewer.getInput()).isEmpty()) return false;
			validateRecursive(launchName, null, launchConfigurationDataList);
			return true;
		} catch (LaunchValidationException launchValidationException) {
			setErrorMessage(launchValidationException.getMessage());
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void validateRecursive(String rootLaunchName, String firstLevelChildLaunchName,
			List<LaunchConfigurationModel> launchConfigurationDataList) throws CoreException {
		for (LaunchConfigurationModel launchConfigurationModel : launchConfigurationDataList) {
			if (rootLaunchName.equals(launchConfigurationModel.getName())) {
				throw new LaunchValidationException(MessageFormat
						.format(LaunchMessages.LaunchGroupConfigurationDelegate_Loop, 
								firstLevelChildLaunchName == null
								? launchConfigurationModel.getName() : firstLevelChildLaunchName));
			}

			ILaunchConfiguration childLaunchConfiguration = LaunchUtils
					.findLaunchConfiguration(launchConfigurationModel.getName());
			if (childLaunchConfiguration != null) {
				List<LaunchConfigurationModel> childLaunchConfigurationModels = LaunchUtils
						.loadLaunchConfigurations(childLaunchConfiguration);
				String childLaunchName = launchConfigurationModel.getName();

				validateRecursive(rootLaunchName,
						firstLevelChildLaunchName == null ? childLaunchName : firstLevelChildLaunchName,
						childLaunchConfigurationModels);
			} else {
				//invalid launch-reference
				if (firstLevelChildLaunchName == null) {
					throw new LaunchValidationException(MessageFormat.format(
							LaunchMessages.LaunchGroupConfiguration_NotFound, launchConfigurationModel.getName()));
				} else {
					throw new LaunchValidationException(
							MessageFormat.format(LaunchMessages.LaunchGroupConfiguration_RecursiveNotFound,
									firstLevelChildLaunchName, launchConfigurationModel.getName()));
				}
			}
		}
	}

	private static class LaunchValidationException extends RuntimeException {
		public LaunchValidationException(String message) {
			super(message);
		}
	}

	@Override
	public Image getImage() {
		return DebugPluginImages.getImage(IDebugUIConstants.IMG_ACT_RUN);
	}
}
