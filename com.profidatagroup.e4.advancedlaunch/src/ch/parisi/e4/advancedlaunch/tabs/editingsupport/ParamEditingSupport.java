package ch.parisi.e4.advancedlaunch.tabs.editingsupport;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import ch.parisi.e4.advancedlaunch.LaunchConfigurationModel;
import ch.parisi.e4.advancedlaunch.tabs.LaunchTab;

/**
 * The {@link ParamEditingSupport} for the CellEditor in the {@link LaunchTab}'s {@code TableViewer}.
 */
public class ParamEditingSupport extends EditingSupport {

	private final TableViewer tableViewer;

	/**
	 * Constructs a {@link ParamEditingSupport}.
	 * 
	 * @param tableViewer the table viewer
	 */
	public ParamEditingSupport(TableViewer tableViewer) {
		super(tableViewer);
		this.tableViewer = tableViewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new TextCellEditor(tableViewer.getTable());
	}

	@Override
	protected boolean canEdit(Object element) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		switch (launchConfigurationModel.getPostLaunchAction()) {
			case DELAY:
			case WAIT_FOR_CONSOLESTRING:
				return true;
			case NONE:
			case WAIT_FOR_TERMINATION:
				return false;
		}
		throw new IllegalArgumentException("Unknown action: " + launchConfigurationModel.getPostLaunchAction());
	}

	@Override
	protected Object getValue(Object element) {
		return ((LaunchConfigurationModel) element).getParam();
	}

	@Override
	protected void setValue(Object element, Object value) {
		((LaunchConfigurationModel) element).setParam(String.valueOf(value));

		tableViewer.update(element, null);
	}

}
