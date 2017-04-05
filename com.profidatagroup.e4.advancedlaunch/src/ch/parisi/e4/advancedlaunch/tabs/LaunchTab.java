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
import org.eclipse.debug.core.ILaunchManager;
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
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import ch.parisi.e4.advancedlaunch.Activator;
import ch.parisi.e4.advancedlaunch.LaunchConfigurationModel;
import ch.parisi.e4.advancedlaunch.dialog.MultiLaunchConfigurationSelectionDialog;
import ch.parisi.e4.advancedlaunch.messages.LaunchMessages;
import ch.parisi.e4.advancedlaunch.tabs.editingsupport.AbortOnErrorEditingSupport;
import ch.parisi.e4.advancedlaunch.tabs.editingsupport.LaunchModeEditingSupport;
import ch.parisi.e4.advancedlaunch.tabs.editingsupport.ParamEditingSupport;
import ch.parisi.e4.advancedlaunch.tabs.editingsupport.PostLaunchActionEditingSupport;
import ch.parisi.e4.advancedlaunch.utils.DatabindingProperties;
import ch.parisi.e4.advancedlaunch.utils.LaunchUtils;
import ch.parisi.e4.advancedlaunch.utils.MultilauncherConfigurationAttributes;
import ch.parisi.e4.advancedlaunch.utils.PostLaunchAction;
import ch.parisi.e4.advancedlaunch.utils.PostLaunchActionUtils;

/**
 * The LaunchConfiguration Tab to edit the multilaunch configuration.
 */
public class LaunchTab extends AbstractLaunchConfigurationTab {

	private Button addButton;
	private Button removeButton;
	private Button upButton;
	private Button downButton;
	private Button editButton;
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
				case DatabindingProperties.MODE_PROPERTY:
					setDirty(true);
					updateDirtyModel();
					break;

				case DatabindingProperties.POST_LAUNCH_ACTION_PROPERTY:
					LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) evt.getSource();
					launchConfigurationModel.setParam("");

					PostLaunchAction postLaunchAction = launchConfigurationModel.getPostLaunchAction();
					if (postLaunchAction == PostLaunchAction.NONE || postLaunchAction == PostLaunchAction.WAIT_FOR_DIALOG) {
						launchConfigurationModel.setAbortLaunchOnError(false);
					}

					setDirty(true);
					updateLaunchConfigurationDialog();
					break;

				case DatabindingProperties.PARAM_PROPERTY:
					setDirty(true);
					updateLaunchConfigurationDialog();
					break;

				case DatabindingProperties.ABORT_LAUNCH_ON_ERROR_PROPERTY:
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
		initAddButtonWithListener();
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

	private void initAddButtonWithListener() {
		addButton = new Button(buttonComposite, SWT.None);
		addButton.setText(LaunchMessages.LaunchGroupConfiguration_Add);
		addButton.addListener(SWT.Selection, new Listener() {
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
					multiLaunchConfigurationSelectionDialog.getPostLaunchAction(),
					String.valueOf(multiLaunchConfigurationSelectionDialog.getParam()),
					multiLaunchConfigurationSelectionDialog.isAbortLaunchOnError());
			launchConfigurationModel.addPropertyChangeListener(DatabindingProperties.MODE_PROPERTY, propertyChangeListener);
			launchConfigurationModel.addPropertyChangeListener(DatabindingProperties.POST_LAUNCH_ACTION_PROPERTY, propertyChangeListener);
			launchConfigurationModel.addPropertyChangeListener(DatabindingProperties.PARAM_PROPERTY, propertyChangeListener);
			launchConfigurationModel.addPropertyChangeListener(DatabindingProperties.ABORT_LAUNCH_ON_ERROR_PROPERTY, propertyChangeListener);
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
		multiLaunchConfigurationSelectionDialog.setMode(ILaunchManager.DEBUG_MODE);
		multiLaunchConfigurationSelectionDialog.setPostLaunchAction(PostLaunchAction.NONE);
		multiLaunchConfigurationSelectionDialog.setParam("");
	}

	private void initEditButtonWithListener() {
		editButton = new Button(buttonComposite, SWT.None);
		editButton.setText(LaunchMessages.LaunchGroupConfiguration_Edit);
		editButton.setEnabled(false);
		editButton.addListener(SWT.Selection, new Listener() {
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
					multiLaunchConfigurationSelectionDialog.getPostLaunchAction(),
					String.valueOf(multiLaunchConfigurationSelectionDialog.getParam()),
					multiLaunchConfigurationSelectionDialog.isAbortLaunchOnError());
			launchConfigurationModel.addPropertyChangeListener(DatabindingProperties.MODE_PROPERTY, propertyChangeListener);
			launchConfigurationModel.addPropertyChangeListener(DatabindingProperties.POST_LAUNCH_ACTION_PROPERTY, propertyChangeListener);
			launchConfigurationModel.addPropertyChangeListener(DatabindingProperties.PARAM_PROPERTY, propertyChangeListener);
			launchConfigurationModel.addPropertyChangeListener(DatabindingProperties.ABORT_LAUNCH_ON_ERROR_PROPERTY, propertyChangeListener);
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
		multiLaunchConfigurationSelectionDialog.setPostLaunchAction(selectedConfiguration.getPostLaunchAction());
		multiLaunchConfigurationSelectionDialog.setParam(selectedConfiguration.getParam());
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
					removeButton.setEnabled(hasSelection);
					upButton.setEnabled(hasSelection);
					downButton.setEnabled(hasSelection);
					editButton.setEnabled(hasSelection);
				}
			}
		});

	}

	private void initRemoveButtonWithListener() {
		removeButton = new Button(buttonComposite, SWT.None);
		removeButton.setText(LaunchMessages.LaunchGroupConfiguration_Remove);
		removeButton.setEnabled(false);
		removeButton.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (selectedConfiguration != null && launchConfigurationDataList != null) {
					launchConfigurationDataList.remove(selectedConfiguration);
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
		upButton = new Button(buttonComposite, SWT.None);
		upButton.setText(LaunchMessages.LaunchGroupConfiguration_Up);
		upButton.setEnabled(false);
		upButton.addListener(SWT.Selection, new Listener() {

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
		downButton = new Button(buttonComposite, SWT.None);
		downButton.setText(LaunchMessages.LaunchGroupConfiguration_Down);
		downButton.setEnabled(false);
		downButton.addListener(SWT.Selection, new Listener() {

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
		addTableColumn(LaunchMessages.LaunchGroupConfiguration_Column_Name, 175, launchConfigurationModel -> launchConfigurationModel.getName());

		// create a column for the launchconfiguration MODE
		addTableColumn(LaunchMessages.LaunchGroupConfiguration_Column_Mode, 175, launchConfigurationModel -> launchConfigurationModel.getMode());

		// create a column for the launchconfiguration POST-ACTION
		addTableColumn(LaunchMessages.LaunchGroupConfiguration_Column_Action, 175, launchConfigurationModel -> PostLaunchActionUtils
				.convertToName(launchConfigurationModel.getPostLaunchAction()));

		// create a column for the launchconfiguration PARAM
		addTableColumn(LaunchMessages.LaunchGroupConfiguration_Column_Param, 120, launchConfigurationModel -> launchConfigurationModel.getParam());

		// create a column for the launchconfiguration ABORT-LAUNCH-ON-ERROR
		addTableColumn(LaunchMessages.LaunchGroupConfiguration_Column_Abort, 50, launchConfigurationModel -> "");

	}

	private void addTableColumn(String columnName, int width, Function<LaunchConfigurationModel, String> actionTextFetcher) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(tableViewer, SWT.NONE);
		tableViewerColumn.getColumn().setWidth(width);
		tableViewerColumn.getColumn().setText(columnName);

		initEditingSupport(columnName, tableViewerColumn);

		tableViewerColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				LaunchConfigurationModel model = (LaunchConfigurationModel) element;
				return actionTextFetcher.apply(model);
			}

			@Override
			public Image getImage(Object element) {
				if (columnName.equals(LaunchMessages.LaunchGroupConfiguration_Column_Abort)) {
					if (((LaunchConfigurationModel) element).isAbortLaunchOnError()) {
						return resize(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/full/obj16/checked.png").createImage(), 10, 10);
					}
					else {
						return resize(AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/full/obj16/unchecked.png").createImage(), 10, 10);
					}
				}
				return null;
			}

			private Image resize(Image image, int toWidth, int toHeight) {
				Image scaledImage = new Image(Display.getDefault(), toWidth, toHeight);
				GC gc = new GC(scaledImage);
				gc.setAntialias(SWT.ON);
				gc.setInterpolation(SWT.HIGH);
				gc.drawImage(
						image,
						0,
						0,
						image.getBounds().width,
						image.getBounds().height,
						0,
						0,
						toWidth,
						toHeight);
				gc.dispose();
				image.dispose();
				return scaledImage;
			}
		});
	}

	private void initEditingSupport(String columnName, TableViewerColumn tableViewerColumn) {
		if (columnName.equals(LaunchMessages.LaunchGroupConfiguration_Column_Mode)) {
			tableViewerColumn.setEditingSupport(new LaunchModeEditingSupport(tableViewer));
		}
		else if (columnName.equals(LaunchMessages.LaunchGroupConfiguration_Column_Action)) {
			tableViewerColumn.setEditingSupport(new PostLaunchActionEditingSupport(tableViewer));
		}
		else if (columnName.equals(LaunchMessages.LaunchGroupConfiguration_Column_Param)) {
			tableViewerColumn.setEditingSupport(new ParamEditingSupport(tableViewer));
		}
		else if (columnName.equals(LaunchMessages.LaunchGroupConfiguration_Column_Abort)) {
			tableViewerColumn.setEditingSupport(new AbortOnErrorEditingSupport(tableViewer));
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
				launchConfigurationModel.addPropertyChangeListener(DatabindingProperties.MODE_PROPERTY, propertyChangeListener);
				launchConfigurationModel.addPropertyChangeListener(DatabindingProperties.POST_LAUNCH_ACTION_PROPERTY, propertyChangeListener);
				launchConfigurationModel.addPropertyChangeListener(DatabindingProperties.PARAM_PROPERTY, propertyChangeListener);
				launchConfigurationModel.addPropertyChangeListener(DatabindingProperties.ABORT_LAUNCH_ON_ERROR_PROPERTY, propertyChangeListener);
			}
			launchConfigurationDataList = loadLaunchConfigurations;
			tableViewer.setInput(launchConfigurationDataList);
			tableViewer.refresh();
			promptBeforeLaunchCheckbox.setSelection(configuration.getAttribute(MultilauncherConfigurationAttributes.PROMPT_BEFORE_LAUNCH_ATTRIBUTE, false));
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
		List<String> abortLaunchesOnError = new ArrayList<>();

		for (LaunchConfigurationModel launchConfigurationModel : launchConfigurationDataList) {
			names.add(launchConfigurationModel.getName());
			modes.add(launchConfigurationModel.getMode());
			postLaunchActions.add(PostLaunchActionUtils.convertToName(launchConfigurationModel.getPostLaunchAction()));
			params.add(launchConfigurationModel.getParam());
			abortLaunchesOnError.add(String.valueOf(launchConfigurationModel.isAbortLaunchOnError()));
		}

		configuration.setAttribute(MultilauncherConfigurationAttributes.CHILDLAUNCH_NAMES_ATTRIBUTE, names);
		configuration.setAttribute(MultilauncherConfigurationAttributes.CHILDLAUNCH_MODES_ATTRIBUTE, modes);
		configuration.setAttribute(MultilauncherConfigurationAttributes.CHILDLAUNCH_POST_LAUNCH_ACTIONS_ATTRIBUTE, postLaunchActions);
		configuration.setAttribute(MultilauncherConfigurationAttributes.CHILDLAUNCH_PARAMS_ATTRIBUTE, params);
		configuration.setAttribute(MultilauncherConfigurationAttributes.CHILDLAUNCH_ABORT_LAUNCHES_ON_ERROR_ATTRIBUTE, abortLaunchesOnError);

		configuration.setAttribute(MultilauncherConfigurationAttributes.PROMPT_BEFORE_LAUNCH_ATTRIBUTE, promptBeforeLaunchCheckbox.getSelection());
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
		private static final long serialVersionUID = 1164646082952205965L;

		public LaunchValidationException(String message) {
			super(message);
		}
	}

	@Override
	public Image getImage() {
		return AbstractUIPlugin.imageDescriptorFromPlugin(Activator.PLUGIN_ID, "icons/full/obj16/lrun_obj.gif").createImage();
	}
}
