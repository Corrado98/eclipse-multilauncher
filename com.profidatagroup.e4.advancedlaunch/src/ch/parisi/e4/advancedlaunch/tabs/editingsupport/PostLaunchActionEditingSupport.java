package ch.parisi.e4.advancedlaunch.tabs.editingsupport;

import java.util.Arrays;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ComboBoxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.TableViewer;

import ch.parisi.e4.advancedlaunch.LaunchConfigurationModel;
import ch.parisi.e4.advancedlaunch.utils.PostLaunchActionUtils;

public class PostLaunchActionEditingSupport extends EditingSupport {

	private final TableViewer tableViewer;

	public PostLaunchActionEditingSupport(TableViewer tableViewer) {
		super(tableViewer);
		this.tableViewer = tableViewer;
	}

	@Override
	protected CellEditor getCellEditor(Object element) {
		return new ComboBoxCellEditor(tableViewer.getTable(), PostLaunchActionUtils.getPostLaunchActionNames());
	}

	@Override
	protected boolean canEdit(Object element) {
		return true;
	}

	@Override
	protected Object getValue(Object element) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		return Arrays.asList(PostLaunchActionUtils.getPostLaunchActionNames()).indexOf(PostLaunchActionUtils.convertToName(launchConfigurationModel.getPostLaunchAction()));
	}

	@Override
	protected void setValue(Object element, Object value) {
		LaunchConfigurationModel launchConfigurationModel = (LaunchConfigurationModel) element;
		launchConfigurationModel.setPostLaunchAction(PostLaunchActionUtils.convertToPostLaunchAction(
				Arrays.asList(PostLaunchActionUtils.getPostLaunchActionNames()).get((int) value)));
		
		tableViewer.update(launchConfigurationModel, null);
	}
}
