package ch.parisi.e4.advancedlaunch.tabs;
//package com.profidatagroup.e4.advancedlaunch.tabs;
//
//import org.eclipse.core.runtime.CoreException;
//import org.eclipse.debug.core.DebugPlugin;
//import org.eclipse.debug.core.ILaunchConfiguration;
//import org.eclipse.debug.core.ILaunchConfigurationType;
//import org.eclipse.debug.core.ILaunchManager;
//import org.eclipse.debug.internal.core.LaunchConfiguration;
//import org.eclipse.debug.ui.EnvironmentTab;
//import org.eclipse.jface.layout.AbstractColumnLayout;
//import org.eclipse.jface.layout.PixelConverter;
//import org.eclipse.jface.viewers.ColumnWeightData;
//import org.eclipse.jface.window.Window;
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.SelectionAdapter;
//import org.eclipse.swt.events.SelectionEvent;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.widgets.Composite;
//import org.eclipse.swt.widgets.Listener;
//import org.eclipse.swt.widgets.TableColumn;
//
//import com.profidatagroup.e4.advancedlaunch.dialogs.CustomDialog;
//
////UNUSED, WRONG APPROACH
//public class CustomEnvironmentTab extends EnvironmentTab {
//
//	public CustomEnvironmentTab() {
//		envTableColumnHeaders = new String[] { "Name", "Mode", "Action" };
//	}
//
//	@Override
//	public String getName() {
//		return "Launches";
//	}
//
//	@Override
//	public Image getImage() {
//		return null;
//	}
//
//	// @Override
//	// protected void handleEnvAddButtonSelected() {
//	// System.out.println("clicked on New ........!!!");
//	// }
//
//	@Override
//	protected void createEnvironmentTable(Composite parent) {
//		super.createEnvironmentTable(parent);
//		final TableColumn tc3 = new TableColumn(environmentTable.getTable(), SWT.NONE, 2);
//		tc3.setText(envTableColumnHeaders[2]);
//		((AbstractColumnLayout) environmentTable.getControl().getParent().getLayout()).setColumnData(tc3,
//				new ColumnWeightData(3, new PixelConverter(parent.getFont()).convertWidthInCharsToPixels(
//						20) /*
//							 * 200
//							 * pixelConverter.convertWidthInCharsToPixels(20)
//							 */));
//	}
//
//	@Override
//	protected void createTableButtons(Composite parent) {
//		super.createTableButtons(parent);
//		Listener[] listeners = envSelectButton.getListeners(SWT.Selection);
//		envSelectButton.removeListener(SWT.Selection, listeners[0]);
//		envSelectButton.addSelectionListener(new SelectionAdapter() {
//			@Override
//			public void widgetSelected(SelectionEvent event) {
//				// "Select.." pressed
//
//				CustomDialog cd = new CustomDialog(getShell(), "debug", false);
//				if (cd.open() == Window.OK) {
//					for (ILaunchConfiguration a : cd.getSelectedLaunchConfigurations()) {
//						System.out.println(a.getName());
//					}
//				}
//
//			}
//		});
//
//	}
//
//}
