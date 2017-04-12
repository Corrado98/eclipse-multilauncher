package ch.parisi.e4.advancedlaunch.tabs.editingsupport;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import ch.parisi.e4.advancedlaunch.LaunchConfigurationModel;
import ch.parisi.e4.advancedlaunch.tabs.LaunchTab;

/**
 * The {@link AbortOnErrorEditingSupport} for the CellEditor in the {@link LaunchTab}'s {@code TableViewer}.
 */
public class AbortOnErrorEditingSupport extends EditingSupport {

	private final TableViewer tableViewer;

	/**
	 * Constructs a {@link AbortOnErrorEditingSupport}.
	 * 
	 * @param tableViewer the table viewer
	 */
	public AbortOnErrorEditingSupport(TableViewer tableViewer) {
		super(tableViewer);
		this.tableViewer = tableViewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new CheckboxCellEditor(tableViewer.getTable());
	}

	@Override
	protected boolean canEdit(Object element) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		switch (launchConfigurationModel.getPostLaunchAction()) {
			case DELAY:
			case WAIT_FOR_TERMINATION:
			case WAIT_FOR_CONSOLE_REGEX:
			case WAIT_FOR_CONSOLE_TEXT:
				return true;
			case NONE:
			case WAIT_FOR_DIALOG:
				return false;
		}
		throw new IllegalArgumentException("Unknown action: " + launchConfigurationModel.getPostLaunchAction());
	}

	@Override
	protected Object getValue(Object element) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		return launchConfigurationModel.isAbortLaunchOnError();
	}

	@Override
	protected void setValue(Object element, Object value) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		launchConfigurationModel.setAbortLaunchOnError((boolean) value);
		tableViewer.update(launchConfigurationModel, null);
	}

}
