package com.profidatagroup.e4.advancedlaunch.strategies;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;

public class DelayStrategy extends AbstractLaunchStrategy {

	@Override
	public void launchSelectedStrategy(ILaunchConfiguration iLaunchConfiguration, String mode, String param) {
		try {
			iLaunchConfiguration.launch(mode, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
		waitDelay(Integer.parseInt(param));
	}

	private void waitDelay(int seconds) {
		try {
			Thread.sleep(seconds * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
