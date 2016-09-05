package com.profidatagroup.e4.advancedlaunch.strategies;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

public class EmptyStrategy extends AbstractLaunchStrategy {

	@Override
	public void launchSelectedStrategy(ILaunchConfiguration iLaunchConfiguration, String mode, String param) {
		try {
			iLaunchConfiguration.launch(mode, null);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
