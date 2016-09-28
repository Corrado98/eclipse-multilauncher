package ch.parisi.e4.advancedlaunch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import ch.parisi.e4.advancedlaunch.utils.LaunchUtils;

public class LaunchModeEditingSupport extends EditingSupport {

	private final TableViewer tableViewer;


	public LaunchModeEditingSupport(TableViewer tableViewer) {
		super(tableViewer);
		this.tableViewer = tableViewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		String[] launchModes = LaunchUtils.getAllowedModes();
		List<String> supportedModes = new ArrayList<>();
		for (int i = 0; i < launchModes.length; i++) {
			try {
				if(LaunchUtils.findLaunchConfiguration(launchConfigurationModel.getName()).supportsMode(launchModes[i])) {
					supportedModes.add(launchModes[i]);
				}
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
		return new ComboBoxCellEditor(tableViewer.getTable(), supportedModes.toArray(new String[0]));
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		String[] launchModes = LaunchUtils.getAllowedModes();
		for (int i = 0; i < launchModes.length; i++) {
			if (launchConfigurationModel.getMode().equals(launchModes[i])) return i;
		}
		return null;
	}

	@Override
	protected void setValue(Object element, Object value) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		String[] launchModes = LaunchUtils.getAllowedModes();
		for (int i = 0; i < launchModes.length; i++) {	
			if (((Integer) value) == i) {
				launchConfigurationModel.setMode(launchModes[i]);
			}
		}
		tableViewer.update(element, null);
	}
}
