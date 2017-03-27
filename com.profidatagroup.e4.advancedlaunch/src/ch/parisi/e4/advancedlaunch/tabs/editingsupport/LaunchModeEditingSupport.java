package ch.parisi.e4.advancedlaunch.tabs.editingsupport;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;

import ch.parisi.e4.advancedlaunch.LaunchConfigurationModel;
import ch.parisi.e4.advancedlaunch.tabs.LaunchTab;
import ch.parisi.e4.advancedlaunch.utils.LaunchUtils;

/**
 * The {@link LaunchModeEditingSupport} for the CellEditor in the {@link LaunchTab}'s {@code TableViewer}.
 */
public class LaunchModeEditingSupport extends EditingSupport {

	private final TableViewer tableViewer;

	/**
	 * Constructs a {@link LaunchModeEditingSupport}.
	 * 
	 * @param tableViewer the table viewer
	 */
	public LaunchModeEditingSupport(TableViewer tableViewer) {
		super(tableViewer);
		this.tableViewer = tableViewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		return new ComboBoxCellEditor(tableViewer.getTable(), LaunchUtils.getSupportedModes(launchConfigurationModel).toArray(new String[0]), SWT.READ_ONLY);
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		return LaunchUtils.getSupportedModes(launchConfigurationModel).indexOf(launchConfigurationModel.getMode());
	}

	@Override
	protected void setValue(Object element, Object value) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		List<String> supportedModes = LaunchUtils.getSupportedModes(launchConfigurationModel);
		launchConfigurationModel.setMode(supportedModes.get((int) value));
		tableViewer.update(launchConfigurationModel, null);
	}
}
