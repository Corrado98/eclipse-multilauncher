package ch.parisi.e4.advancedlaunch.tabs.editingsupport;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;

import ch.parisi.e4.advancedlaunch.LaunchConfigurationModel;
import ch.parisi.e4.advancedlaunch.utils.PostLaunchAction;

public class ParamEditingSupport extends EditingSupport {

	private final TableViewer tableViewer;

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
		if(launchConfigurationModel.getPostLaunchAction().equals(PostLaunchAction.NONE) || launchConfigurationModel.getPostLaunchAction().equals(PostLaunchAction.WAIT_FOR_TERMINATION)) {
			return false;
		}	
		return true;
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
