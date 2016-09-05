package com.profidatagroup.e4.advancedlaunch.strategies;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

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
