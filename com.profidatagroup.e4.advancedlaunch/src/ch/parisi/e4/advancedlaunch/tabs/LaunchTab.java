package ch.parisi.e4.advancedlaunch.tabs;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.parisi.e4.advancedlaunch.Activator;
import ch.parisi.e4.advancedlaunch.LaunchConfigurationModel;
import ch.parisi.e4.advancedlaunch.dialog.MultiLaunchConfigurationSelectionDialog;
import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;
import ch.parisi.e4.advancedlaunch.tabs.editingsupport.LaunchModeEditingSupport;
import ch.parisi.e4.advancedlaunch.tabs.editingsupport.ParamEditingSupport;
import ch.parisi.e4.advancedlaunch.tabs.editingsupport.PostLaunchActionEditingSupport;
import ch.parisi.e4.advancedlaunch.utils.LaunchUtils;
import ch.parisi.e4.advancedlaunch.utils.PostLaunchAction;
import ch.parisi.e4.advancedlaunch.utils.PostLaunchActionUtils;

/**
 * The LaunchConfiguration Tab to edit the multilaunch configuration.
 */
public class LaunchTab extends AbstractLaunchConfigurationTab {

	private Button btnAdd;
	private Button btnRemove;
	private Button btnUp;
	private Button btnDown;
	private Button btnEdit;
	private Button promptBeforeLaunchCheckbox;
	private TableViewer tableViewer;
	private LaunchConfigurationModel selectedConfiguration;
	private Composite mainComposite;
	private Composite buttonComposite;

	private String launchName;
	private List<LaunchConfigurationModel> launchConfigurationDataList = new ArrayList<>();

	private PropertyChangeListener propertyChangeListener = new PropertyChangeListener() {
		@Override
		public void propertyChange(PropertyChangeEvent evt) {
			switch (evt.getPropertyName()) {
				case "mode":
					setDirty(true);
					updateDirtyModel();
					break;

				case "postLaunchAction":
					((LaunchConfigurationModel) evt.getSource()).setParam("");
					setDirty(true);
					updateLaunchConfigurationDialog();
					break;

				case "param":
					setDirty(true);
					updateLaunchConfigurationDialog();
					break;
				case "abortLaunchOnError":
					setDirty(true);
					updateLaunchConfigurationDialog();
					break;
			}
		}
	};

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

		initPromptBeforeLaunchCheckbox();
	}

	private void initMainComposite(Composite parent) {
		mainComposite = new Group(parent, SWT.NONE);
		GridDataFactory.swtDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(mainComposite);
		setControl(mainComposite);
		GridLayoutFactory.swtDefaults().numColumns(2).applyTo(mainComposite);
	}

	private void initTableViewer() {
		tableViewer = new TableViewer(
				mainComposite,
				SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);

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
			LaunchConfigurationModel launchConfigurationModel = new LaunchConfigurationModel(
					multiLaunchConfigurationSelectionDialog.getSelectedLaunchConfiguration().getName(),
					multiLaunchConfigurationSelectionDialog.getMode(),
					multiLaunchConfigurationSelectionDialog.getAction(),
					String.valueOf(multiLaunchConfigurationSelectionDialog.getActionParam()),
					multiLaunchConfigurationSelectionDialog.isAbortLaunchOnError());
			launchConfigurationModel.addPropertyChangeListener("mode", propertyChangeListener);
			launchConfigurationModel.addPropertyChangeListener("postLaunchAction", propertyChangeListener);
			launchConfigurationModel.addPropertyChangeListener("param", propertyChangeListener);
			launchConfigurationModel.addPropertyChangeListener("abortLaunchOnError", propertyChangeListener);
			launchConfigurationDataList.add(launchConfigurationModel);

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
		multiLaunchConfigurationSelectionDialog.setEditMode(false);
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

		if (multiLaunchConfigurationSelectionDialog.open() == Window.OK) {
			ILaunchConfiguration configuration = multiLaunchConfigurationSelectionDialog
					.getSelectedLaunchConfiguration();

			LaunchConfigurationModel launchConfigurationModel = new LaunchConfigurationModel(
					configuration.getName(),
					multiLaunchConfigurationSelectionDialog.getMode(),
					multiLaunchConfigurationSelectionDialog.getAction(),
					String.valueOf(multiLaunchConfigurationSelectionDialog.getActionParam()),
					multiLaunchConfigurationSelectionDialog.isAbortLaunchOnError());
			launchConfigurationModel.addPropertyChangeListener("mode", propertyChangeListener);
			launchConfigurationModel.addPropertyChangeListener("postLaunchAction", propertyChangeListener);
			launchConfigurationModel.addPropertyChangeListener("param", propertyChangeListener);
			launchConfigurationModel.addPropertyChangeListener("abortLaunchOnError", propertyChangeListener);
			LaunchConfigurationModel model = launchConfigurationModel;
			launchConfigurationDataList.set(launchConfigurationDataList.indexOf(selectedConfiguration), model);

			if (launchConfigurationDataList != null) {
				updateDirtyModel();
			}
		}
	}

	private void loadExistingConfigurationData(
			MultiLaunchConfigurationSelectionDialog multiLaunchConfigurationSelectionDialog) {
		multiLaunchConfigurationSelectionDialog.setEditMode(true);
		multiLaunchConfigurationSelectionDialog.setMode(selectedConfiguration.getMode());
		multiLaunchConfigurationSelectionDialog.setAction(selectedConfiguration.getPostLaunchAction());
		multiLaunchConfigurationSelectionDialog.setActionParam(selectedConfiguration.getParam());
		multiLaunchConfigurationSelectionDialog.setAbortLaunchOnError(selectedConfiguration.isAbortLaunchOnError());

		try {
			ILaunchConfiguration launchConfiguration = LaunchUtils.findLaunchConfiguration(selectedConfiguration.getName());
			if (launchConfiguration != null) {
				if (LaunchUtils.isValidLaunchReference(launchConfiguration)) { //likely unnecessary
					multiLaunchConfigurationSelectionDialog.setInitialSelection(launchConfiguration);
				}
			}
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
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
					//Is this cleaner?
					//selectedConfiguration.removePropertyChangeListener(propertyChangeListener);
					updateDirtyModel();
				}
			}
		});
	}

	private void initPromptBeforeLaunchCheckbox() {
		promptBeforeLaunchCheckbox = new Button(mainComposite, SWT.CHECK);
		promptBeforeLaunchCheckbox.setText(LaunchMessages.LaunchGroupConfiguration_PromptBeforeLaunch);
		promptBeforeLaunchCheckbox.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateDirtyModel();
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
					// If first element of table is selected, can't move it further
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
					// If last element of table is selected, can't move it further
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

		initEditingSupport(name, tableViewerColumn);

		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				LaunchConfigurationModel model = (LaunchConfigurationModel) element;
				return actionTextFetcher.apply(model);
			}
		});
	}

	private void initEditingSupport(String name, TableViewerColumn tableViewerColumn) {
		if (name.equals("Mode")) {
			tableViewerColumn.setEditingSupport(new LaunchModeEditingSupport(tableViewer));
		}
		else if (name.equals("Action")) {
			tableViewerColumn.setEditingSupport(new PostLaunchActionEditingSupport(tableViewer));
		}
		else if (name.equals("Param")) {
			tableViewerColumn.setEditingSupport(new ParamEditingSupport(tableViewer));
		}
	}

	@Override
	public String getName() {
		// Used to set the tab's name
		return LaunchMessages.LaunchGroupConfiguration_Launches;
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		// empty
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		launchName = configuration.getName();
		initMultilaunchConfigurationFromAttributes(configuration);
	}

	private void initMultilaunchConfigurationFromAttributes(ILaunchConfiguration configuration) {
		try {
			List<LaunchConfigurationModel> loadLaunchConfigurations = LaunchUtils
					.loadLaunchConfigurations(configuration);
			for (LaunchConfigurationModel launchConfigurationModel : loadLaunchConfigurations) {
				launchConfigurationModel.addPropertyChangeListener("mode", propertyChangeListener);
				launchConfigurationModel.addPropertyChangeListener("postLaunchAction", propertyChangeListener);
				launchConfigurationModel.addPropertyChangeListener("param", propertyChangeListener);
				launchConfigurationModel.addPropertyChangeListener("abortLaunchOnError", propertyChangeListener);
			}
			launchConfigurationDataList = loadLaunchConfigurations;
			tableViewer.setInput(launchConfigurationDataList);
			tableViewer.refresh();
			promptBeforeLaunchCheckbox.setSelection(configuration.getAttribute("promptBeforeLaunch", false));
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		List<String> names = new ArrayList<>();
		List<String> modes = new ArrayList<>();
		List<String> postLaunchActions = new ArrayList<>();
		List<String> params = new ArrayList<>();
		List<String> abortLaunchesOnException = new ArrayList<>();

		for (LaunchConfigurationModel launchConfigurationModel : launchConfigurationDataList) {
			names.add(launchConfigurationModel.getName());
			modes.add(launchConfigurationModel.getMode());
			postLaunchActions.add(PostLaunchActionUtils.convertToName(launchConfigurationModel.getPostLaunchAction()));
			params.add(launchConfigurationModel.getParam());
			abortLaunchesOnException.add(String.valueOf(launchConfigurationModel.isAbortLaunchOnError()));
		}

		configuration.setAttribute("names", names);
		configuration.setAttribute("modes", modes);
		configuration.setAttribute("postLaunchActions", postLaunchActions);
		configuration.setAttribute("params", params);
		configuration.setAttribute("abortLaunchesOnException", abortLaunchesOnException);

		configuration.setAttribute("promptBeforeLaunch", promptBeforeLaunchCheckbox.getSelection());
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

		for (LaunchConfigurationModel launchConfigurationModel : launchConfigurationDataList) {
			if (launchConfigurationModel.getPostLaunchAction() == PostLaunchAction.DELAY) {
				if (!isValidNumber(launchConfigurationModel.getParam())) {
					setErrorMessage("Invalid number of seconds: " + launchConfigurationModel.getParam());
					return false;
				}
			}
			if (launchConfigurationModel.getPostLaunchAction() == PostLaunchAction.WAIT_FOR_CONSOLESTRING) {
				if (launchConfigurationModel.getParam().trim().isEmpty()) {
					setErrorMessage("Empty regular expression: " + launchConfigurationModel.getParam());
					return false;
				}
			}
		}

		try {
			if (((List<?>) tableViewer.getInput()).isEmpty()) {
				setMessage(null);
				setErrorMessage(null);
				return false;
			}
			validateRecursive(launchName, null, launchConfigurationDataList);
			return true;
		}
		catch (LaunchValidationException launchValidationException) {
			setErrorMessage(launchValidationException.getMessage());
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		return false;
	}

	private boolean isValidNumber(String string) {
		try {
			Integer.parseInt(string);
		}
		catch (NumberFormatException nfe) {
			return false;
		}
		return true;
	}

	private static void validateRecursive(
			String rootLaunchName,
			String firstLevelChildLaunchName,
			List<LaunchConfigurationModel> launchConfigurationDataList) throws CoreException {
		for (LaunchConfigurationModel launchConfigurationModel : launchConfigurationDataList) {
			if (rootLaunchName.equals(launchConfigurationModel.getName())) {
				throw new LaunchValidationException(MessageFormat
						.format(LaunchMessages.LaunchGroupConfigurationDelegate_Loop, firstLevelChildLaunchName == null ? launchConfigurationModel.getName() : firstLevelChildLaunchName));
			}

			ILaunchConfiguration childLaunchConfiguration = LaunchUtils
					.findLaunchConfiguration(launchConfigurationModel.getName());
			if (childLaunchConfiguration != null) {
				List<LaunchConfigurationModel> childLaunchConfigurationModels = LaunchUtils
						.loadLaunchConfigurations(childLaunchConfiguration);
				String childLaunchName = launchConfigurationModel.getName();

				validateRecursive(
						rootLaunchName,
						firstLevelChildLaunchName == null ? childLaunchName : firstLevelChildLaunchName,
						childLaunchConfigurationModels);
			}
			else {
				//invalid launch-reference
				if (firstLevelChildLaunchName == null) {
					throw new LaunchValidationException(MessageFormat.format(
							LaunchMessages.LaunchGroupConfiguration_NotFound,
							launchConfigurationModel.getName()));
				}
				else {
					throw new LaunchValidationException(
							MessageFormat.format(
									LaunchMessages.LaunchGroupConfiguration_RecursiveNotFound,
									firstLevelChildLaunchName,
									launchConfigurationModel.getName()));
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
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/full/obj16/lrun_obj.gif").createImage();
	}
}
