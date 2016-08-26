package com.profidatagroup.e4.advancedlaunch.dialogs;

import org.eclipse.cdt.launch.internal.ui.MultiLaunchConfigurationSelectionDialog;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class CustomDialog extends MultiLaunchConfigurationSelectionDialog {

	public CustomDialog(Shell shell, String initMode, boolean forEditing) {
		super(shell, initMode, forEditing);
	}
	
//	@Override
//	protected void okPressed() {
//	System.out.println("Ok pressed.");
//	
//	super.okPressed();
//	}
//	
//	@Override
//	protected void cancelPressed() {
//	System.out.println("Cancel pressed.");
//	super.cancelPressed();
//	}
	
}

//	public CustomDialog(Shell parentShell) {
//		super(parentShell);
//	
//		
//	}
//	
//	@Override
//	protected Control createDialogArea(Composite parent) {
//		Composite listBox = new Composite(parent, SWT.None);
//		Display display = Display.getCurrent();
//		Color blue = display.getSystemColor(SWT.COLOR_BLUE);
//		listBox.setSize(700,400);
//		listBox.setBackground(blue);
//		
//		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
//		ILaunchConfigurationType [] allTypes = manager.getLaunchConfigurationTypes();
//		ILaunchConfiguration [] configs;
//		
//		//print all root elements with its elements.
//		for (int i = 0; i < allTypes.length; i++) {
//			try {
//				System.out.println("Type: " + allTypes[i].getName());	
//				configs = manager.getLaunchConfigurations(allTypes[i]);
//				for (int j = 0; j < configs.length; j++) {
//					System.out.println("Elements: " + configs[j].getName());
//				}
//			} catch (CoreException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//			
//		
//		
//		return super.createDialogArea(parent);
//	}
	
	
	
	
	
	


	
//	public CustomDialog(Shell parentShell) {
//		super(parentShell);
//		getTitleImageLabel();
//		//setTitle("GESCHAFFFTTT???");
//		// TODO Auto-generated constructor stub
//	}

	//Control launchConfigSelectionArea = createLaunchConfigurationSelectionArea(getShell());
	
//	@Override
//	protected Control createContents(Composite parent) {		
//		return super.createContents(parent);
//	}
	
//	@Override
//	protected Label getTitleImageLabel() {
//		return new Label(getShell(), SWT.None).setText("saasdasd");
//	}
	
//	@Override
//	public void setTitle(String newTitle) {
//		super.setTitle(newTitle);
//	}


