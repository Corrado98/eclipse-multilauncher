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
	
}


