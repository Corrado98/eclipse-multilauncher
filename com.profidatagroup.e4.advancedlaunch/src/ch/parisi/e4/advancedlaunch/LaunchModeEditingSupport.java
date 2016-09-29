package ch.parisi.e4.advancedlaunch;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import ch.parisi.e4.advancedlaunch.utils.LaunchUtils;

public class LaunchModeEditingSupport extends EditingSupport {

	private final TableViewer tableViewer;
	int counter = 0;
	int b = -11;

	public LaunchModeEditingSupport(TableViewer tableViewer) {
		super(tableViewer);
		this.tableViewer = tableViewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		return new ComboBoxCellEditor(tableViewer.getTable(), getSupportedModes(launchConfigurationModel).toArray(new String[0]));
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		return getSupportedModes(launchConfigurationModel).indexOf(launchConfigurationModel.getMode());
	}

	@Override
	protected void setValue(Object element, Object value) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		List<String> supportedModes = getSupportedModes(launchConfigurationModel);
		launchConfigurationModel.setMode(supportedModes.get((int) value));		
		tableViewer.update(launchConfigurationModel, null);
	}
	
	private static List<String> getSupportedModes(LaunchConfigurationModel launchConfigurationModel) {
		List<String> supportedModes = new ArrayList<>();

		try {
			ILaunchConfiguration launchConfiguration = LaunchUtils.findLaunchConfiguration(launchConfigurationModel.getName());
			String[] launchModes = LaunchUtils.getAllowedModes();
			for (String launchMode : launchModes) {
				if (launchConfiguration.supportsMode(launchMode)) {
					supportedModes.add(launchMode);
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		return supportedModes;
	}
}
