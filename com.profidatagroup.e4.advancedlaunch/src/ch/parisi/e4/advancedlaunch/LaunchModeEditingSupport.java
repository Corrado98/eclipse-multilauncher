package ch.parisi.e4.advancedlaunch;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

public class LaunchModeEditingSupport extends EditingSupport {

	private final TableViewer tableViewer;

	public LaunchModeEditingSupport(TableViewer tableViewer) {
		super(tableViewer);
		this.tableViewer = tableViewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		String[] launchModes = new String[2];
		launchModes[0] = ILaunchManager.RUN_MODE;
		launchModes[1] = ILaunchManager.DEBUG_MODE;

		return new ComboBoxCellEditor(tableViewer.getTable(), launchModes);
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		if (launchConfigurationModel.getMode().equals(ILaunchManager.RUN_MODE)) return 0;
		return 1;
	}

	@Override
	protected void setValue(Object element, Object value) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		if (((Integer) value) == 0) {
			launchConfigurationModel.setMode(ILaunchManager.RUN_MODE);
		} else {
			launchConfigurationModel.setMode(ILaunchManager.DEBUG_MODE);
		}
		tableViewer.update(element, null);
	}
}
