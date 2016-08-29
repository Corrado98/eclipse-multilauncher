package com.profidatagroup.e4.advancedlaunch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.ILaunchConfigurationDelegate;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.ui.DebugUITools;

import com.profidatagroup.e4.advancedlaunch.strategies.DelayStrategy;
import com.profidatagroup.e4.advancedlaunch.strategies.EmptyStrategy;
import com.profidatagroup.e4.advancedlaunch.strategies.ReadConsoleTextStrategy;
import com.profidatagroup.e4.advancedlaunch.strategies.WaitUntilPriorConfigTerminatedStrategy;

public class LaunchGroupConfigurationDelegate implements ILaunchConfigurationDelegate {

	List<Set<IProcess>> processesToWait = Collections.synchronizedList(new ArrayList<>()); 
	String selectedPostLaunchAction; // = ComboBox.getSelectedIndex().getText()
	
	public LaunchGroupConfigurationDelegate() {
		//HARDCODED ATM
		selectedPostLaunchAction = "Delay"; // = ComboBox.getSelectedIndex().getText()
	}

	@Override
	public void launch(ILaunchConfiguration configuration, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		
		//get all configs and check their attributes
		
		switch(selectedPostLaunchAction) {
		case "Wait until prior config Terminated": 
			new WaitUntilPriorConfigTerminatedStrategy().launch();
			break;
			
		case "Delay": //HARDCODED 4s ATM
			new DelayStrategy().launch();
			break;
			
		case "Read Console-Text": 
			new ReadConsoleTextStrategy().launch();
			break;
			
		case "None":
			new EmptyStrategy().launch();
			break;
			
		}

	}
}
