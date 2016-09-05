package com.profidatagroup.e4.advancedlaunch.strategies;

import org.eclipse.debug.core.ILaunchConfiguration;

public abstract class AbstractLaunchStrategy {

	/**
	 * Launches the specified {@link ILaunchConfiguration} in the given <code>mode</code> and waits until the conditions of this strategy are met.
	 * 
	 * This method will only return when conditions of this strategy are met.
	 * 
	 * @param launchConfiguration the {@link ILaunchConfiguration}
	 * @param mode the launch mode, see {@link org.eclipse.debug.core.ILaunchManager}
	 * @param param the runtime parameter for this strategy (e.g. delay time)
	 */
	protected abstract void launchSelectedStrategy(ILaunchConfiguration launchConfiguration, String mode, String param);

}
