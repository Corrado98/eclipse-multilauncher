package com.profidatagroup.e4.advancedlaunch.strategies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;

public class EmptyStrategy extends AbstractLaunchStrategy {

	@Override
	public void launchSelectedStrategy(ILaunchConfiguration iLaunchConfiguration, String mode, String param) {
		try {
			iLaunchConfiguration.launch(mode, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return;
	}

}
